package com.relecotech.androidsparsh.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.relecotech.androidsparsh.R;

/**
 * Created by yogesh on 19-05-2017.
 */

public class SparshAbout extends AppCompatActivity {

    private String app_ver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sparshabout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        TextView versionTextView = (TextView) findViewById(R.id.appVersionTextView);
//        try {
//            app_ver = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            System.out.println("INSIDE CATCH");
//        }
//
//
//        versionTextView.setText("Version " + app_ver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
