package hoppingvikings.housefinancemobile.UserInterface.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import hoppingvikings.housefinancemobile.ApiErrorCodes;
import hoppingvikings.housefinancemobile.FileIOHandler;
import hoppingvikings.housefinancemobile.Services.SaltVault.ToDo.ToDoEndpoint;
import hoppingvikings.housefinancemobile.Services.SaltVault.User.LogInEndpoint;
import hoppingvikings.housefinancemobile.HouseFinanceClass;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.SignInActivity;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.SessionPersister;

public class AddNewToDoActivity extends AppCompatActivity implements CommunicationCallback
{
    private SessionPersister _session;
    private LogInEndpoint _logInEndpoint;
    private ToDoEndpoint _toDoEndpoint;

    Button submitButton;
    TextInputLayout taskTitleEntry;
    TextInputEditText taskTitleEntryText;

    TextInputLayout taskDueDateEntry;
    TextInputEditText taskDueDateEntryText;

    ArrayList<Integer> _selectedPeopleIDs = new ArrayList<>();
    ArrayList<String> _selectedPeopleNames = new ArrayList<>();

    CoordinatorLayout layout;
    TextView selectedPeople;
    ImageButton editPeople;

    String taskTitle;
    Date taskDueDate;

    boolean _obtainingSession = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewtask);

        _session = HouseFinanceClass.GetSessionPersisterComponent().GetSessionPersister();
        _logInEndpoint = HouseFinanceClass.GetUserComponent().GetLogInEndpoint();
        _toDoEndpoint = HouseFinanceClass.GetToDoComponent().GetToDoEndpoint();

        Toolbar toolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        layout = findViewById(R.id.coordlayout);

        submitButton = findViewById(R.id.submitTask);
        taskTitleEntry = findViewById(R.id.taskTitleEntry);
        taskTitleEntryText = findViewById(R.id.taskTitleEntryText);
        taskDueDateEntry = findViewById(R.id.taskDueDateEntry);
        taskDueDateEntryText = findViewById(R.id.taskDueDateEntryText);

        selectedPeople = findViewById(R.id.selectUsers);
        editPeople = findViewById(R.id.editPeople);
        editPeople.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent selectPeople = new Intent(AddNewToDoActivity.this, SelectUsersActivity.class);
                selectPeople.putExtra("multiple_user_selection", true);
                if(_selectedPeopleIDs.size() > 0)
                    selectPeople.putExtra("currently_selected_ids", _selectedPeopleIDs);

                startActivityForResult(selectPeople, 0);
            }
        });

        final Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
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
                taskDueDateEntryText.setText(sdf.format(gc.getTime()));
                taskDueDateEntry.setError(null);
            }
        };

        taskDueDateEntryText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new DatePickerDialog(AddNewToDoActivity.this, date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!ValidateFields())
                    return;

                final AlertDialog confirmCancel = new AlertDialog.Builder(AddNewToDoActivity.this).create();
                confirmCancel.setTitle("Submit new task?");
                confirmCancel.setMessage("Please check that all details are correct");

                confirmCancel.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmCancel.dismiss();

                        taskTitleEntry.setEnabled(false);
                        taskDueDateEntry.setEnabled(false);
                        editPeople.setEnabled(false);
                        submitButton.setEnabled(false);

                        JSONObject newTask = new JSONObject();
                        try {
                            newTask.put("Title", taskTitle);

                            if(taskDueDate != null)
                                newTask.put("Due", new SimpleDateFormat("yyyy-MM-dd").format(taskDueDate));

                            JSONArray people = new JSONArray();
                            for(int id : _selectedPeopleIDs)
                                people.put(id);

                            newTask.put("PeopleIds", people);

                            _toDoEndpoint.Post(getApplicationContext(), AddNewToDoActivity.this, newTask);

                        } catch (JSONException je)
                        {
                            Snackbar.make(layout, "Failed to create JSON for task", Snackbar.LENGTH_LONG).show();
                            ReenableElements();
                        }
                    }
                });

                confirmCancel.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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

            _selectedPeopleIDs.add(userId);
            _selectedPeopleNames.add(username);

            StringBuilder namesString = new StringBuilder();
            int index = 0;
            for (String name:_selectedPeopleNames)
            {
                if(index != _selectedPeopleNames.size() - 1)
                    namesString.append(name).append(", ");
                else
                    namesString.append(name);

                index++;
            }
            selectedPeople.setText(namesString);
        } catch (JSONException je)
        {

        }
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
        if(taskTitleEntryText.getText().length() == 0
                && taskDueDateEntryText.getText().length() == 0)
        {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
            return;
        }

        final AlertDialog confirmCancel = new AlertDialog.Builder(this).create();

        confirmCancel.setTitle("Cancel task entry?");
        confirmCancel.setMessage("All details entered will be lost.");

        confirmCancel.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                confirmCancel.dismiss();
                setResult(RESULT_CANCELED);
                AddNewToDoActivity.super.onBackPressed();
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
        taskTitleEntry.setEnabled(true);
        taskDueDateEntry.setEnabled(true);
        submitButton.setEnabled(true);
        editPeople.setEnabled(true);
    }

    private boolean ValidateFields()
    {
        if(taskTitleEntryText.getText().length() > 0)
        {
            taskTitle = taskTitleEntryText.getText().toString();
            taskTitleEntry.setError(null);
        }
        else
        {
            taskTitleEntryText.requestFocus();
            taskTitleEntry.setError("Please enter a valid task title");
            return false;
        }

        // Date is optional. Don't break out of there is no date entered
        if(taskDueDateEntryText.getText().length() > 0)
        {
            try
            {
                taskDueDate = new SimpleDateFormat("dd-MM-yyyy").parse(taskDueDateEntryText.getText().toString());
            }
            catch (ParseException pe)
            {
                taskDueDate = null;
            }
        }
        else
        {
            taskDueDate = null;
        }

        if(_selectedPeopleIDs.size() < 1)
        {
            Snackbar.make(layout, "Please select at least one person for this task", Snackbar.LENGTH_LONG).show();
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
                    _selectedPeopleIDs = data.getIntegerArrayListExtra("selected_ids");
                    _selectedPeopleNames = data.getStringArrayListExtra("selected_names");

                    StringBuilder namesString = new StringBuilder();

                    int index = 0;
                    for (String name : _selectedPeopleNames) {
                        if(index != _selectedPeopleNames.size() - 1)
                            namesString.append(name).append(", ");
                        else
                            namesString.append(name);

                        index++;
                    }
                    selectedPeople.setText(namesString);
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

        Toast.makeText(getApplicationContext(), "Task Successfully Added", Toast.LENGTH_LONG).show();
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
                                Intent signInRefresh = new Intent(AddNewToDoActivity.this, SignInActivity.class);
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
                } catch (JSONException e)
                {

                }
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
