    package ca.example.autoupdate;


    import android.app.Activity;
    import android.app.Application;
    import android.app.job.JobInfo;
    import android.app.job.JobScheduler;
    import android.arch.lifecycle.LifecycleObserver;
    import android.content.ComponentName;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.SharedPreferences;
    import android.content.pm.PackageManager;
    import android.os.Build;
    import android.support.v4.app.ActivityCompat;
    import android.support.v7.app.AlertDialog;


    public class App extends Application implements LifecycleObserver {
        private static String[]permissions;
        private static  final int JOB_ID=10;
        private static final int  MY_PERMISSIONS_REQUEST_CODE = 100;
        private static App instance;
     private JobScheduler jobScheduler;
     private JobInfo jobInfo;
     public static final String MyPREFERENCES = "MyPrefs" ;
        private SharedPreferences sharedPref;
        private SharedPreferences.Editor editor;
        private static int delayDays;

        @Override
        public void onCreate() {
            super.onCreate();
            instance=this;
            setUp();





        }

        public void setUp()
        {
            delayDays =60*24*7;
            sharedPref = getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            editor.putString("appUrl", "http://naijaconnectsapis.ca/appupdate/app-debug.apk");
           editor.putInt("hourly",60);
            editor.putInt("Daily",60*24);
            editor.putInt("Weekly",60*24*7);
            editor.putInt("Monthly",60*24*7*4);
            editor.putInt("Yearly",60*24*7*4*12);
            editor.apply();
            scheduleJob();
        }

        public static App get() {
            return instance;
        }


        public void scheduleJob()
        {
            AppDownloadManager.downloadCompleted=false;
            ComponentName componentName  = new ComponentName(this, SelfUpdateJobScheduler.class);
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID,componentName);
            builder.setPeriodic(delayDays*60*1000);
            builder.setPersisted(true);
            jobInfo = builder.build();
            jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);

            jobScheduler.schedule(jobInfo);
        }


        private static boolean hasPermission(Activity activity, String... permissions)
        {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && activity!=null && permissions!=null)
            {
                for(String permission:permissions)
                {
                    if(ActivityCompat.checkSelfPermission(activity,permission)!= PackageManager.PERMISSION_GRANTED)
                    {
                        return false;
                    }
                }
            }
            return true;
        }




        //for testing purpose
        // found in ResultActivity (Methods: startAppUpdate(), OnCreate(), updateApp())
        // found in ErrorExceptionHandler (Method: yesButtonClick)
        // found in OnNotificationCancelledBroadcastReceiver
        // found in UpdateApp
        //call this method if you want to check for update every week
        //call this method if you want to check for update every week
        public App setWeekly()
        {
            delayDays = sharedPref.getInt("Weekly",0);
            return instance;
        }

        //call this method if you want to check for update every hour
        public App setHourly()
        {
            delayDays = sharedPref.getInt("hourly",0);
            return instance;
        }

        //call this method if you want to check for update every month
        public App setMonthly()
        {
            delayDays = sharedPref.getInt("Monthly",0);
            return instance;
        }

        //call this method if you want to check for update every day
        public App setDaily()
        {
            delayDays = sharedPref.getInt("Daily",0);
            return instance;
        }


        //call this method if you want to check for update every year
        public App setYearly()
        {
            delayDays = sharedPref.getInt("Yearly",0);
            return instance;
        }

        public void askPermission(final Activity activity)
        {

            permissions=new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

             if(!hasPermission(activity, permissions))
            {

                if( ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    new AlertDialog.Builder(activity)
                            .setTitle("Permission needed")
                            .setMessage("This permission is needed")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(activity,permissions,
                                            MY_PERMISSIONS_REQUEST_CODE);



                                }
                            }).create().show();
                }
                else
                {
                    ActivityCompat.requestPermissions(activity,permissions,
                            MY_PERMISSIONS_REQUEST_CODE);
                }

            }





        }




    }




