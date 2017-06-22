package com.relecotech.androidsparsh.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import com.relecotech.androidsparsh.R;

/**
 * Created by ajinkya on 10/20/2015.
 */
public class ForgotPassword extends Activity {

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    EditText forgotText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xp);


        // alert = new AlertDialogManager();

    }

//    public void resetforgot(View view) {
//        String email = forgotText.getText().toString();
//        if (email.length() > 0) {
//            if (email.matches(emailPattern)) {
//
//                forgotpassworddialog.setMessage("Check your email");
//                forgotpassworddialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                forgotpassworddialog.create().show();
//            } else {
//                //alert.showAlertDialog(ForgotPassword.this, "Invalid email Id", "Please check your email Id", false);
//                forgotText.setError("Please check your email Id");
//
//            }
//        } else {
//
//            Snackbar snackbar = Snackbar.make(view, "Provide email Id", Snackbar.LENGTH_LONG);
//            View snackbarView = snackbar.getView();
//            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
//            textView.setTextColor(Color.WHITE);
//            snackbar.show();
//        }
//    }
}
