    package ca.example.autoupdate;

    import android.app.ActivityManager;
    import android.app.NotificationChannel;
    import android.app.NotificationManager;
    import android.app.PendingIntent;
    import android.app.TaskStackBuilder;
    import android.content.ComponentName;
    import android.content.Context;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.os.Build;
    import android.support.v4.app.NotificationCompat;

    import java.util.List;

    import static android.content.Context.NOTIFICATION_SERVICE;
    import static ca.example.autoupdate.App.MyPREFERENCES;


    public class SendNotifications {
        private Context context;

        private String CHANNEL_ID = "3";
        private SharedPreferences sharedPref;
        private SharedPreferences.Editor editor;
        private SendNotifications(Context context)
        {
            this.context=context;
            sharedPref = context.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
            editor = sharedPref.edit();
        }

        public static SendNotifications init(Context context)
        {
            return new SendNotifications( context);
        }

        public void sendUpdateNotification()
        {
            Intent intent = new Intent(context, ResultActivity.class);

            editor.putString("notificationType", "update");
            editor.commit();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            createNotificationChannel();


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("AutoUpdate")
                    .setContentText("This app is trying to check for update but there an has prevented it. Click for more details")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("This app is trying to check for update but there an has prevented it. Click for more details"))

                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setDeleteIntent(getDeleteIntent());

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(3, builder.build());


        }





        private void createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "test channel";
                String description = "to test app self update";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }


        public void sendExceptionNotification()
        {
            editor.putString("notificationType", "exception");
            editor.commit();
            Intent intent = new Intent(context, ResultActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(intent);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            createNotificationChannel();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                  .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("AutoUpdate")
                    .setContentText("This app is trying to check for update but there an has prevented it. Click for more details")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("This app is trying to check for update but there an has prevented it. Click for more details"))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setDeleteIntent(getDeleteIntent());

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(NOTIFICATION_SERVICE);

            notificationManager.cancel(3);
            notificationManager.notify(3, builder.build());


        }
        protected PendingIntent getDeleteIntent()
        {
            Intent intent = new Intent(context, OnNotificationCancelledBroadcastReceiver.class);
            intent.setAction("notification_cancelled");
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        public boolean isAppIsInBackground() {
            boolean isInBackground = true;
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(context.getPackageName())) {
                    isInBackground = false;
                }
            }
            return isInBackground;
        }

    }
