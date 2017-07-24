package hoppingvikings.housefinancemobile.UserInterface;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.WebService.UploadCallback;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

/**
 * Created by Josh on 03/05/2017.
 */

public class AddNewShoppingItemActivity extends AppCompatActivity implements UploadCallback {

    Button submitButton;
    Button addToCartButton;
    TextInputLayout itemNameLayout;
    TextInputEditText shoppingItemNameEntry;

    CheckBox davidCheck;
    CheckBox vikkiCheck;
    CheckBox joshCheck;

    RadioButton davidRadio;
    RadioButton vikkiRadio;
    RadioButton joshRadio;

    String itemName;

    boolean forDavid;
    boolean forVikki;
    boolean forJosh;

    boolean fromDavid;
    boolean fromVikki;
    boolean fromJosh;

    CoordinatorLayout layout;

    ArrayList<String> _shoppingItems;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("shopping_items", _shoppingItems);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewshoppingitem);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);
        layout = (CoordinatorLayout) findViewById(R.id.coordlayout);

        submitButton = (Button) findViewById(R.id.submitItem);
        addToCartButton = (Button) findViewById(R.id.addToList);
        _shoppingItems = new ArrayList<>();

        itemNameLayout = (TextInputLayout) findViewById(R.id.itemNameLayout);
        shoppingItemNameEntry = (TextInputEditText) findViewById(R.id.ShoppingItemNameEntry);

        davidCheck = (CheckBox) findViewById(R.id.CheckBoxDavid);
        vikkiCheck = (CheckBox) findViewById(R.id.CheckBoxVikki);
        joshCheck = (CheckBox) findViewById(R.id.CheckBoxJosh);

        davidRadio = (RadioButton) findViewById(R.id.davidRadio);
        vikkiRadio = (RadioButton) findViewById(R.id.vikkiRadio);
        joshRadio = (RadioButton) findViewById(R.id.joshRadio);

        davidRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vikkiRadio.isChecked())
                    vikkiRadio.setChecked(false);

                if(joshRadio.isChecked())
                    joshRadio.setChecked(false);
            }
        });

        vikkiRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(davidRadio.isChecked())
                    davidRadio.setChecked(false);

                if(joshRadio.isChecked())
                    joshRadio.setChecked(false);
            }
        });

        joshRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vikkiRadio.isChecked())
                    vikkiRadio.setChecked(false);

                if(davidRadio.isChecked())
                    davidRadio.setChecked(false);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!ValidateFields())
                {
                    return;
                }

                final AlertDialog confirmCancel = new AlertDialog.Builder(AddNewShoppingItemActivity.this).create();

                confirmCancel.setTitle("Submit Item?");
                confirmCancel.setMessage("Please check all details are correct before continuing");

                confirmCancel.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmCancel.dismiss();
                    }
                });

                confirmCancel.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmCancel.dismiss();

                        shoppingItemNameEntry.setEnabled(false);
                        submitButton.setEnabled(false);
                        davidCheck.setEnabled(false);
                        vikkiCheck.setEnabled(false);
                        joshCheck.setEnabled(false);
                        davidRadio.setEnabled(false);
                        vikkiRadio.setEnabled(false);
                        joshRadio.setEnabled(false);

                        JSONObject newItem = new JSONObject();

                        try{
                            newItem.put("Name", itemName);
                            newItem.put("Added", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                            if(fromDavid)
                                newItem.put("AddedBy", "e9636bbb-8b54-49b9-9fa2-9477c303032f");
                            else if(fromVikki)
                                newItem.put("AddedBy", "25c15fb4-b5d5-47d9-917b-c572b1119e65");
                            else if(fromJosh)
                                newItem.put("AddedBy", "f97a50c9-8451-4537-bccb-e89ba5ade95a");

                            JSONArray people = new JSONArray();

                            if(forDavid)
                                people.put("e9636bbb-8b54-49b9-9fa2-9477c303032f");

                            if(forVikki)
                                people.put("25c15fb4-b5d5-47d9-917b-c572b1119e65");

                            if(forJosh)
                                people.put("f97a50c9-8451-4537-bccb-e89ba5ade95a");

                            newItem.put("ItemFor", people);

                            GlobalObjects.webHandler.UploadNewShoppingItem(getApplicationContext(), newItem, AddNewShoppingItemActivity.this);
                        } catch (JSONException je)
                        {
                            Snackbar.make(layout, "Failed to create Json", Snackbar.LENGTH_LONG).show();
                            ReenableElements();
                        }
                    }
                });

                confirmCancel.show();
            }
        });

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ValidateFields())
                {
                    return;
                }

                JSONObject newItem = new JSONObject();

                try{
                    newItem.put("Name", itemName);
                    newItem.put("Added", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                    if(fromDavid)
                        newItem.put("AddedBy", "e9636bbb-8b54-49b9-9fa2-9477c303032f");
                    else if(fromVikki)
                        newItem.put("AddedBy", "25c15fb4-b5d5-47d9-917b-c572b1119e65");
                    else if(fromJosh)
                        newItem.put("AddedBy", "f97a50c9-8451-4537-bccb-e89ba5ade95a");

                    JSONArray people = new JSONArray();

                    if(forDavid)
                        people.put("e9636bbb-8b54-49b9-9fa2-9477c303032f");

                    if(forVikki)
                        people.put("25c15fb4-b5d5-47d9-917b-c572b1119e65");

                    if(forJosh)
                        people.put("f97a50c9-8451-4537-bccb-e89ba5ade95a");

                    newItem.put("ItemFor", people);

                    _shoppingItems.add(newItem.toString());
                    Toast.makeText(getApplicationContext(), "Item added to cart", Toast.LENGTH_SHORT).show();
                    //GlobalObjects.webHandler.UploadNewShoppingItem(getApplicationContext(), newItem, AddNewShoppingItemActivity.this);
                } catch (JSONException je)
                {
                    Snackbar.make(layout, "Failed to create Json", Snackbar.LENGTH_LONG).show();
                    ReenableElements();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.billentrytoolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        final AlertDialog confirmCancel = new AlertDialog.Builder(this).create();

        confirmCancel.setTitle("Cancel item entry?");
        confirmCancel.setMessage("All details entered will be lost.");

        confirmCancel.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                confirmCancel.dismiss();
                setResult(RESULT_CANCELED);
                AddNewShoppingItemActivity.super.onBackPressed();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.cancelButton:
                final AlertDialog confirmCancel = new AlertDialog.Builder(this).create();

                confirmCancel.setTitle("Cancel item entry?");
                confirmCancel.setMessage("All details entered will be lost.");

                confirmCancel.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        confirmCancel.dismiss();
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });

                confirmCancel.setButton(DialogInterface.BUTTON_NEGATIVE, "Stay Here", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        confirmCancel.dismiss();
                    }
                });

                confirmCancel.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ReenableElements()
    {
        shoppingItemNameEntry.setEnabled(true);
        submitButton.setEnabled(true);

        davidCheck.setEnabled(true);
        vikkiCheck.setEnabled(true);
        joshCheck.setEnabled(true);

        davidRadio.setEnabled(true);
        vikkiRadio.setEnabled(true);
        joshRadio.setEnabled(true);
    }

    @Override
    public void OnFailedUpload(String failReason) {
        Snackbar.make(layout, failReason, Snackbar.LENGTH_LONG).show();
        ReenableElements();
    }

    @Override
    public void OnSuccessfulUpload() {
        Toast.makeText(getApplicationContext(), "Item successfully added", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }

    private boolean ValidateFields()
    {
        if(shoppingItemNameEntry.getText().length() > 0) {
            itemName = shoppingItemNameEntry.getText().toString();
            itemNameLayout.setError(null);
        } else {
            itemNameLayout.setError("Please enter a valid Item name");
            return false;
        }

        forDavid = davidCheck.isChecked();
        forVikki = vikkiCheck.isChecked();
        forJosh = joshCheck.isChecked();

        if(!forDavid && !forVikki && !forJosh)
        {
            Snackbar.make(layout, "Please select at least one person for this item", Snackbar.LENGTH_LONG).show();
            return false;
        }

        fromDavid = davidRadio.isChecked();
        fromVikki = vikkiRadio.isChecked();
        fromJosh = joshRadio.isChecked();

        if(!fromDavid && !fromJosh)
        {
            Snackbar.make(layout, "Please select who is adding the item", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
