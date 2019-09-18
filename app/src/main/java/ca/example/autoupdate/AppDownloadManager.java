    package ca.example.autoupdate;

    import android.app.DownloadManager;
    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.Intent;
    import android.content.IntentFilter;
    import android.content.SharedPreferences;
    import android.database.Cursor;
    import android.net.Uri;
    import android.os.Environment;


    import java.io.File;

    import static ca.example.autoupdate.App.MyPREFERENCES;

    public class AppDownloadManager {
        private String appUrl;
        private Context context;
        private long downloadId;
        public static boolean downloadCompleted;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
        private AppDownloadManager(Context context)
        {
            downloadCompleted=false;
            this.context=context;


        }

        public static AppDownloadManager init(Context context)
        {
            return new AppDownloadManager(context);
        }


        public void startDownload()
        {
            sharedPref = context.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
            this.appUrl=sharedPref.getString("appUrl",null);
            Uri uri = Uri.parse(appUrl);

            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), BuildConfig.APPLICATION_ID);

            //if the storage folder for this app already exist. If not it will be created.
            if(file.exists())
             {
                 file.delete();
                 file.mkdirs();
             }


                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle("Apk Download");
                request.setDescription("Download app apk");
                request.setDestinationInExternalFilesDir(context,file.getAbsolutePath(),uri.getLastPathSegment());
                request.setMimeType(".apk");
               final DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
                downloadId = downloadManager.enqueue(request);




            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(getDownloadStatus()==DownloadManager.STATUS_SUCCESSFUL)
                        {
                           Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
                            sharedPref = context.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
                             editor = sharedPref.edit();
                            editor.putString("urlLink", uri.getPath());
                            editor.apply();
                            UpdateApp.init(context).checkForUpdate();
                        }
                        else if (getDownloadStatus()==DownloadManager.STATUS_FAILED)
                        {
                            int reason =  getDownloadFailedReason();
                            handleDownloadErrors(reason);
                        }
                    downloadCompleted = true;
                    }

            },new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


        }


        private void handleDownloadErrors(int reason)
        {
            String reasonText = "Null Error";

            switch (reason) {
                case DownloadManager.ERROR_CANNOT_RESUME:
                    reasonText = "ERROR_CANNOT_RESUME";
                    break;
                case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                    reasonText = "ERROR_DEVICE_NOT_FOUND";
                    break;
                case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                    reasonText = "ERROR_FILE_ALREADY_EXISTS";
                    break;
                case DownloadManager.ERROR_FILE_ERROR:
                    reasonText = "ERROR_FILE_ERROR";
                    break;
                case DownloadManager.ERROR_HTTP_DATA_ERROR:
                    reasonText = "ERROR_HTTP_DATA_ERROR";
                    break;
                case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                    reasonText = "ERROR_INSUFFICIENT_SPACE";
                    break;
                case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                    reasonText = "ERROR_TOO_MANY_REDIRECTS";
                    break;
                case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                    reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                    break;
                case DownloadManager.ERROR_UNKNOWN:
                    reasonText = "ERROR_UNKNOWN";
                    break;
            }

            ErrorExceptionHandler.with().handleException(context,reasonText);
        }

        private int getDownloadStatus()
        {
            int status=0;
            DownloadManager.Query query = new DownloadManager.Query();
            if(query!=null) {
                query.setFilterById(downloadId);

                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor cursor = downloadManager.query(query);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    status = cursor.getInt(columnIndex);


                }
                cursor.close();
            }
            return status;
        }

        private int getDownloadFailedReason()
        {
            int reason=0;

            DownloadManager.Query query = new DownloadManager.Query();
            if(query!=null) {
                query.setFilterById(downloadId);

                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor cursor = downloadManager.query(query);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {

                        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                        reason = cursor.getInt(columnReason);


                    }
                    cursor.close();
                }
            }
            return reason;
        }


    }
