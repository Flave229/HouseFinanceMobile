package hoppingvikings.housefinancemobile.UserInterface.Activities;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.FileName;
import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.FileIOHandler;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Fragments.AddShoppingItemFragment;
import hoppingvikings.housefinancemobile.UserInterface.Fragments.Interfaces.ButtonPressedCallback;
import hoppingvikings.housefinancemobile.UserInterface.Fragments.ShoppingCartFragment;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

public class AddNewShoppingItemActivity extends AppCompatActivity implements CommunicationCallback {

    public Button addToCartButton;
    FrameLayout _fragmentContainer;

    CoordinatorLayout layout;

    public ArrayList<String> _shoppingItems;
    public ArrayList<JSONObject> _recentShoppingItems;

    ButtonPressedCallback _owner;

    ProgressBar uploadProgress;
    public int progress;

    boolean disableCartButton;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("shopping_items", _shoppingItems);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewshoppingitem);
        _fragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
        uploadProgress = (ProgressBar) findViewById(R.id.uploadProgress);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);
        layout = (CoordinatorLayout) findViewById(R.id.coordlayout);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        addToCartButton = (Button) findViewById(R.id.addToList);
        _shoppingItems = new ArrayList<>();

        disableCartButton = false;

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _owner.AddToCartPressed();
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AddShoppingItemFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();

        /*LoadRecentItemsAsync task = new LoadRecentItemsAsync();
        task.execute();*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shopping_item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void SetCallbackOwner(ButtonPressedCallback owner)
    {
        _owner = owner;
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 1)
        {
            disableCartButton = false;
            getSupportFragmentManager().popBackStack();
        }
        else
        {
            if(_shoppingItems.size() == 0)
            {
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
            final AlertDialog confirmCancel = new AlertDialog.Builder(this).create();

            confirmCancel.setTitle("Cancel item entry?");
            confirmCancel.setMessage("All items in the cart will be lost.");

            confirmCancel.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    confirmCancel.dismiss();
                    setResult(RESULT_CANCELED);
                    finish();
                    //AddNewShoppingItemActivity.super.onBackPressed();
                }
            });

            confirmCancel.setButton(DialogInterface.BUTTON_NEGATIVE, "Stay Here", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    confirmCancel.dismiss();
                }
            });

            confirmCancel.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.view_cart:
                if(!disableCartButton)
                {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ShoppingCartFragment())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null)
                            .commit();
                }

                disableCartButton = true;
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void ReenableElements()
    {
        addToCartButton.setEnabled(true);
    }

    public void UploadNextItem()
    {
        try {
            WebHandler.Instance().UploadNewItem(this, new JSONObject(_shoppingItems.get(0)), this, ItemType.SHOPPING);
        } catch (Exception e)
        {

        }
    }

    @Override
    public void OnSuccess(RequestType requestType, Object o)
    {
        uploadProgress.setVisibility(View.VISIBLE);
        uploadProgress.setProgress(uploadProgress.getProgress() + progress);
        if(_shoppingItems.size() > 0)
        {
            _shoppingItems.remove(0);
        }

        if(_shoppingItems.size() > 0)
        {
            UploadNextItem();
            return;
        }
        Toast.makeText(getApplicationContext(), "Item successfully added", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void OnFail(RequestType requestType, String message)
    {
        Snackbar.make(layout, message, Snackbar.LENGTH_LONG).show();
        ReenableElements();
    }

    private class LoadRecentItemsAsync extends AsyncTask<Void, Void, ArrayList<JSONObject>>
    {
        @Override
        protected ArrayList<JSONObject> doInBackground(Void... params) {
            return new FileIOHandler().ReadFile(FileName.SHOPPING_RECENT_ITEMS);
        }

        @Override
        protected void onPostExecute(ArrayList<JSONObject> arrayList) {
            if(arrayList != null)
            {
                _recentShoppingItems = arrayList;
            }
            else
            {
                _recentShoppingItems = new ArrayList<>();
            }
        }
    }
}
