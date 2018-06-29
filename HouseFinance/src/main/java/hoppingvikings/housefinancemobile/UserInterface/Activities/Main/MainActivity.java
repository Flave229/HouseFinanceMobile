package hoppingvikings.housefinancemobile.UserInterface.Activities.Main;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Activities.MainMenu.MainMenuActivity;
import hoppingvikings.housefinancemobile.UserInterface.SignInActivity;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.WebHandler;


public class MainActivity extends AppCompatActivity implements CommunicationCallback
{
    GoogleSignInClient _signInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.backend_id))
                .build();

        _signInClient = GoogleSignIn.getClient(this, gso);

        // TODO: Need to keep this around for when "We want to write stuff to a file".
        if((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
        }
        else
        {
            HandleSilentSignIn();
        }
    }

    private void HandleSilentSignIn()
    {
        _signInClient.silentSignIn().addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
            @Override
            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                HandleSignInResult(task);
            }
        });
    }

    private void GoToSignInPage()
    {
        Intent signIn = new Intent(this, SignInActivity.class);
        startActivity(signIn);

        finish();
    }

    private void GoToMainMenu()
    {
        Intent mainMenu = new Intent(this, MainMenuActivity.class);
        startActivity(mainMenu);

        finish();
    }

    private void HandleSignInResult(@NonNull Task<GoogleSignInAccount> completeTask)
    {
        try {
            GoogleSignInAccount account = completeTask.getResult(ApiException.class);
            String idToken = account.getIdToken();

            JSONObject tokenJson = new JSONObject();
            try {
                tokenJson.put("Token", idToken);
            } catch (JSONException e)
            {

            }

            WebHandler.Instance().GetSessionID(this, this, tokenJson);
            // Send the token to dave to confirm
            //GoToMainMenu();
        } catch (ApiException e)
        {
            Log.e("Error: ", "signInResult:failed code=" + e.getStatusCode());
            GoToSignInPage();
        }
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
                    //GoToMainMenu();
                    HandleSilentSignIn();
                }
                else
                {
                    //Snackbar.make(_layout, "Some features may not work", Snackbar.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    @Override
    public void OnSuccess(RequestType requestType, Object o) {
        GoToMainMenu();
    }

    @Override
    public void OnFail(RequestType requestType, String message) {
        //Toast.makeText(this, "Could not obtain session", Toast.LENGTH_LONG).show();
        GoToSignInPage();
    }
}
