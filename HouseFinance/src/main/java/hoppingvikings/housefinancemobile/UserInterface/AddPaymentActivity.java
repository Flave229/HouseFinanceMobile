package hoppingvikings.housefinancemobile.UserInterface;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
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
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.WebService.UploadCallback;

/**
 * Created by iView on 18/07/2017.
 */

public class AddPaymentActivity extends AppCompatActivity implements UploadCallback {

    TextView billName;
    Button submitButton;
    TextInputLayout paymentAmountEntry;
    TextInputEditText paymentAmountEntryText;

    TextInputLayout paymentDateEntry;
    TextInputEditText paymentDateEntryText;

    RadioButton davidRadio;
    RadioButton joshRadio;
    RadioButton vikkiRadio;

    Number paymentAmount;
    Date paymentDate;

    String billid;

    boolean forDavid;
    boolean forJosh;
    boolean forVikki;

    CoordinatorLayout layout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpayment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();

        if(i.hasExtra("bill_id"))
        {
            billid = i.getStringExtra("bill_id");
        }

        layout = (CoordinatorLayout) findViewById(R.id.coordlayout);

        submitButton = (Button) findViewById(R.id.submitBill);
        billName = (TextView) findViewById(R.id.billNameLabel);

        if(i.hasExtra("bill_name"))
        {
            billName.setText("Bill: " + i.getStringExtra("bill_name"));
        }

        paymentAmountEntry = (TextInputLayout) findViewById(R.id.paymentAmountEntry);
        paymentAmountEntryText = (TextInputEditText) findViewById(R.id.paymentAmountEntryText);

        paymentDateEntry = (TextInputLayout) findViewById(R.id.paymentDateEntry);
        paymentDateEntryText = (TextInputEditText) findViewById(R.id.paymentDateEntryText);
        paymentDateEntryText.setInputType(InputType.TYPE_NULL);

        davidRadio = (RadioButton) findViewById(R.id.davidRadio);
        joshRadio = (RadioButton) findViewById(R.id.joshRadio);
        vikkiRadio = (RadioButton) findViewById(R.id.vikkiRadio);
        vikkiRadio.setVisibility(View.GONE);

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
                paymentDateEntryText.setText(sdf.format(gc.getTime()));
                paymentDateEntry.setError(null);
            }
        };

        paymentDateEntryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddPaymentActivity.this, date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        String format = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.UK);
        paymentDateEntryText.setText(sdf.format(myCalendar.getTime()));

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ValidateFields())
                    return;

                final AlertDialog confirmCancel = new AlertDialog.Builder(AddPaymentActivity.this).create();

                confirmCancel.setTitle("Submit payment?");
                confirmCancel.setMessage("Please check that all details are correct before continuing");

                confirmCancel.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmCancel.dismiss();

                        paymentAmountEntry.setEnabled(false);
                        paymentDateEntry.setEnabled(false);
                        davidRadio.setEnabled(false);
                        joshRadio.setEnabled(false);

                        forDavid = davidRadio.isChecked();
                        forJosh = joshRadio.isChecked();

                        JSONObject newPayment = new JSONObject();

                        try {
                            newPayment.put("Amount", paymentAmount.doubleValue());
                            newPayment.put("Created", new SimpleDateFormat("yyyy-MM-dd").format(paymentDate));

                            if(forDavid)
                                newPayment.put("PersonId", "e9636bbb-8b54-49b9-9fa2-9477c303032f");
                            else if(forJosh)
                                newPayment.put("PersonId", "f97a50c9-8451-4537-bccb-e89ba5ade95a");


                            OnSuccessfulUpload();
                            //GlobalObjects.webHandler.UploadNewPayment(getApplicationContext(), newPayment, billid, AddPaymentActivity.this);
                        } catch (Exception e)
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

    private void ReenableElements()
    {
        paymentAmountEntry.setEnabled(true);
        paymentDateEntry.setEnabled(true);
        submitButton.setEnabled(true);

        davidRadio.setEnabled(true);
        vikkiRadio.setEnabled(true);
        joshRadio.setEnabled(true);
    }

    private boolean ValidateFields()
    {
        try {
            if(paymentAmountEntryText.getText().length() > 0)
            {
                paymentAmount = DecimalFormat.getInstance().parse(paymentAmountEntryText.getText().toString());
                paymentAmountEntry.setError(null);
            }
            else
            {
                paymentAmountEntryText.requestFocus();
                paymentAmountEntry.setError("Please enter a valid payment amount");
                return false;
            }

            if(paymentDateEntryText.getText().length() > 0)
            {
                paymentDate = new SimpleDateFormat("dd-MM-yyyy").parse(paymentDateEntryText.getText().toString());
                paymentDateEntry.setError(null);
            }
            else
            {
                paymentDateEntry.setError("Please enter a valid date");
                return false;
            }

            forDavid = davidRadio.isChecked();
            forVikki = vikkiRadio.isChecked();
            forJosh = joshRadio.isChecked();

            if(!forDavid && !forJosh)
            {
                Snackbar.make(layout, "Please select at least one person for this bill", Snackbar.LENGTH_LONG).show();
                return false;
            }
        } catch (Exception e)
        {
            Log.d("Error", "Parse Error: " + e.getMessage());
            Snackbar.make(layout, "Parsing Error: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.billentrytoolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.cancelButton:
                final AlertDialog confirmCancel = new AlertDialog.Builder(this).create();

                confirmCancel.setTitle("Cancel payment entry?");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void OnFailedUpload(String failReason) {
        Snackbar.make(layout, failReason, Snackbar.LENGTH_LONG).show();
        ReenableElements();
    }

    @Override
    public void OnSuccessfulUpload() {
        Toast.makeText(getApplicationContext(), "Payment added successfully", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }
}
