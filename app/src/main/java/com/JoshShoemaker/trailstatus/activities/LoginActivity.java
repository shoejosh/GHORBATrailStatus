package com.JoshShoemaker.trailstatus.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.JoshShoemaker.trailstatus.R;
import com.JoshShoemaker.trailstatus.helpers.GhorbaSiteActions;
import com.JoshShoemaker.trailstatus.models.Trail;

/**
 * Created by Josh on 2/9/14.
 */
public class LoginActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
    }


    public void btnLoginClicked(View v)
    {
        TextView errorText = (TextView) findViewById(R.id.tvError);
        errorText.setText("");

        EditText usernameText = (EditText) findViewById(R.id.txtUsername);
        EditText passText = (EditText) findViewById(R.id.txtPassword);

        String username = String.valueOf(usernameText.getText());
        String password = String.valueOf(passText.getText());

        if(username.isEmpty() || password.isEmpty())
        {
            errorText.setText("Please enter a username and password");
            return;
        }

        ProgressBar pb = (ProgressBar) findViewById(R.id.pbLogin);
        pb.setVisibility(ProgressBar.VISIBLE);

        //test login creds
        new LoginTask().execute(username, password);
    }

    public void btnCancelLoginClicked(View v)
    {
        setResult(RESULT_CANCELED);
        finish();
    }


    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... creds) {
            Boolean result = GhorbaSiteActions.login(creds[0], creds[1]);
            if(result)
            {
                CheckBox cbSaveCreds = (CheckBox) findViewById(R.id.cbSaveCreds);
                if(cbSaveCreds.isChecked())
                {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("username", creds[0]);
                    edit.putString("password", creds[1]);
                    edit.commit();
                }
            }

            return result;
        }

        protected void onPostExecute(Boolean result)
        {
            ProgressBar pb = (ProgressBar) findViewById(R.id.pbLogin);
            pb.setVisibility(ProgressBar.INVISIBLE);

            if(result)
            {
                Intent data = new Intent();
                data.putExtra("login", result);
                setResult(RESULT_OK, data);
                finish();
            }
            else
            {
                TextView errorText = (TextView) findViewById(R.id.tvError);
                errorText.setText("Error logging in");
            }
        }

    }
}