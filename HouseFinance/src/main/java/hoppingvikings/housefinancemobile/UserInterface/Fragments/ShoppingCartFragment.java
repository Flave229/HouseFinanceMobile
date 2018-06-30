package hoppingvikings.housefinancemobile.UserInterface.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.Services.SaltVault.User.UserEndpoint;
import hoppingvikings.housefinancemobile.HouseFinanceClass;
import hoppingvikings.housefinancemobile.Person;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Activities.AddNewShoppingItemActivity;
import hoppingvikings.housefinancemobile.UserInterface.Fragments.Interfaces.ButtonPressedCallback;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingCartList.ShoppingCartAdapter;
import hoppingvikings.housefinancemobile.UserInterface.Items.ShoppingCartItem;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;

public class ShoppingCartFragment extends Fragment
        implements ButtonPressedCallback, CommunicationCallback<ArrayList<Person>>
{
    View currentView;
    TextView cartEmptyText;
    CoordinatorLayout layout;
    RecyclerView rv;
    ShoppingCartAdapter adapter;
    ArrayList<ShoppingCartItem> items;
    AddNewShoppingItemActivity _activity;
    boolean submitting = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        UserEndpoint userEndpoint = HouseFinanceClass.GetUserComponent().GetUserEndpoint();
        userEndpoint.Get(getContext(), this);
        currentView = inflater.inflate(R.layout.fragment_shoppingcart, container, false);
        _activity = (AddNewShoppingItemActivity)getActivity();
        //_activity.SetCallbackOwner(this);
        //_activity.addToCartButton.setText("Submit");
        //_activity.addToCartButton.setEnabled(true);

        cartEmptyText = (TextView) currentView.findViewById(R.id.cartEmptyText);
        layout = (CoordinatorLayout) currentView.findViewById(R.id.coordlayout);
        rv = (RecyclerView) currentView.findViewById(R.id.cartList);
        items = new ArrayList<>();

//        if(_activity._shoppingItems.size() < 1)
//        {
//            cartEmptyText.setVisibility(View.VISIBLE);
//            _activity.addToCartButton.setEnabled(false);
//        }

        return currentView;
    }

    @Override
    public void SubmitPressed() {
        _activity.onBackPressed();
    }

    @Override
    public void AddToCartPressed()
    {
        final AlertDialog submitcheck = new AlertDialog.Builder(getContext()).create();

        submitcheck.setMessage("Please check all items are correct before submitting");
        submitcheck.setButton(DialogInterface.BUTTON_POSITIVE, "Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                submitcheck.dismiss();

//                FileIOHandler fileIO = new FileIOHandler();
//
//                for (String item: _activity._shoppingItems) {
//                    //fileIO.WriteToFile(FileName.SHOPPING_RECENT_ITEMS, item);
//                }

                submitting = true;
                //_activity.progress = 100 / _activity._shoppingItems.size();
                //_activity.addToCartButton.setEnabled(false);
                try {
                    //WebHandler.Instance().UploadNewItem(getContext(), new JSONObject(_activity._shoppingItems.get(0)), _activity, ItemType.SHOPPING);
                } catch (Exception e)
                {

                }
            }
        });
        submitcheck.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                submitcheck.dismiss();
            }
        });
        submitcheck.show();
    }

    @Override
    public void OnSuccess(RequestType requestType, ArrayList<Person> users)
    {
        try
        {
//            for (String jsonstring: _activity._shoppingItems) {
//                JSONObject json = new JSONObject(jsonstring);
//                items.add(new ShoppingCartItem(json));
//            }
        }
        catch (Exception e)
        { }

        adapter = new ShoppingCartAdapter(items, getContext());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(adapter.getItemCount() > 0)
        {
            cartEmptyText.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnFail(RequestType requestType, String message)
    { }
}
