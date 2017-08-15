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
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Items.ShoppingListObject;
import hoppingvikings.housefinancemobile.WebService.UploadCallback;

/**
 * Created by iView on 11/08/2017.
 */

public class EditShoppingItemActivity extends AppCompatActivity implements UploadCallback {

    Button submitButton;

    TextInputLayout itemNameLayout;
    TextInputEditText shoppingItemNameEntry;

    CheckBox davidCheck;
    CheckBox vikkiCheck;
    CheckBox joshCheck;

    CheckBox editName;
    CheckBox editFor;

    String itemName;

    boolean forDavid;
    boolean forVikki;
    boolean forJosh;

    boolean fromDavid;
    boolean fromVikki;
    boolean fromJosh;

    CoordinatorLayout layout;

    ShoppingListObject item = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editshoppingitem);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appToolbar);
        toolbar.setTitle("Edit Shopping item");
        toolbar.setSubtitle("Tick the fields you wish to edit");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent() != null && getIntent().hasExtra("id"))
        {
            item = GlobalObjects.GetShoppingItemFromID(getIntent().getStringExtra("id"));
        }

        layout = (CoordinatorLayout) findViewById(R.id.coordlayout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        submitButton = (Button) findViewById(R.id.submitBill);
        itemNameLayout = (TextInputLayout)  findViewById(R.id.itemNameLayout);
        shoppingItemNameEntry = (TextInputEditText)  findViewById(R.id.ShoppingItemNameEntry);
        itemNameLayout.setEnabled(false);
        shoppingItemNameEntry.setEnabled(false);

        davidCheck = (CheckBox) findViewById(R.id.CheckBoxDavid);
        davidCheck.setEnabled(false);
        davidCheck.setChecked(false);
        vikkiCheck = (CheckBox) findViewById(R.id.CheckBoxVikki);
        vikkiCheck.setChecked(false);
        joshCheck = (CheckBox) findViewById(R.id.CheckBoxJosh);
        joshCheck.setEnabled(false);
        joshCheck.setChecked(false);

        if(item != null)
        {
            shoppingItemNameEntry.setText(item.itemName);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Shopping item not found", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }

        editName = (CheckBox) findViewById(R.id.editNameCheck);
        editFor = (CheckBox) findViewById(R.id.editForCheck);

        SetEditCheckListeners();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editName.isChecked() && !editFor.isChecked() )
                {
                    Snackbar.make(layout, "Please edit at least one field to submit", Snackbar.LENGTH_LONG).show();
                    return;
                }

                SubmitEditedShoppingItem();
            }
        });
    }

    private void SetEditCheckListeners()
    {
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editName.isChecked())
                {
                    itemNameLayout.setEnabled(true);
                    shoppingItemNameEntry.setEnabled(true);
                    shoppingItemNameEntry.requestFocus();
                }
                else
                {
                    itemNameLayout.setEnabled(false);
                    shoppingItemNameEntry.setEnabled(false);
                }
            }
        });

        editFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editFor.isChecked())
                {
                    davidCheck.setEnabled(true);
                    vikkiCheck.setEnabled(true);
                    joshCheck.setEnabled(true);
                }
                else
                {
                    davidCheck.setEnabled(false);
                    vikkiCheck.setEnabled(false);
                    joshCheck.setEnabled(false);
                }
            }
        });

    }

    private void SubmitEditedShoppingItem()
    {
        if(!ValidateFields()) {
            return;
        }

        final AlertDialog confirmcancel = new AlertDialog.Builder(this).create();
        confirmcancel.setMessage("Submit edit? Check that all details are correct before submitting");
        confirmcancel.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmcancel.dismiss();
            }
        });

        confirmcancel.setButton(DialogInterface.BUTTON_POSITIVE, "Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmcancel.dismiss();
                JSONObject editedBill = new JSONObject();
                try {
                    editedBill.put("Id", item.ID);
                    if(editName.isChecked())
                        editedBill.put("Name", shoppingItemNameEntry.getText().toString());

                    if(editFor.isChecked())
                    {
                        JSONArray people = new JSONArray();
                        if(davidCheck.isChecked())
                            people.put(GlobalObjects.USERGUID_DAVE);

                        if(joshCheck.isChecked())
                            people.put(GlobalObjects.USERGUID_JOSH);

                        editedBill.put("ItemFor", people);
                    }

                    GlobalObjects.webHandler.EditShoppingItem(EditShoppingItemActivity.this, editedBill, EditShoppingItemActivity.this);

                } catch (Exception e)
                {
                    Snackbar.make(layout, "Failed to create JSON", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        confirmcancel.show();
    }

    private boolean ValidateFields()
    {
        if(shoppingItemNameEntry.getText().length() > 0) {
            itemName = shoppingItemNameEntry.getText().toString();
            itemNameLayout.setError(null);
        }
        else {
            shoppingItemNameEntry.requestFocus();
            itemNameLayout.setError("Please enter a valid Bill name");
            return false;
        }

        forDavid = davidCheck.isChecked();
        forVikki = vikkiCheck.isChecked();
        forJosh = joshCheck.isChecked();

        if(editFor.isChecked())
        {
            if(!forDavid && !forJosh)
            {
                Snackbar.make(layout, "Please select at least one person for this bill", Snackbar.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    @Override
    public void OnSuccessfulUpload() {
        Toast.makeText(getApplicationContext(), "Edit successfully uploaded", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void OnFailedUpload(String failReason) {
        Snackbar.make(layout, failReason, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
