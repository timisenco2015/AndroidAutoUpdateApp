    package ca.example.autoupdate;


    import android.content.Context;

    import android.content.SharedPreferences;
    import android.content.pm.PackageInfo;
    import android.content.pm.PackageManager;
     import static ca.example.autoupdate.App.MyPREFERENCES;

    public class UpdateApp {

        private Context context;

        private SharedPreferences sharedPref;


        private String urlLink=null;
        private  UpdateApp(Context context)
        {
            this.context=context;


        }

        public static UpdateApp init(Context context)
        {
           return new UpdateApp(context);
        }

        private void confirmUpdate()
        {

            SendNotifications.init(context).sendUpdateNotification();
        }

        public void checkForUpdate() {

            final PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo;
            sharedPref = context.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
            urlLink = sharedPref.getString("urlLink", null);
            packageInfo = packageManager.getPackageArchiveInfo(urlLink, 0);
            String versionName = packageInfo.versionName;
            versionName = versionName.replaceAll("[a-zA-Z]|-", "");

            if (versionName.compareTo(getInstalledAppVersion()) > 0) {
                confirmUpdate();
            }
            else
            {
                App.get().setWeekly().scheduleJob();
            }

        }

        private String getInstalledAppVersion() {
            String result = "";
            try {
                result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;

                result = result.replaceAll("[a-zA-Z]|-", "");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return result;
        }


    }


