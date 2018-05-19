package hoppingvikings.housefinancemobile.UserInterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

public class SignInActivity extends AppCompatActivity implements CommunicationCallback {

    SignInButton signInButton;
    Toolbar _toolbar;
    CoordinatorLayout _layout;

    GoogleSignInClient _signInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        _toolbar = findViewById(R.id.appToolbar);
        _toolbar.setTitle("Welcome");
        _toolbar.setSubtitle("Please sign in to continue");
        _layout = findViewById(R.id.coordlayout);
        setSupportActionBar(_toolbar);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.backend_id))
                .build();

        _signInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if(account != null)
        {
            JSONObject tokenJson = new JSONObject();
            try {
                tokenJson.put("Token", account.getIdToken());
            } catch (JSONException e)
            {

            }
            WebHandler.Instance().GetSessionID(this, this, tokenJson);
        }
        //HandleAccount(account);
    }

    private void HandleAccount(GoogleSignInAccount account)
    {
        Intent goToMainMenu = new Intent(this, MainMenuActivity.class);
        startActivity(goToMainMenu);

        finish();
    }

    private void SignIn()
    {
        Intent signInIntent = _signInClient.getSignInIntent();
        startActivityForResult(signInIntent, 0);
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

            //Send the token to dave to confirm
            WebHandler.Instance().GetSessionID(this, this, tokenJson);

            //HandleAccount(account);
        } catch (ApiException e)
        {
            Log.e("Error: ", "signInResult:failed code=" + e.getStatusCode());
            HandleAccount(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            HandleSignInResult(task);
        }
    }

    @Override
    public void OnSuccess(RequestType requestType, Object o) {
        HandleAccount(null);
    }

    @Override
    public void OnFail(RequestType requestType, String message) {
        Snackbar.make(_layout, message, Snackbar.LENGTH_LONG).show();
    }
}
