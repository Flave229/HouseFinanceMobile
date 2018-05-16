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
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.Repositories.TodoRepository;
import hoppingvikings.housefinancemobile.UserInterface.Items.TodoListObject;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

public class EditTodoItemActivity extends AppCompatActivity
    implements CommunicationCallback
{

    Button submitButton;
    TextInputLayout taskTitleEntry;
    TextInputEditText taskTitleEntryText;
    TextInputLayout taskDueDateEntry;
    TextInputEditText taskDueDateEntryText;

    ArrayList<Integer> _selectedPeopleIds = new ArrayList<>();
    ArrayList<String> _selectedPeopleNames = new ArrayList<>();

    TextView selectedPeople;
    ImageButton editPeople;

    CheckBox editTitle;
    CheckBox editDueDate;
    CheckBox editTaskFor;

    String taskTitle;
    Date taskDueDate;

    CoordinatorLayout layout;

    TodoListObject task = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittodoitem);

        Toolbar toolbar = findViewById(R.id.appToolbar);
        toolbar.setTitle("Edit Todo Task");
        toolbar.setSubtitle("Tick the fields you wish to edit");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent() != null && getIntent().hasExtra("id"))
        {
            task = TodoRepository.Instance().GetFromId(getIntent().getIntExtra("id", -1));
        }

        layout = findViewById(R.id.coordlayout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        submitButton = findViewById(R.id.submitTask);
        taskTitleEntry = findViewById(R.id.taskTitleEntry);
        taskTitleEntryText = findViewById(R.id.taskTitleEntryText);
        taskDueDateEntry = findViewById(R.id.taskDueDateEntry);
        taskDueDateEntryText = findViewById(R.id.taskDueDateEntryText);

        taskTitleEntry.setEnabled(false);
        taskTitleEntryText.setEnabled(false);
        taskDueDateEntry.setEnabled(false);
        taskDueDateEntryText.setEnabled(false);

        selectedPeople = findViewById(R.id.selectUsers);
        editPeople = findViewById(R.id.editPeople);
        editPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectPeople =  new Intent(EditTodoItemActivity.this, SelectUsersActivity.class);
                selectPeople.putExtra("multiple_user_selection", true);
                if(_selectedPeopleIds.size() > 0)
                    selectPeople.putExtra("currently_selected_ids", _selectedPeopleIds);

                startActivityForResult(selectPeople, 0);
            }
        });

        editPeople.setEnabled(false);
        editPeople.setAlpha(0.3f);

        if(task != null)
        {
            taskTitleEntryText.setText(task.title);
            taskDueDateEntryText.setText(task.dueDate);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Task not found", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
            finish();
        }

        editTitle = findViewById(R.id.editTitleCheck);
        editTaskFor = findViewById(R.id.editForCheck);
        editDueDate = findViewById(R.id.editDueDateCheck);

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
                taskDueDateEntryText.setText(sdf.format(gc.getTime()));
                taskDueDateEntry.setError(null);
            }
        };

        taskDueDateEntryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(EditTodoItemActivity.this, date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editTitle.isChecked() && !editTaskFor.isChecked() && !editDueDate.isChecked())
                {
                    Snackbar.make(layout, "At least one field should be edited before submitting", Snackbar.LENGTH_LONG).show();
                    return;
                }

                SubmitEditedTodoItem();
            }
        });
    }

    private void SetEditCheckListeners()
    {
        editTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTitle.isChecked())
                {
                    taskTitleEntry.setEnabled(true);
                    taskTitleEntryText.setEnabled(true);
                    taskTitleEntryText.requestFocus();
                }
                else
                {
                    taskTitleEntry.setEnabled(false);
                    taskTitleEntryText.setEnabled(false);
                }
            }
        });

        editDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editDueDate.isChecked())
                {
                    taskDueDateEntry.setEnabled(true);
                    taskDueDateEntryText.setEnabled(true);
                    taskDueDateEntryText.requestFocus();
                }
                else
                {
                    taskDueDateEntry.setEnabled(false);
                    taskDueDateEntryText.setEnabled(false);
                }
            }
        });

        editTaskFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTaskFor.isChecked())
                {
                    editPeople.setEnabled(true);
                    editPeople.setAlpha(1.0f);
                }
                else
                {
                    editPeople.setEnabled(false);
                    editPeople.setAlpha(0.3f);
                }
            }
        });
    }

    private void SubmitEditedTodoItem()
    {
        if(!ValidateFields())
        {
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
                JSONObject editedTask = new JSONObject();
                try {
                    editedTask.put("Id", task.id);
                    if(editTitle.isChecked())
                        editedTask.put("Title", taskTitle);

                    if(editDueDate.isChecked())
                        editedTask.put("Due", new SimpleDateFormat("yyyy-MM-dd").format(taskDueDate));

                    if(editTaskFor.isChecked())
                    {
                        JSONArray people = new JSONArray();
                        for (int id : _selectedPeopleIds) {
                            people.put(id);
                        }

                        editedTask.put("PeopleIds", people);
                    }

                    WebHandler.Instance().EditItem(EditTodoItemActivity.this, editedTask, EditTodoItemActivity.this, ItemType.TODO);

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
        if(taskTitleEntryText.getText().length() > 0) {
            taskTitle = taskTitleEntryText.getText().toString();
            taskTitleEntry.setError(null);
        }
        else {
            taskTitleEntryText.requestFocus();
            taskTitleEntry.setError("Please enter a valid Task title");
            return false;
        }

        if(editDueDate.isChecked() && taskDueDateEntryText.getText().length() > 0)
        {
            try {
                taskDueDate = new SimpleDateFormat("dd-MM-yyyy").parse(taskDueDateEntryText.getText().toString());
                taskDueDateEntry.setError(null);
            } catch (ParseException pe)
            {
                taskDueDate = null;
            }
        }

        if(editTaskFor.isChecked())
        {
            if(_selectedPeopleIds.size() < 1)
            {
                Snackbar.make(layout, "Please select at least one person for this task", Snackbar.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
            case RESULT_OK:
                if(data != null)
                {
                    _selectedPeopleIds = data.getIntegerArrayListExtra("selected_ids");
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
    public void OnSuccess(RequestType requestType, Object o) {
        Toast.makeText(getApplicationContext(), "Edit successfully uploaded", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void OnFail(RequestType requestType, String message) {
        Snackbar.make(layout, message, Snackbar.LENGTH_LONG).show();
    }
}
