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
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONArray;
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
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;
import hoppingvikings.housefinancemobile.WebService.UploadCallback;

/**
 * Created by iView on 10/08/2017.
 */

public class EditBillDetailsActivity extends AppCompatActivity implements UploadCallback {

    Button submitButton;

    CheckBox editName;
    CheckBox editAmount;
    CheckBox editDate;
    CheckBox editFor;
    CheckBox editType;

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
    BillListObject bill = null;

    CoordinatorLayout layout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editbill);

        if(getIntent() != null && getIntent().hasExtra("bill_id"))
        {
            bill = GlobalObjects.GetBillFromID(getIntent().getIntExtra("bill_id", -1));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.appToolbar);
        toolbar.setTitle("Edit Bill");
        toolbar.setSubtitle("Tick the fields you wish to edit");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layout = (CoordinatorLayout) findViewById(R.id.coordlayout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        submitButton = (Button) findViewById(R.id.submitBill);
        billNameEntry = (TextInputLayout) findViewById(R.id.BillNameEntry);
        billNameEntryText = (TextInputEditText) findViewById(R.id.BillNameEntryText);
        billNameEntry.setEnabled(false);
        billNameEntryText.setEnabled(false);
        billAmountEntry = (TextInputLayout) findViewById(R.id.BillAmountEntry);
        billAmountEntryText = (TextInputEditText) findViewById(R.id.BillAmountEntryText);
        billAmountEntry.setEnabled(false);
        billAmountEntryText.setEnabled(false);
        billDueDateEntry = (TextInputLayout) findViewById(R.id.billDueDateEntry);
        billDueDateEntryText = (TextInputEditText) findViewById(R.id.billDueDateEntryText);
        billDueDateEntry.setEnabled(false);
        billDueDateEntryText.setEnabled(false);
        billDueDateEntryText.setInputType(InputType.TYPE_NULL);

        davidCheck = (CheckBox) findViewById(R.id.CheckBoxDavid);
        davidCheck.setEnabled(false);
        davidCheck.setChecked(false);
        vikkiCheck = (CheckBox) findViewById(R.id.CheckBoxVikki);
        vikkiCheck.setChecked(false);
        joshCheck = (CheckBox) findViewById(R.id.CheckBoxJosh);
        joshCheck.setEnabled(false);
        joshCheck.setChecked(false);

        regularRadio = (RadioButton) findViewById(R.id.BillTypeRegular);
        regularRadio.setEnabled(false);
        recurRadio = (RadioButton) findViewById(R.id.BillTypeRecur);
        recurRadio.setEnabled(false);

        editName = (CheckBox) findViewById(R.id.editNameCheck);
        editAmount = (CheckBox) findViewById(R.id.editAmountCheck);
        editDate = (CheckBox) findViewById(R.id.editDateCheck);
        editFor = (CheckBox) findViewById(R.id.editForCheck);
        editType = (CheckBox) findViewById(R.id.editTypeCheck);

        SetEditCheckListeners();

        if(bill != null)
        {
            billNameEntryText.setText(bill.billName);
            billAmountEntryText.setText(bill.billTotalAmount);
            billDueDateEntryText.setText(bill.billDate);
            /*for (BillListObjectPeople person : bill.people) {
                switch (person.ID)
                {
                    case GlobalObjects.USERGUID_DAVE:
                        davidCheck.setChecked(true);
                        break;

                    case GlobalObjects.USERGUID_JOSH:
                        joshCheck.setChecked(true);
                        break;
                }
            }*/
        }
        else
        {
            Toast.makeText(getBaseContext(), "No bill was provided", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

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

                new DatePickerDialog(EditBillDetailsActivity.this, date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editName.isChecked() && !editAmount.isChecked() && !editDate.isChecked() && !editFor.isChecked() && !editType.isChecked())
                {
                    Snackbar.make(layout, "Please edit at least one field to submit", Snackbar.LENGTH_LONG).show();
                    return;
                }

                SubmitEditedBill();
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
                    billNameEntry.setEnabled(true);
                    billNameEntryText.setEnabled(true);
                    billNameEntryText.requestFocus();
                }
                else
                {
                    billNameEntry.setEnabled(false);
                    billNameEntryText.setEnabled(false);

                    if(bill != null)
                        billNameEntryText.setText(bill.billName);
                }
            }
        });

        editAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editAmount.isChecked())
                {
                    billAmountEntry.setEnabled(true);
                    billAmountEntryText.setEnabled(true);
                    billAmountEntryText.requestFocus();
                }
                else
                {
                    billAmountEntry.setEnabled(false);
                    billAmountEntryText.setEnabled(false);
                    if(bill != null)
                        billAmountEntryText.setText(bill.billTotalAmount);
                }
            }
        });

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editDate.isChecked())
                {
                    billDueDateEntry.setEnabled(true);
                    billDueDateEntryText.setEnabled(true);
                }
                else
                {
                    billDueDateEntry.setEnabled(false);
                    billDueDateEntryText.setEnabled(false);
                    if(bill != null)
                        billDueDateEntryText.setText(bill.billDate);
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

        editType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editType.isChecked())
                {
                    regularRadio.setEnabled(true);
                    recurRadio.setEnabled(true);
                }
                else
                {
                    regularRadio.setEnabled(false);
                    recurRadio.setEnabled(false);
                }
            }
        });
    }

    private void SubmitEditedBill()
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
                    editedBill.put("Id", bill.ID);
                    if(editName.isChecked())
                        editedBill.put("Name", billNameEntryText.getText().toString());

                    if(editAmount.isChecked())
                        editedBill.put("TotalAmount", Double.valueOf(billAmountEntryText.getText().toString()));

                    if(editDate.isChecked())
                    {
                        Date editedDate = new SimpleDateFormat("dd-MM-yyyy").parse(billDueDateEntryText.getText().toString());
                        editedBill.put("Due", new SimpleDateFormat("yyyy-MM-dd").format(editedDate));
                    }

                    if(editFor.isChecked())
                    {
                        JSONArray people = new JSONArray();
                        if(davidCheck.isChecked())
                            people.put(GlobalObjects.USERGUID_DAVE);

                        if(joshCheck.isChecked())
                            people.put(GlobalObjects.USERGUID_JOSH);

                        editedBill.put("PeopleIds", people);
                    }

                    if(editType.isChecked())
                    {
                        if(recurRadio.isChecked())
                            editedBill.put("RecurringType", 1);
                        else
                            editedBill.put("RecurringType", 0);
                    }

                    GlobalObjects.webHandler.EditItem(EditBillDetailsActivity.this, editedBill, EditBillDetailsActivity.this, GlobalObjects.ITEM_TYPE_BILL);

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
