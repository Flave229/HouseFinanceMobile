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
import hoppingvikings.housefinancemobile.UserInterface.AddNewBillActivity;
import hoppingvikings.housefinancemobile.UserInterface.Lists.BillList.BillListAdapter;
import hoppingvikings.housefinancemobile.UserInterface.Lists.BillList.BillListObject;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ListItemDivider;
import hoppingvikings.housefinancemobile.WebService.DownloadCallback;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Josh on 24/09/2016.
 */

public class BillsFragment extends Fragment implements DownloadCallback {

    CoordinatorLayout layout;
    Handler _handler;
    RecyclerView rv;
    BillListAdapter adapter;
    ArrayList<BillListObject> cards;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton addItemButton;

    private Runnable contactWebsite = new Runnable() {
        @Override
        public void run() {
            GlobalObjects._service.contactWebsiteBills(getContext(), BillsFragment.this);
        }
    };

    private Runnable updateList = new Runnable() {
        @Override
        public void run() {

            if(!GlobalObjects.downloading) {
                if (GlobalObjects.GetBills() != null) {
                    if(cards != null)
                    {
                        cards.clear();
                        cards.addAll(GlobalObjects.GetBills());
                        adapter.AddAll(cards);
                    }
                    else
                    {
                        cards = new ArrayList<>();
                        cards.addAll(GlobalObjects.GetBills());
                        adapter.AddAll(cards);
                    }
                    if (adapter.getItemCount() != cards.size()) {
                        _handler.post(contactWebsite);
                    } else {

                        swipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    _handler.post(contactWebsite);
                }
            }
            else
            {
                // If we are already trying to talk to the website, wait 3 seconds before trying again
                _handler.postDelayed(this, 3000);
            }
        }
    };

    public BillsFragment()
    {
        // Blank constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        layout = (CoordinatorLayout)view.findViewById(R.id.coordlayout);
        addItemButton = (FloatingActionButton) view.findViewById(R.id.addItem);

        _handler = new Handler();
        rv = (RecyclerView) view.findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);

        if(GlobalObjects.GetBills() != null && GlobalObjects.GetBills().size() != 0)
        {
            cards = new ArrayList<>();
            cards.addAll(GlobalObjects.GetBills());
        }
        else
        {
            cards = new ArrayList<>();
        }

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addBill = new Intent(getContext(), AddNewBillActivity.class);
                startActivityForResult(addBill, 0);
            }
        });

        if(rv != null) {
            adapter = new BillListAdapter(cards, getContext());
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

        //_handler.postDelayed(runnable, 1000);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
            case RESULT_OK:
                _handler.removeCallbacksAndMessages(null);
                swipeRefreshLayout.setRefreshing(true);
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
        Snackbar.make(layout, failReason + ". Retrying...", Snackbar.LENGTH_LONG).show();
        _handler.removeCallbacksAndMessages(null);
        _handler.postDelayed(contactWebsite, 500);
    }
}
