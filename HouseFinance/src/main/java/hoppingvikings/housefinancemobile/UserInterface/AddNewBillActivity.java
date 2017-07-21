package hoppingvikings.housefinancemobile.UserInterface;

import android.app.DatePickerDialog;
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
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.WebService.UploadCallback;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

/**
 * Created by Josh on 11/02/2017.
 */

public class AddNewBillActivity extends AppCompatActivity implements UploadCallback {

    Button submitButton;
    TextInputLayout billNameEntry;
    TextInputEditText billNameEntryText;
    TextInputLayout billAmountEntry;
    TextInputEditText billAmountEntryText;

    TextInputLayout billDueDateEntry;
    TextInputEditText billDueDateEntryText;

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
        billNameEntry = (TextInputLayout) findViewById(R.id.BillNameEntry);
        billNameEntryText = (TextInputEditText) findViewById(R.id.BillNameEntryText);
        billAmountEntry = (TextInputLayout) findViewById(R.id.BillAmountEntry);
        billAmountEntryText = (TextInputEditText) findViewById(R.id.BillAmountEntryText);
        billDueDateEntry = (TextInputLayout) findViewById(R.id.billDueDateEntry);
        billDueDateEntryText = (TextInputEditText) findViewById(R.id.billDueDateEntryText);
        billDueDateEntryText.setInputType(InputType.TYPE_NULL);

        davidCheck = (CheckBox) findViewById(R.id.CheckBoxDavid);
        vikkiCheck = (CheckBox) findViewById(R.id.CheckBoxVikki);
        vikkiCheck.setChecked(false);
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

        final Calendar myCalendar = Calendar.getInstance(Locale.ENGLISH);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                GregorianCalendar gc = new GregorianCalendar();
                gc.setFirstDayOfWeek(Calendar.MONDAY);
                gc.set(Calendar.MONTH, view.getMonth());
                gc.set(Calendar.DAY_OF_MONTH, view.getDayOfMonth());
                gc.set(Calendar.YEAR, view.getYear());

                String format = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.UK);
                billDueDateEntryText.setText(sdf.format(gc.getTime()));
                billDueDateEntry.setError(null);
            }
        };

        billDueDateEntryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(AddNewBillActivity.this, date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(!ValidateFields()) {
                    return;
                }
                final AlertDialog confirmCancel = new AlertDialog.Builder(AddNewBillActivity.this).create();

                confirmCancel.setTitle("Submit bill entry?");
                confirmCancel.setMessage("Please check that all details are correct before continuing");

                confirmCancel.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        confirmCancel.dismiss();

                        recurring = recurRadio.isChecked();

                        billNameEntry.setEnabled(false);
                        billAmountEntry.setEnabled(false);
                        billAmountEntryText.setEnabled(false);
                        submitButton.setEnabled(false);

                        davidCheck.setEnabled(false);
                        vikkiCheck.setEnabled(false);
                        joshCheck.setEnabled(false);
                        recurRadio.setEnabled(false);
                        regularRadio.setEnabled(false);

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


                            GlobalObjects.webHandler.UploadNewBill(getApplicationContext(), newBill, AddNewBillActivity.this);

                        } catch (JSONException je)
                        {
                            Snackbar.make(layout, "Failed to create Json", Snackbar.LENGTH_LONG).show();
                            ReenableElements();
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
                setResult(RESULT_CANCELED);
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
        billNameEntry.setEnabled(true);
        billAmountEntry.setEnabled(true);
        billAmountEntryText.setEnabled(true);
        submitButton.setEnabled(true);

        davidCheck.setEnabled(true);
        vikkiCheck.setEnabled(true);
        joshCheck.setEnabled(true);

        recurRadio.setEnabled(true);
        regularRadio.setEnabled(true);
    }

    private boolean ValidateFields()
    {
        if(billNameEntryText.getText().length() > 0) {
            billName = billNameEntryText.getText().toString();
            billNameEntry.setError(null);
        }
        else {
            billNameEntryText.requestFocus();
            billNameEntry.setError("Please enter a valid Bill name");
            return false;
        }

        try {
            if(billAmountEntryText.getText().length() > 0) {
                billAmount = DecimalFormat.getInstance().parse(billAmountEntryText.getText().toString());
                billAmountEntry.setError(null);
            }
            else {
                billAmountEntryText.requestFocus();
                billAmountEntry.setError("Please enter a valid Bill amount");
                return false;
            }

            if(billDueDateEntryText.getText().length() > 0)
            {
                billDueDate = new SimpleDateFormat("dd-MM-yyyy").parse(billDueDateEntryText.getText().toString());
                billDueDateEntry.setError(null);
            }
            else
            {
                billDueDateEntry.setError("Please enter a valid Date");
                return false;
            }

        } catch (ParseException pe)
        {
            Log.d("Error", "Parse Error: " + pe.getMessage());
            Snackbar.make(layout, "Parsing Error: " + pe.getMessage(), Snackbar.LENGTH_LONG).show();
            return false;
        }

        forDavid = davidCheck.isChecked();
        forVikki = vikkiCheck.isChecked();
        forJosh = joshCheck.isChecked();

        if(!forDavid && !forJosh)
        {
            Snackbar.make(layout, "Please select at least one person for this bill", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    public void OnSuccessfulUpload() {
        Toast.makeText(getApplicationContext(), "Bill successfully uploaded", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void OnFailedUpload(String failReason) {
        Snackbar.make(layout, failReason, Snackbar.LENGTH_LONG).show();
        ReenableElements();
    }


}
