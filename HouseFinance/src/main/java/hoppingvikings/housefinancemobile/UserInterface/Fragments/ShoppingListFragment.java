package hoppingvikings.housefinancemobile.UserInterface.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.AddNewShoppingItemActivity;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ListItemDivider;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingList.ShoppingListAdapter;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingList.ShoppingListObject;
import hoppingvikings.housefinancemobile.WebService.DownloadCallback;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Josh on 24/09/2016.
 */

public class ShoppingListFragment extends Fragment implements DownloadCallback {

    CoordinatorLayout layout;
    Handler _handler;
    RecyclerView rv;
    ShoppingListAdapter adapter;
    ArrayList<ShoppingListObject> items;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton addItemButton;

    private Runnable contactWebsite = new Runnable() {
        @Override
        public void run() {
            GlobalObjects._service.contactWebsiteShoppingItems(getContext(), ShoppingListFragment.this);
        }
    };

    private Runnable updateList = new Runnable() {
        @Override
        public void run() {

            if(!GlobalObjects.downloading)
            {
                if (GlobalObjects.GetShoppingItems() != null) {
                    if(items != null)
                    {
                        items.clear();
                        items.addAll(GlobalObjects.GetShoppingItems());
                        adapter.addAll(items);
                    }
                    else
                    {
                        items = new ArrayList<>();
                        items.addAll(GlobalObjects.GetShoppingItems());
                        adapter.addAll(items);
                    }
                    if (adapter.getItemCount() != items.size()) {
                        _handler.post(contactWebsite);
                    } else {

                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
                else
                {
                    _handler.post(contactWebsite);
                }
            }
            else
            {
                _handler.postDelayed(this, 3000);
            }
        }
    };

    public ShoppingListFragment()
    {
        // Blank Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        layout = (CoordinatorLayout)view.findViewById(R.id.coordlayout);
        _handler = new Handler();
        addItemButton = (FloatingActionButton)view.findViewById(R.id.addItem);

        rv = (RecyclerView) view.findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);

        if(GlobalObjects.GetShoppingItems() != null && GlobalObjects.GetShoppingItems().size() != 0)
        {
            items = new ArrayList<>();
            items.addAll(GlobalObjects.GetShoppingItems());
        }
        else
        {
            items = new ArrayList<>();
        }

        if(rv != null)
        {
            adapter = new ShoppingListAdapter(items, getContext());
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(getActivity()));
            rv.addItemDecoration(new ListItemDivider(getContext()));
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _handler.post(contactWebsite);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayout.setRefreshing(true);
        _handler.postDelayed(contactWebsite, 200);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItem = new Intent(getContext(), AddNewShoppingItemActivity.class);
                startActivityForResult(addItem, 0);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
            case RESULT_OK:
                swipeRefreshLayout.setRefreshing(true);
                _handler.removeCallbacksAndMessages(null);
                _handler.postDelayed(contactWebsite, 200);
                break;

            case RESULT_CANCELED:

                break;
        }
    }

    @Override
    public void OnSuccessfulDownload() {
        // After calling the website, allow 3 seconds before we update the list. Can be reduced if needed
        _handler.postDelayed(updateList, 1000);
    }

    @Override
    public void OnFailedDownload(String failReason) {
        _handler.removeCallbacksAndMessages(null);

        if(!failReason.equals("No internet connection"))
        {
            Snackbar.make(layout, failReason + ". Retrying...", Snackbar.LENGTH_LONG).show();
            _handler.postDelayed(contactWebsite, 500);
        }
        else
        {
            Snackbar.make(layout, failReason, Snackbar.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
