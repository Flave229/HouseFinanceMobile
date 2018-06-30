package hoppingvikings.housefinancemobile.UserInterface.Activities;

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
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import hoppingvikings.housefinancemobile.HouseFinanceClass;
import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.Services.SaltVault.Bills.PaymentsEndpoint;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

public class EditPaymentActivity extends AppCompatActivity implements CommunicationCallback
{
    private PaymentsEndpoint _paymentEndpoint;

    Button submitButton;

    CheckBox editAmount;
    CheckBox editDate;

    TextInputLayout paymentAmountEntry;
    TextInputEditText paymentAmountEntryText;

    TextInputLayout paymentDateEntry;
    TextInputEditText paymentDateEntryText;

    String paymentID;
    String paymentAmount;
    String paymentDate;

    CoordinatorLayout layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpayment);

        _paymentEndpoint = HouseFinanceClass.GetBillComponent().GetPaymentsEndpoint();

        Toolbar toolbar = (Toolbar) findViewById(R.id.appToolbar);
        toolbar.setTitle("Edit Payment");
        toolbar.setSubtitle("Tick the fields you wish to edit");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent() != null)
        {
            if(getIntent().hasExtra("payment_id"))
                paymentID = getIntent().getStringExtra("payment_id");

            if(getIntent().hasExtra("payment_amount"))
                paymentAmount = String.valueOf(getIntent().getDoubleExtra("payment_amount", 0.0));

            if(getIntent().hasExtra("payment_date"))
                paymentDate = getIntent().getStringExtra("payment_date");
        }

        layout = (CoordinatorLayout) findViewById(R.id.coordlayout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        submitButton = (Button) findViewById(R.id.submitBill);

        paymentAmountEntry = (TextInputLayout) findViewById(R.id.paymentAmountEntry);
        paymentAmountEntryText = (TextInputEditText) findViewById(R.id.paymentAmountEntryText);
        paymentAmountEntry.setEnabled(false);
        paymentAmountEntryText.setEnabled(false);

        paymentDateEntry = (TextInputLayout) findViewById(R.id.paymentDateEntry);
        paymentDateEntryText = (TextInputEditText) findViewById(R.id.paymentDateEntryText);
        paymentDateEntry.setEnabled(false);
        paymentDateEntryText.setEnabled(false);
        paymentDateEntryText.setInputType(InputType.TYPE_NULL);

        editAmount = (CheckBox) findViewById(R.id.editAmountCheck);
        editDate = (CheckBox) findViewById(R.id.editDateCheck);

        paymentAmountEntryText.setText(paymentAmount);
        paymentDateEntryText.setText(paymentDate);

        SetEditCheckListeners();

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

                new DatePickerDialog(EditPaymentActivity.this, date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editAmount.isChecked() && !editDate.isChecked())
                {
                    Snackbar.make(layout, "Please edit at least one field to submit", Snackbar.LENGTH_LONG).show();
                    return;
                }

                SubmitEditedPayment();
            }
        });
    }

    private void SetEditCheckListeners()
    {
        editAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editAmount.isChecked())
                {
                    paymentAmountEntry.setEnabled(true);
                    paymentAmountEntryText.setEnabled(true);
                    paymentAmountEntryText.requestFocus();
                }
                else
                {
                    paymentAmountEntry.setEnabled(false);
                    paymentAmountEntryText.setEnabled(false);
                    paymentAmountEntryText.setText(paymentAmount);
                }
            }
        });

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editDate.isChecked())
                {
                    paymentDateEntry.setEnabled(true);
                    paymentDateEntryText.setEnabled(true);
                }
                else
                {
                    paymentDateEntry.setEnabled(false);
                    paymentDateEntryText.setEnabled(false);
                    paymentDateEntryText.setText(paymentDate);
                }
            }
        });
    }

    private void SubmitEditedPayment()
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
                JSONObject editedPayment = new JSONObject();
                try {
                    editedPayment.put("Id", paymentID);

                    if(editAmount.isChecked())
                        editedPayment.put("Amount", Double.valueOf(paymentAmountEntryText.getText().toString()));

                    if(editDate.isChecked())
                    {
                        Date editedDate = new SimpleDateFormat("dd-MM-yyyy").parse(paymentDateEntryText.getText().toString());
                        editedPayment.put("Created", new SimpleDateFormat("yyyy-MM-dd").format(editedDate));
                    }

                    _paymentEndpoint.Patch(EditPaymentActivity.this, EditPaymentActivity.this, editedPayment);
                }
                catch (Exception e)
                {
                    Snackbar.make(layout, "Failed to create JSON", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        confirmcancel.show();
    }

    private boolean ValidateFields()
    {
        try {
            if(paymentAmountEntryText.getText().length() > 0) {
                paymentAmountEntry.setError(null);
            }
            else {
                paymentAmountEntryText.requestFocus();
                paymentAmountEntry.setError("Please enter a valid payment remainingAmount");
                return false;
            }

            if(paymentDateEntryText.getText().length() > 0)
            {
                paymentDateEntry.setError(null);
            }
            else
            {
                paymentDateEntry.setError("Please enter a valid Date");
                return false;
            }

        } catch (Exception pe)
        {
            Log.d("Error", pe.getMessage());
            Snackbar.make(layout, "Error: " + pe.getMessage(), Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
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

    @Override
    public void OnSuccess(RequestType requestType, Object o)
    {
        Toast.makeText(getApplicationContext(), "Edit successfully uploaded", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void OnFail(RequestType requestType, String message)
    {
        Snackbar.make(layout, message, Snackbar.LENGTH_LONG).show();
    }
}
