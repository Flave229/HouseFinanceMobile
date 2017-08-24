package hoppingvikings.housefinancemobile.UserInterface.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Activities.AddNewBillActivity;
import hoppingvikings.housefinancemobile.UserInterface.Activities.AddNewShoppingItemActivity;
import hoppingvikings.housefinancemobile.UserInterface.Activities.SelectUsersActivity;
import hoppingvikings.housefinancemobile.UserInterface.Fragments.Interfaces.ButtonPressedCallback;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by iView on 25/07/2017.
 */

public class AddShoppingItemFragment extends Fragment implements ButtonPressedCallback{
    View _currentView;

    TextInputLayout itemNameLayout;
    TextInputEditText shoppingItemNameEntry;

    TextView selectUser;
    ImageButton editPerson;

    TextView selectUsers;
    ImageButton editPeople;

    String itemName;

    ArrayList<Integer> _selectedUserIds = new ArrayList<>();
    ArrayList<String> _selectedUserNames = new ArrayList<>();
    int _selectedUserId = -1;
    String _selectedUserName = "";

    CoordinatorLayout layout;

    AddNewShoppingItemActivity _activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _currentView = inflater.inflate(R.layout.fragment_shoppingform, container, false);
        _activity = (AddNewShoppingItemActivity)getActivity();
        _activity.SetCallbackOwner(this);
        _activity.addToCartButton.setEnabled(true);
        _activity.addToCartButton.setText("Add to Cart");

        if(_activity._shoppingItems.size() > 0)
        {
            _activity.submitButton.setText("View Cart");
        }
        else
        {
            _activity.submitButton.setText("Submit");
        }

        layout = (CoordinatorLayout) _currentView.findViewById(R.id.coordlayout);

        itemNameLayout = (TextInputLayout)  _currentView.findViewById(R.id.itemNameLayout);
        shoppingItemNameEntry = (TextInputEditText)  _currentView.findViewById(R.id.ShoppingItemNameEntry);

        selectUsers = (TextView) _currentView.findViewById(R.id.selectUsers);
        editPeople = (ImageButton) _currentView.findViewById(R.id.editPeople);
        editPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectusers = new Intent(getContext(), SelectUsersActivity.class);
                selectusers.putExtra("multiple_user_selection", true);
                if(_selectedUserIds.size() > 0)
                {
                    selectusers.putExtra("currently_selected_ids", _selectedUserIds);
                }
                startActivityForResult(selectusers, 0);
            }
        });

        selectUser = (TextView) _currentView.findViewById(R.id.selectUser);
        editPerson = (ImageButton) _currentView.findViewById(R.id.editPerson);
        editPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectusers = new Intent(getContext(), SelectUsersActivity.class);
                if(_selectedUserId > -1)
                {
                    selectusers.putExtra("currently_selected_id", _selectedUserId);
                }
                startActivityForResult(selectusers, 1);
            }
        });

        return _currentView;
    }

    private boolean ValidateFields()
    {
        if(shoppingItemNameEntry.getText().length() > 0) {
            itemName = shoppingItemNameEntry.getText().toString();
            itemNameLayout.setError(null);
        } else {
            itemNameLayout.setError("Please enter a valid Item name");
            return false;
        }

        if(_selectedUserIds.size() < 1)
        {
            Snackbar.make(layout, "Please select at least one person for this item", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(_selectedUserId == -1)
        {
            Snackbar.make(layout, "Please select who is adding the item", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void ReenableElements()
    {
        shoppingItemNameEntry.setEnabled(true);

        editPeople.setEnabled(true);
        editPerson.setEnabled(true);

        _activity.ReenableElements();
    }

    @Override
    public void SubmitPressed() {
        if(_activity._shoppingItems.size() > 0)
        {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ShoppingCartFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(null)
                    .commit();

            return;
        }

        if(!ValidateFields())
        {
            _activity.ReenableElements();
            return;
        }

        final AlertDialog confirmCancel = new AlertDialog.Builder(getContext()).create();

        confirmCancel.setTitle("Submit Item?");
        confirmCancel.setMessage("Please check all details are correct before continuing");

        confirmCancel.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmCancel.dismiss();
            }
        });

        confirmCancel.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmCancel.dismiss();

                shoppingItemNameEntry.setEnabled(false);
                editPerson.setEnabled(false);
                editPeople.setEnabled(false);

                JSONObject newItem = new JSONObject();

                try{
                    newItem.put("Name", itemName);
                    newItem.put("Added", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                    newItem.put("AddedBy", _selectedUserId);

                    JSONArray people = new JSONArray();

                    for (int id : _selectedUserIds) {
                        people.put(id);
                    }

                    newItem.put("ItemFor", people);

                    // Add the item to a file on the device
                    //GlobalObjects.WriteToFile(getContext(), newItem.toString());

                    GlobalObjects.webHandler.UploadNewItem(getContext(), newItem, _activity, GlobalObjects.ITEM_TYPE_SHOPPING);
                } catch (JSONException je)
                {
                    Snackbar.make(layout, "Failed to create Json", Snackbar.LENGTH_LONG).show();
                    ReenableElements();
                }
            }
        });

        confirmCancel.show();
    }

    @Override
    public void AddToCartPressed() {

        if(!ValidateFields())
        {
            return;
        }

        JSONObject newItem = new JSONObject();

        try{
            newItem.put("Name", itemName);
            newItem.put("Added", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            newItem.put("AddedBy", _selectedUserId);

            JSONArray people = new JSONArray();

            for (int id : _selectedUserIds) {
                people.put(id);
            }

            newItem.put("ItemFor", people);

            _activity._shoppingItems.add(newItem.toString());
            Toast.makeText(getContext(), "Item added to cart", Toast.LENGTH_SHORT).show();
            _activity.getSupportActionBar().setSubtitle("Items in cart: " + String.valueOf(_activity._shoppingItems.size()));

            _activity.submitButton.setText("View Cart");
            //GlobalObjects.webHandler.UploadNewShoppingItem(getApplicationContext(), newItem, AddNewShoppingItemActivity.this);
        } catch (JSONException je)
        {
            Snackbar.make(layout, "Failed to create Json", Snackbar.LENGTH_LONG).show();
            ReenableElements();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case 0:
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
                break;

            case 1:
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
                break;
        }
    }
}
