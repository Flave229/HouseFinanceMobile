package hoppingvikings.housefinancemobile.UserInterface.Activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import hoppingvikings.housefinancemobile.ApiErrorCodes;
import hoppingvikings.housefinancemobile.FileIOHandler;
import hoppingvikings.housefinancemobile.HouseFinanceClass;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.Services.SaltVault.Bills.PaymentsEndpoint;
import hoppingvikings.housefinancemobile.UserInterface.SignInActivity;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;

public class AddPaymentActivity extends AppCompatActivity implements CommunicationCallback
{
    private PaymentsEndpoint _paymentEndpoint;

    TextView billName;
    Button submitButton;
    TextInputLayout paymentAmountEntry;
    TextInputEditText paymentAmountEntryText;

    TextInputLayout paymentDateEntry;
    TextInputEditText paymentDateEntryText;

    TextView selectUser;
    ImageButton editPerson;

    Number paymentAmount;
    Date paymentDate;

    int billid;
    String suggestedPayment;

    CoordinatorLayout layout;

    int _selectedUserId = -1;
    String _selectedUserName = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpayment);

        _paymentEndpoint = HouseFinanceClass.GetBillComponent().GetPaymentsEndpoint();

        Toolbar toolbar = (Toolbar) findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();

        if(i.hasExtra("bill_id"))
        {
            billid = i.getIntExtra("bill_id", -1);
        }

        if(i.hasExtra("suggested_amount"))
        {
            suggestedPayment = i.getStringExtra("suggested_amount");
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

        if(suggestedPayment != null && suggestedPayment.length() > 0)
        {
            paymentAmountEntryText.setText(suggestedPayment);
            paymentAmountEntryText.setSelection(paymentAmountEntryText.length());
        }

        paymentDateEntry = (TextInputLayout) findViewById(R.id.paymentDateEntry);
        paymentDateEntryText = (TextInputEditText) findViewById(R.id.paymentDateEntryText);
        paymentDateEntryText.setInputType(InputType.TYPE_NULL);

        selectUser = (TextView) findViewById(R.id.selectUser);
        editPerson = (ImageButton) findViewById(R.id.editPerson);
        editPerson.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent selectusers = new Intent(AddPaymentActivity.this, SelectUsersActivity.class);
                if(_selectedUserId > -1)
                {
                    selectusers.putExtra("currently_selected_id", _selectedUserId);
                }
                startActivityForResult(selectusers, 0);
            }
        });

        final Calendar myCalendar = Calendar.getInstance(Locale.ENGLISH);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
        {
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

        paymentDateEntryText.setOnClickListener(new View.OnClickListener()
        {
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

        submitButton.setOnClickListener(new View.OnClickListener()
        {
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
                        submitButton.setEnabled(false);
                        confirmCancel.dismiss();

                        paymentAmountEntry.setEnabled(false);
                        paymentDateEntry.setEnabled(false);
                        editPerson.setEnabled(false);

                        JSONObject newPayment = new JSONObject();

                        try
                        {
                            newPayment.put("BillId", billid);
                            newPayment.put("Amount", paymentAmount.doubleValue());
                            newPayment.put("Created", new SimpleDateFormat("yyyy-MM-dd").format(paymentDate));

                            newPayment.put("PersonId", _selectedUserId);

                            _paymentEndpoint.Post(getApplicationContext(), AddPaymentActivity.this, newPayment);
                        }
                        catch (Exception e)
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

        try {
            JSONObject currentUser = new JSONObject(FileIOHandler.Instance().ReadFileAsString("CurrentUser"));
            int userId = currentUser.getInt("id");
            String username = currentUser.getString("firstName");

            _selectedUserId = userId;
            _selectedUserName = username;

            selectUser.setText(_selectedUserName);
        } catch (JSONException je)
        {

        }
    }

    private void ReenableElements()
    {
        paymentAmountEntry.setEnabled(true);
        paymentDateEntry.setEnabled(true);
        submitButton.setEnabled(true);

        editPerson.setEnabled(true);
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
                paymentAmountEntry.setError("Please enter a valid payment remainingAmount");
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

            if(_selectedUserId == -1)
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
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
            case RESULT_OK:
                if(data != null)
                {
                    _selectedUserId = data.getIntExtra("selected_id", -1);
                    _selectedUserName = data.getStringExtra("selected_name");

                    selectUser.setText(_selectedUserName);
                }
                break;

            case RESULT_CANCELED:

                break;
        }
    }

    @Override
    public void OnSuccess(RequestType requestType, Object o)
    {
        Toast.makeText(getApplicationContext(), "Payment added successfully", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void OnFail(RequestType requestType, String message)
    {
        try {
            ApiErrorCodes errorCode = ApiErrorCodes.get(Integer.parseInt(message));

            if(errorCode == ApiErrorCodes.SESSION_EXPIRED || errorCode == ApiErrorCodes.SESSION_INVALID)
            {
                String sessionMessage = "Your session has expired";
                Snackbar.make(layout, sessionMessage, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Refresh", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent signInRefresh = new Intent(AddPaymentActivity.this, SignInActivity.class);
                                signInRefresh.putExtra("Refresh", true);
                                startActivityForResult(signInRefresh, 0);
                            }
                        })
                        .show();
            }
        } catch (Exception e)
        {
            // Not an API error
            Snackbar.make(layout, message, Snackbar.LENGTH_LONG).show();
        }
        ReenableElements();
    }
}
