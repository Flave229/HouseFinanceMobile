package hoppingvikings.housefinancemobile.UserInterface.Fragments;

import android.content.DialogInterface;
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
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Activities.AddNewShoppingItemActivity;
import hoppingvikings.housefinancemobile.UserInterface.Fragments.Interfaces.ButtonPressedCallback;

/**
 * Created by iView on 25/07/2017.
 */

public class AddShoppingItemFragment extends Fragment implements ButtonPressedCallback{
    View _currentView;

    TextInputLayout itemNameLayout;
    TextInputEditText shoppingItemNameEntry;

    CheckBox davidCheck;
    CheckBox vikkiCheck;
    CheckBox joshCheck;

    RadioButton davidRadio;
    RadioButton vikkiRadio;
    RadioButton joshRadio;

    String itemName;

    boolean forDavid;
    boolean forVikki;
    boolean forJosh;

    boolean fromDavid;
    boolean fromVikki;
    boolean fromJosh;

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

        davidCheck = (CheckBox) _currentView.findViewById(R.id.CheckBoxDavid);
        vikkiCheck = (CheckBox) _currentView.findViewById(R.id.CheckBoxVikki);
        joshCheck = (CheckBox)  _currentView.findViewById(R.id.CheckBoxJosh);

        davidRadio = (RadioButton)  _currentView.findViewById(R.id.davidRadio);
        vikkiRadio = (RadioButton)  _currentView.findViewById(R.id.vikkiRadio);
        joshRadio = (RadioButton)  _currentView.findViewById(R.id.joshRadio);

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

        forDavid = davidCheck.isChecked();
        forVikki = vikkiCheck.isChecked();
        forJosh = joshCheck.isChecked();

        if(!forDavid && !forVikki && !forJosh)
        {
            Snackbar.make(layout, "Please select at least one person for this item", Snackbar.LENGTH_LONG).show();
            return false;
        }

        fromDavid = davidRadio.isChecked();
        fromVikki = vikkiRadio.isChecked();
        fromJosh = joshRadio.isChecked();

        if(!fromDavid && !fromJosh)
        {
            Snackbar.make(layout, "Please select who is adding the item", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void ReenableElements()
    {
        shoppingItemNameEntry.setEnabled(true);

        davidCheck.setEnabled(true);
        vikkiCheck.setEnabled(true);
        joshCheck.setEnabled(true);

        davidRadio.setEnabled(true);
        vikkiRadio.setEnabled(true);
        joshRadio.setEnabled(true);
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
                davidCheck.setEnabled(false);
                vikkiCheck.setEnabled(false);
                joshCheck.setEnabled(false);
                davidRadio.setEnabled(false);
                vikkiRadio.setEnabled(false);
                joshRadio.setEnabled(false);

                JSONObject newItem = new JSONObject();

                try{
                    newItem.put("Name", itemName);
                    newItem.put("Added", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                    if(fromDavid)
                        newItem.put("AddedBy", GlobalObjects.USERGUID_DAVE);
                    else if(fromVikki)
                        newItem.put("AddedBy", "25c15fb4-b5d5-47d9-917b-c572b1119e65");
                    else if(fromJosh)
                        newItem.put("AddedBy", GlobalObjects.USERGUID_JOSH);

                    JSONArray people = new JSONArray();

                    if(forDavid)
                        people.put(GlobalObjects.USERGUID_DAVE);

                    /*if(forVikki)
                        people.put("25c15fb4-b5d5-47d9-917b-c572b1119e65");*/

                    if(forJosh)
                        people.put(GlobalObjects.USERGUID_JOSH);

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

            if(fromDavid)
                newItem.put("AddedBy", GlobalObjects.USERGUID_DAVE);
            else if(fromVikki)
                newItem.put("AddedBy", "25c15fb4-b5d5-47d9-917b-c572b1119e65");
            else if(fromJosh)
                newItem.put("AddedBy", GlobalObjects.USERGUID_JOSH);

            JSONArray people = new JSONArray();

            if(forDavid)
                people.put(GlobalObjects.USERGUID_DAVE);

            if(forVikki)
                people.put("25c15fb4-b5d5-47d9-917b-c572b1119e65");

            if(forJosh)
                people.put(GlobalObjects.USERGUID_JOSH);

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
}
