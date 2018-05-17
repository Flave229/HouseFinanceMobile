package hoppingvikings.housefinancemobile.UserInterface;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import hoppingvikings.housefinancemobile.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Need to keep this around for when "We want to write stuff to a file". I asked what specifically... Ha
        if((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
        }
        else
        {
            GoToMainMenu();
        }
    }

    private void GoToMainMenu()
    {
        Intent mainMenu = new Intent(this, MainMenuActivity.class);
        startActivity(mainMenu);

        finish();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //_handler.removeCallbacksAndMessages(runnable);
    }

    @Override
    public void onBackPressed() {
        // End the app process after pressing back
        finish();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass)
    {
        ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : mngr.getRunningServices(Integer.MAX_VALUE))
        {
            if(serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 10:
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    GoToMainMenu();
                }
                else
                {
                    //Snackbar.make(_layout, "Some features may not work", Snackbar.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }
}
