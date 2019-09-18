    package ca.example.autoupdate;


    import android.content.ActivityNotFoundException;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.net.Uri;
    import android.os.Build;
    import android.os.Bundle;
   import android.support.v7.app.AlertDialog;
    import android.support.v7.app.AppCompatActivity;
    import android.util.Log;
    import android.view.View;
    import android.widget.Toast;
    import java.io.File;

    import static ca.example.autoupdate.App.MyPREFERENCES;


    public class ResultActivity extends AppCompatActivity {

    private Uri uri;

        private SharedPreferences sharedPref;
        private SharedPreferences.Editor sharedEditor;
        private String urlLink=null;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_notification);

           getSupportActionBar().hide();
           sharedPref = this.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
            sharedEditor = sharedPref.edit();
            sharedEditor.putBoolean("localInstallationAllow",false);
            sharedEditor.apply();

           String notificationType = sharedPref.getString("notificationType",null);
          if(notificationType!=null) {
              if (notificationType.equalsIgnoreCase("update")) {
                  urlLink = sharedPref.getString("urlLink", null);

                  if(urlLink!=null) {
                     File file = new File(urlLink);

                     if (Build.VERSION.SDK_INT < 24)
                     {
                         uri = Uri.fromFile(file);
                     } else {
                         uri = Uri.parse(file.getPath());
                     }

                     startAppUpdate();
                 }
              } else if (notificationType.equalsIgnoreCase("exception")) {
                  String exception = sharedPref.getString("exception", null);

                  AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("Error Notification")
                          .setMessage("AppUpdate could check for update or could be update due to this error - " + exception)
                          .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialogInterface, int i) {

                                 App.get().setWeekly().scheduleJob();

                                  Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                                  startActivity(intent);
                              }
                          }).create();

                  alertDialog.show();
              }
          }

        }
        private void startAppUpdate()
        {

            final AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("New Version Available")
                    .setMessage("New version now available. Please update to this newer version")
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            updateApp();

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                          App.get().setWeekly().scheduleJob();

                            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }).create();

            alertDialog.show();
        }





        public void updateApp() {

            try {

                try {
             Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                     intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent,1);


                } catch (ActivityNotFoundException e) {

                } catch (Exception ex) {
                    //catch exception
                }





//                App.get().byMinute().scheduleJob();


            } catch (Exception e)
            {
                e.printStackTrace();
            }


        }




        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data)
        {
            if (requestCode == 1)
            {

                 if (resultCode == RESULT_OK)
                 {


                    Toast.makeText(this, "User pressed 'Open' button", Toast.LENGTH_LONG).show();

                }
                else {
                 App.get().setWeekly().scheduleJob();
                    Toast.makeText(this, "You have choosen to cancel installation. Reminder will be sent you in 24hrs", Toast.LENGTH_LONG).show();
                }
            }
           // super.onActivityResult(requestCode, resultCode, data);
        }


    public void backToHome(View view)
    {

    }

        }



