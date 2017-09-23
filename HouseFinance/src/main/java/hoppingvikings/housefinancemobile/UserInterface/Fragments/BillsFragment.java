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

import hoppingvikings.housefinancemobile.MemoryRepositories.BillMemoryRepository;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Activities.AddNewBillActivity;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObjectPeople;
import hoppingvikings.housefinancemobile.UserInterface.Lists.BillList.BillListAdapter;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ListItemDivider;
import hoppingvikings.housefinancemobile.UserInterface.MainActivity;
import hoppingvikings.housefinancemobile.UserInterface.Activities.ViewBillDetailsActivity;
import hoppingvikings.housefinancemobile.UserInterface.PeoplePopup;
import hoppingvikings.housefinancemobile.WebService.DownloadCallback;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Josh on 24/09/2016.
 */

public class BillsFragment extends Fragment
        implements DownloadCallback, BillListAdapter.ViewAllPeopleClicked {

    CoordinatorLayout _layout;
    Handler _handler;
    RecyclerView _recyclerView;
    BillListAdapter _adapter;
    ArrayList<BillListObject> _cards;
    SwipeRefreshLayout _swipeRefreshLayout;
    FloatingActionButton _addItemButton;
    MainActivity _activity;

    PeoplePopup _peopleListPopup;

    private Runnable contactWebsite = new Runnable() {
        @Override
        public void run() {
            WebHandler.Instance().contactWebsiteBills(getContext(), BillsFragment.this);
        }
    };

    private Runnable updateList = new Runnable() {
        @Override
        public void run() {
            if(!WebHandler.Instance().IsDownloading()) {
                BillMemoryRepository billRepository = BillMemoryRepository.Instance();
                if (billRepository.Get() != null) {
                    if(_cards != null) {
                        _cards.clear();
                        _cards.addAll(billRepository.Get());
                        _adapter.AddAll(_cards);
                    }
                    else {
                        _cards = new ArrayList<>();
                        _cards.addAll(billRepository.Get());
                        _adapter.AddAll(_cards);
                    }

                    if (_adapter.getItemCount() != _cards.size()) {
                        _handler.post(contactWebsite);
                    }
                    else {
                        _swipeRefreshLayout.setRefreshing(false);

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

    @Override
    public void onViewAllPressed(ArrayList<BillListObjectPeople> allPeople) {
        _peopleListPopup.Show(allPeople);
    }

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
        _activity = ((MainActivity)getActivity());
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        _swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        _layout = (CoordinatorLayout)view.findViewById(R.id.coordlayout);
        _addItemButton = (FloatingActionButton) view.findViewById(R.id.addItem);

        _handler = new Handler();
        _recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        _recyclerView.setHasFixedSize(true);

        ArrayList<BillListObject> bills = BillMemoryRepository.Instance().Get();
        _cards = new ArrayList<>();
        if(bills != null && bills.size() != 0)
        {
            _cards.addAll(bills);
        }

        _activity.addBillFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addBill = new Intent(getContext(), AddNewBillActivity.class);
                startActivityForResult(addBill, 0);
            }
        });

        if(_recyclerView != null) {
            _adapter = new BillListAdapter(_cards, getContext());
            _recyclerView.setAdapter(_adapter);
            _recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            _recyclerView.addItemDecoration(new ListItemDivider(getContext()));
        }

        _adapter.setOnBillClickListener(new BillListAdapter.BillClickedListener() {
            @Override
            public void onBillClick(View itemView, int pos) {
                BillListObject bill = _adapter.GetItem(pos);

                Intent viewBillDetails = new Intent(getContext(), ViewBillDetailsActivity.class);
                viewBillDetails.putExtra("bill_id", bill.id);
                startActivityForResult(viewBillDetails, 0);
            }
        });

        _adapter.SetViewAllCallback(this);

        _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _handler.post(contactWebsite);
            }
        });

        _swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        _swipeRefreshLayout.setRefreshing(true);
        _handler.postDelayed(contactWebsite, 200);

        _peopleListPopup = new PeoplePopup(getContext(), (ViewGroup) view);

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
                _swipeRefreshLayout.setRefreshing(true);
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
        Snackbar.make(_activity._layout, failReason, Snackbar.LENGTH_LONG).show();
        _swipeRefreshLayout.setRefreshing(false);
    }
}
