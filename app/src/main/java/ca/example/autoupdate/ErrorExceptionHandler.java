    package ca.example.autoupdate;

    import android.app.Dialog;
    import android.content.Context;
    import android.content.SharedPreferences;
    import android.view.Gravity;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.Window;
    import android.view.WindowManager;
    import android.widget.Button;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import static ca.example.autoupdate.App.MyPREFERENCES;


    public class ErrorExceptionHandler {
        private SharedPreferences sharedPref;
        private SharedPreferences.Editor editor;
        private ErrorExceptionHandler()
        {

        }

        public static ErrorExceptionHandler with()
        {
            return new ErrorExceptionHandler();
        }




        public void handleException(final Context context, String exception)
        {
            sharedPref = context.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
            sharedPref = context.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            editor.putString("exception", exception);
            editor.apply();

            String title= "Exception Returned";
           if(!SendNotifications.init(context).isAppIsInBackground()) {
               showCustomDialog(context, exception, title);
           }
           else
           {

               SendNotifications.init(context).sendExceptionNotification();
           }

        }




        private void showCustomDialog(final Context context,String exception,String title) {
            Dialog dialog = new Dialog(context,R.style.NewDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(inflater!=null) {
                View convertView = inflater.inflate(R.layout.customdialog, null, false);
                if (convertView != null)
                {
                    Button yesButton = convertView.findViewById(R.id.yes_button);
                    Button noButton = convertView.findViewById(R.id.no_button);
                    TextView titleTextView = convertView.findViewById(R.id.titleTWId);
                    TextView messageTextView = convertView.findViewById(R.id.messageTWId);
                    yesButton.setOnClickListener(yesButtonClick(dialog));
                    noButton.setVisibility(View.GONE);

                    titleTextView.setText(title);
                    messageTextView.setText(exception);
                    dialog.setContentView(convertView);
                    final Window window = dialog.getWindow();
                    if (window != null)
                    {
                        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawableResource(R.color.trans);
                        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        window.setGravity(Gravity.CENTER);
                    }
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);

                    dialog.show();
                }
            }
        }


        public View.OnClickListener yesButtonClick(final Dialog dialog) {
            return new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                   App.get().setWeekly().scheduleJob();
                   dialog.dismiss();
                }

            };
        }





    }
