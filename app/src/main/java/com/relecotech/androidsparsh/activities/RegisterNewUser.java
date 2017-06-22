package com.relecotech.androidsparsh.activities;

/**
 * Created by ajinkya on 10/20/2015.
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.relecotech.androidsparsh.AlertDialogManager;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.ServiceHandler;

/**
 * Created by ajinkya on 6/25/2015.
 */
public class RegisterNewUser extends ActionBarActivity {

    EditText firstname, lastname, editemail, editpassword, conPassword;

    Button signup;
    String url_prepared;
    String raw_url = "https://relecoschool-developer-edition.ap2.force.com/services/apexrest/student?name=";
    String restResponseString;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String namePattern = "[a-zA-Z]*";
    String role;

    AlertDialogManager signUpAlerts;
    RadioGroup roleRadioGroup;
    RadioButton roleRadioButton;
    ProgressBar registerPBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_new_user);
        getSupportActionBar().setTitle("Sign Up");

        firstname = (EditText) findViewById(R.id.firsteditText);
        lastname = (EditText) findViewById(R.id.lasteditText);
        editemail = (EditText) findViewById(R.id.emaileditText);
        editpassword = (EditText) findViewById(R.id.passwordeditText);
        conPassword = (EditText) findViewById(R.id.confirmpasseditText);

        signup = (Button) findViewById(R.id.signupbutton);
        roleRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        signUpAlerts = new AlertDialogManager();

    }

    public void SignUp(View view) {

        String fname = firstname.getText().toString();
        String lname = lastname.getText().toString();
        String email = editemail.getText().toString();
        String password = editpassword.getText().toString();
        String conPass = conPassword.getText().toString();

        int selectedId = roleRadioGroup.getCheckedRadioButtonId();
        roleRadioButton = (RadioButton) findViewById(selectedId);
        role = (String) roleRadioButton.getText();

        url_prepared = raw_url + fname + ";" + lname + ";" + email + ";" + password + ";" + role;

        Log.d("url", "" + url_prepared);
        //Toast.makeText(getApplicationContext(),"  "+url_prepared, Toast.LENGTH_LONG).show();

        //checking fields are empty or not
        if ((fname.length() > 0) && (lname.length() > 0) && (email.length() > 0) && (password.length() > 0) && (conPass.length() > 0)) {

            //checking first and last name contains only characters or not
            if (fname.matches(namePattern) && lname.matches(namePattern))

                //checking email valid or not
                if (email.matches(emailPattern)) {

                    //checking password and conpasssword match or not
                    if (password.equals(conPass)) {

                        if (password.length() >= 4) {
                            //all validating conditions clear
                            new Restcaliing().execute();
                        } else {
                            signUpAlerts.showAlertDialog(RegisterNewUser.this, "Password short", "password should be minimum 4 characters", false);
                        }

                    } else {
                        signUpAlerts.showAlertDialog(RegisterNewUser.this, "Password mismatch", "Re-enter password", false);
                    }
                } else {
                    signUpAlerts.showAlertDialog(RegisterNewUser.this, "oops...", "Invalid email address", false);
                }
            else {
                signUpAlerts.showAlertDialog(RegisterNewUser.this, "oops..something wrong", "First or Last must have characters only", false);
            }
        } else {
            signUpAlerts.showAlertDialog(RegisterNewUser.this, "oops..something missing", "Fields are blank", false);
        }
    }

    private class Restcaliing extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            registerPBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            ServiceHandler sh = new ServiceHandler();
            restResponseString = sh.makeServiceCall(url_prepared, ServiceHandler.GET);
            Log.d("Response: ", "> " + restResponseString);
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            String success = "Success";

            if (restResponseString.contains(success)) {

                Toast.makeText(getApplicationContext(), "Welcome...!", Toast.LENGTH_LONG).show();
                Intent signUpIntent = new Intent(getApplicationContext(), LoginActivity.class);
                registerPBar.setVisibility(View.INVISIBLE);
                startActivity(signUpIntent);
                finish();

            } else {

                registerPBar.setVisibility(View.VISIBLE);
                signUpAlerts.showAlertDialog(RegisterNewUser.this, "Email exist", "Try another email", false);
            }
        }
    }
}
