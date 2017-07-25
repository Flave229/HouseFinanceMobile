package hoppingvikings.housefinancemobile.UserInterface.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.AddNewShoppingItemActivity;
import hoppingvikings.housefinancemobile.UserInterface.Fragments.Interfaces.ButtonPressedCallback;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingCartList.ShoppingCartAdapter;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingCartList.ShoppingCartItem;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingList.ShoppingListAdapter;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingList.ShoppingListObject;

/**
 * Created by iView on 25/07/2017.
 */

public class ShoppingCartFragment extends Fragment implements ButtonPressedCallback, ShoppingCartAdapter.DeleteCallback {
    View currentView;
    TextView cartEmptyText;
    CoordinatorLayout layout;
    RecyclerView rv;
    ShoppingCartAdapter adapter;
    ArrayList<ShoppingCartItem> items;
    AddNewShoppingItemActivity _activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_shoppingcart, container, false);
        _activity = (AddNewShoppingItemActivity)getActivity();
        _activity.SetCallbackOwner(this);
        _activity.submitButton.setText("Go Back");
        _activity.addToCartButton.setText("Submit Items");
        _activity.addToCartButton.setEnabled(false);

        cartEmptyText = (TextView) currentView.findViewById(R.id.cartEmptyText);
        layout = (CoordinatorLayout) currentView.findViewById(R.id.coordlayout);
        rv = (RecyclerView) currentView.findViewById(R.id.cartList);
        items = new ArrayList<>();

        try {
            for (String jsonstring: _activity._shoppingItems) {
                JSONObject json = new JSONObject(jsonstring);
                items.add(new ShoppingCartItem(json));
            }
        } catch (Exception e)
        {

        }
        adapter = new ShoppingCartAdapter(items, getContext());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.SetDeleteCallback(this);

        if(adapter.getItemCount() > 0)
        {
            cartEmptyText.setVisibility(View.GONE);
        }

        return currentView;
    }

    @Override
    public void SubmitPressed() {
        _activity.onBackPressed();
    }

    @Override
    public void AddToCartPressed() {

    }

    @Override
    public void onItemDeleted(int item) {
        _activity._shoppingItems.remove(item);
        if(_activity._shoppingItems.size() < 1)
        {
            cartEmptyText.setVisibility(View.VISIBLE);
        }
    }
}
