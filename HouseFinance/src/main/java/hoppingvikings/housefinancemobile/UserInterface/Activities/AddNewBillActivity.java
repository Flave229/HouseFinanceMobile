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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import hoppingvikings.housefinancemobile.Services.SaltVault.User.LogInEndpoint;
import hoppingvikings.housefinancemobile.HouseFinanceClass;
import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.SignInActivity;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.SessionPersister;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

public class AddNewBillActivity extends AppCompatActivity implements CommunicationCallback
{
    private SessionPersister _session;
    private LogInEndpoint _logInEndpoint;

    Button submitButton;
    TextInputLayout billNameEntry;
    TextInputEditText billNameEntryText;
    TextInputLayout billAmountEntry;
    TextInputEditText billAmountEntryText;

    TextInputLayout billDueDateEntry;
    TextInputEditText billDueDateEntryText;

    TextView selectUsers;
    ImageButton editPeople;

    RadioButton regularRadio;
    RadioButton recurRadio;

    String billName;
    Number billAmount;
    Date billDueDate;

    boolean recurring;

    CoordinatorLayout layout;

    ArrayList<Integer> _selectedUserIds = new ArrayList<>();
    ArrayList<String> _selectedUserNames = new ArrayList<>();

    boolean _obtainingSession = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewbill);

        _session = HouseFinanceClass.GetSessionPersisterComponent().GetSessionPersister();
        _logInEndpoint = HouseFinanceClass.GetUserComponent().GetLogInEndpoint();

        Toolbar toolbar = (Toolbar) findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layout = (CoordinatorLayout) findViewById(R.id.coordlayout);

        submitButton = (Button) findViewById(R.id.submitBill);
        billNameEntry = (TextInputLayout) findViewById(R.id.BillNameEntry);
        billNameEntryText = (TextInputEditText) findViewById(R.id.BillNameEntryText);
        billAmountEntry = (TextInputLayout) findViewById(R.id.BillAmountEntry);
        billAmountEntryText = (TextInputEditText) findViewById(R.id.BillAmountEntryText);
        billDueDateEntry = (TextInputLayout) findViewById(R.id.billDueDateEntry);
        billDueDateEntryText = (TextInputEditText) findViewById(R.id.billDueDateEntryText);
        billDueDateEntryText.setInputType(InputType.TYPE_NULL);

        selectUsers = (TextView) findViewById(R.id.selectUsers);
        editPeople = (ImageButton) findViewById(R.id.editPeople);
        editPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectusers = new Intent(AddNewBillActivity.this, SelectUsersActivity.class);
                selectusers.putExtra("multiple_user_selection", true);
                if(_selectedUserIds.size() > 0)
                {
                    selectusers.putExtra("currently_selected_ids", _selectedUserIds);
                }
                startActivityForResult(selectusers, 0);
            }
        });

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
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        confirmCancel.dismiss();

                        recurring = recurRadio.isChecked();

                        billNameEntry.setEnabled(false);
                        billAmountEntry.setEnabled(false);
                        billAmountEntryText.setEnabled(false);
                        editPeople.setEnabled(false);
                        submitButton.setEnabled(false);

                        recurRadio.setEnabled(false);
                        regularRadio.setEnabled(false);

                        JSONObject newBill = new JSONObject();

                        try
                        {
                            newBill.put("Name", billName);
                            newBill.put("TotalAmount", billAmount.doubleValue());
                            newBill.put("Due", new SimpleDateFormat("yyyy-MM-dd").format(billDueDate));

                            JSONArray people = new JSONArray();

                            for (int id : _selectedUserIds) {
                                people.put(id);
                            }

                            newBill.put("PeopleIds", people);

                            if(recurring)
                                newBill.put("RecurringType", 1);
                            else
                                newBill.put("RecurringType", 0);


                            WebHandler.Instance().UploadNewItem(getApplicationContext(), newBill, AddNewBillActivity.this, ItemType.BILL);

                        }
                        catch (JSONException je)
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.billentrytoolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed()
    {
        if(billNameEntryText.getText().length() == 0
                && billAmountEntryText.getText().length() == 0
                && billDueDateEntryText.getText().length() == 0
                && _selectedUserIds.size() == 0)
        {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
            return;
        }
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
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

        editPeople.setEnabled(true);

        recurRadio.setEnabled(true);
        regularRadio.setEnabled(true);
    }

    private boolean ValidateFields()
    {
        if(billNameEntryText.getText().length() > 0)
        {
            billName = billNameEntryText.getText().toString();
            billNameEntry.setError(null);
        }
        else
            {
            billNameEntryText.requestFocus();
            billNameEntry.setError("Please enter a valid Bill name");
            return false;
        }

        try {
            if(billAmountEntryText.getText().length() > 0) {
                billAmount = DecimalFormat.getInstance().parse(billAmountEntryText.getText().toString());
                billAmountEntry.setError(null);
            }
            else
                {
                billAmountEntryText.requestFocus();
                billAmountEntry.setError("Please enter a valid Bill remainingAmount");
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

        if(_selectedUserIds.size() < 1)
        {
            Snackbar.make(layout, "Please select at least one person for this bill", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
            case RESULT_OK:
                if(data != null)
                {
                    _selectedUserIds = data.getIntegerArrayListExtra("selected_ids");
                    _selectedUserNames = data.getStringArrayListExtra("selected_names");

                    String namesString = "";
                    int index = 0;
                    for (String name:_selectedUserNames) {
                        if(index != _selectedUserNames.size() - 1)
                            namesString += (name + ", ");
                        else
                            namesString += name;

                        index++;
                    }
                    selectUsers.setText(namesString);
                }
                break;

            case RESULT_CANCELED:

                break;
        }
    }

    @Override
    public void OnSuccess(RequestType requestType, Object o)
    {
        if(_obtainingSession)
        {
            _obtainingSession = false;
            return;
        }
        Toast.makeText(getApplicationContext(), "Bill successfully uploaded", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void OnFail(RequestType requestType, String message)
    {
        Snackbar.make(layout, message, Snackbar.LENGTH_LONG).show();
        ReenableElements();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(_session.HasSessionID() == false)
        {
            _obtainingSession = true;
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

            if(account != null)
            {
                JSONObject tokenJson = new JSONObject();
                try
                {
                    tokenJson.put("Token", account.getIdToken());
                }
                catch (JSONException e)
                { }
                _logInEndpoint.Post(this, this, tokenJson);
            }
            else
            {
                Intent signIn = new Intent(this, SignInActivity.class);
                signIn.putExtra("IrregularStart", true);
                startActivity(signIn);
            }
        }
    }
}
