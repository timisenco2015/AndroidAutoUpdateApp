    package ca.example.autoupdate;

    import android.app.job.JobParameters;
    import android.app.job.JobService;
    import android.content.Context;

    public class SelfUpdateJobScheduler extends JobService{
       private  JobExecutor jobExecutor;
        private Context context;


        @Override
        public boolean onStartJob(JobParameters jobParameters) {
            context= this.getApplicationContext();

           if(!AppDownloadManager.downloadCompleted)
           {
                    AppDownloadManager.init(context).startDownload();

           }

            jobFinished(jobParameters,false);
            return true;
        }

        @Override
        public boolean onStopJob(JobParameters jobParameters) {

            AppDownloadManager.downloadCompleted=false;
            return true;
        }
    }
