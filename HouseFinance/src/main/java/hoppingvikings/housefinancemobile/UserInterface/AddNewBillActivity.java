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
            public void onClick(View view) {
                final AlertDialog confirmCancel = new AlertDialog.Builder(AddNewBillActivity.this).create();

                confirmCancel.setTitle("Submit bill entry?");
                confirmCancel.setMessage("Please check that all details are correct before continuing");

                confirmCancel.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        confirmCancel.dismiss();

                        billName = billNameEntry.getText().toString();
                        try {
                            billAmount = DecimalFormat.getInstance().parse(billAmountEntry.getText().toString());
                            billDueDate = new SimpleDateFormat("yyyy-MM-dd").parse(billDueDateEntry.getText().toString());
                        } catch (ParseException pe)
                        {
                            Log.d("Error", "Parse Error: " + pe.getMessage());
                        }

                        forDavid = davidCheck.isChecked();
                        forVikki = vikkiCheck.isChecked();
                        forJosh = joshCheck.isChecked();

                        recurring = recurRadio.isChecked();

                        JSONObject newBill = new JSONObject();

                        try {
                            newBill.put("Name", billName);
                            newBill.put("DateDue", new SimpleDateFormat("yyyy-MM-dd").format(billDueDate));
                            newBill.put("AmountDue", billAmount.doubleValue());

                            newBill.put("David", forDavid);
                            newBill.put("Vikki", forVikki);
                            newBill.put("Josh", forJosh);

                            if(recurring)
                                newBill.put("Recurring", true);
                            else
                                newBill.put("Single", true);


                            GlobalObjects._service.UploadNewBill(newBill);
                        } catch (JSONException je)
                        {
                            Snackbar.make(layout, "Failed to create Json", Snackbar.LENGTH_LONG).show();
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
