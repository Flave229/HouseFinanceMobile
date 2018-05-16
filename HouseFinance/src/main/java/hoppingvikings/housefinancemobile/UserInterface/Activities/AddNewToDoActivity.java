package hoppingvikings.housefinancemobile.UserInterface.Activities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;

public class AddNewToDoActivity extends AppCompatActivity implements CommunicationCallback {

    @Override
    public void OnSuccess(RequestType requestType, Object o) {

    }

    @Override
    public void OnFail(RequestType requestType, String message) {

    }
}
