package hoppingvikings.housefinancemobile.UserInterface;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.R;

/**
 * Created by Josh on 11/02/2017.
 */

public class AddNewBillActivity extends AppCompatActivity {

    Button submitButton;
    EditText billNameEntry;
    EditText billAmountEntry;
    EditText billDueDateEntry;

    CheckBox davidCheck;
    CheckBox vikkiCheck;
    CheckBox joshCheck;

    RadioButton regularRadio;
    RadioButton recurRadio;

    String billName;
    Number billAmount;
    Date billDueDate;

    boolean forDavid;
    boolean forVikki;
    boolean forJosh;

    boolean recurring;

    CoordinatorLayout layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewbill);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);

        layout = (CoordinatorLayout) findViewById(R.id.coordlayout);

        submitButton = (Button) findViewById(R.id.submitBill);
        billNameEntry = (EditText) findViewById(R.id.BillNameEntry);
        billAmountEntry = (EditText) findViewById(R.id.BillAmountEntry);
        billDueDateEntry = (EditText) findViewById(R.id.BillDueEntry);

        davidCheck = (CheckBox) findViewById(R.id.CheckBoxDavid);
        vikkiCheck = (CheckBox) findViewById(R.id.CheckBoxVikki);
        joshCheck = (CheckBox) findViewById(R.id.CheckBoxJosh);

        regularRadio = (RadioButton) findViewById(R.id.BillTypeRegular);
        recurRadio = (RadioButton) findViewById(R.id.BillTypeRecur);

        regularRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recurRadio.isChecked())
                    recurRadio.setChecked(false);
            }
        });

        recurRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(regularRadio.isChecked())
                    regularRadio.setChecked(false);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final AlertDialog confirmCancel = new AlertDialog.Builder(AddNewBillActivity.this).create();

                confirmCancel.setTitle("Submit bill entry?");
                confirmCancel.setMessage("Please check that all details are correct before continuing");

                confirmCancel.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        confirmCancel.dismiss();

                        if(billNameEntry.getText().length() > 0)
                            billName = billNameEntry.getText().toString();
                        else {
                            Snackbar.make(layout, "Please enter a valid Bill name", Snackbar.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            if(billAmountEntry.getText().length() > 0)
                                billAmount = DecimalFormat.getInstance().parse(billAmountEntry.getText().toString());
                            else {
                                Snackbar.make(layout, "Please enter a valid Bill amount", Snackbar.LENGTH_LONG).show();
                                return;
                            }

                            if(billDueDateEntry.getText().length() > 0)
                                billDueDate = new SimpleDateFormat("dd-MM-yyyy").parse(billDueDateEntry.getText().toString());
                            else {
                                Snackbar.make(layout, "Please enter a valid Bill due date", Snackbar.LENGTH_LONG).show();
                                return;
                            }
                        } catch (ParseException pe)
                        {
                            Log.d("Error", "Parse Error: " + pe.getMessage());
                            Snackbar.make(layout, "Parsing Error: " + pe.getMessage(), Snackbar.LENGTH_LONG).show();
                            return;
                        }

                        forDavid = davidCheck.isChecked();
                        forVikki = vikkiCheck.isChecked();
                        forJosh = joshCheck.isChecked();

                        recurring = recurRadio.isChecked();

                        billNameEntry.setEnabled(false);
                        billAmountEntry.setEnabled(false);
                        billDueDateEntry.setEnabled(false);
                        submitButton.setEnabled(false);

                        davidCheck.setEnabled(false);
                        vikkiCheck.setEnabled(false);
                        joshCheck.setEnabled(false);

                        JSONObject newBill = new JSONObject();

                        try {
                            newBill.put("Name", billName);
                            newBill.put("AmountOwed", billAmount.doubleValue());
                            newBill.put("Due", new SimpleDateFormat("yyyy-MM-dd").format(billDueDate));

                            JSONArray people = new JSONArray();

                            if(forDavid)
                                people.put("e9636bbb-8b54-49b9-9fa2-9477c303032f");

                            if(forVikki)
                                people.put("25c15fb4-b5d5-47d9-917b-c572b1119e65");

                            if(forJosh)
                                people.put("f97a50c9-8451-4537-bccb-e89ba5ade95a");

                            /*JSONObject david = new JSONObject();
                            JSONObject vikki = new JSONObject();
                            JSONObject josh = new JSONObject();

                            david.put("Id","");
                            david.put("ForDavid", forDavid);

                            vikki.put("Id", "");
                            vikki.put("ForVikki", forVikki);

                            josh.put("Id", "");
                            josh.put("ForJosh", forJosh);

                            people.put(david);
                            people.put(vikki);
                            people.put(josh);*/

                            newBill.put("People", people);

                            if(recurring)
                                newBill.put("RecurringType", 1);
                            else
                                newBill.put("RecurringType", 0);


                            GlobalObjects._service.UploadNewBill(newBill);
                            finish();

                        } catch (JSONException je)
                        {
                            Snackbar.make(layout, "Failed to create Json", Snackbar.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });

                confirmCancel.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        confirmCancel.dismiss();
                    }
                });

                confirmCancel.show();
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

        confirmCancel.setTitle("Cancel bill entry?");
        confirmCancel.setMessage("All details entered will be lost.");

        confirmCancel.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                confirmCancel.dismiss();
                AddNewBillActivity.super.onBackPressed();
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

                confirmCancel.setTitle("Cancel bill entry?");
                confirmCancel.setMessage("All details entered will be lost.");

                confirmCancel.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        confirmCancel.dismiss();
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
}
