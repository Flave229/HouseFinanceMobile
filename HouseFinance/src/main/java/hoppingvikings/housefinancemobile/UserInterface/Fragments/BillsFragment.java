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

import hoppingvikings.housefinancemobile.Repositories.BillRepository;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Activities.AddNewBillActivity;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObjectPeople;
import hoppingvikings.housefinancemobile.UserInterface.Lists.BillList.BillListAdapter;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ListItemDivider;
import hoppingvikings.housefinancemobile.UserInterface.MainActivity;
import hoppingvikings.housefinancemobile.UserInterface.Activities.ViewBillDetailsActivity;
import hoppingvikings.housefinancemobile.UserInterface.MainMenuActivity;
import hoppingvikings.housefinancemobile.UserInterface.PeoplePopup;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class BillsFragment extends Fragment
        implements CommunicationCallback, BillListAdapter.ViewAllPeopleClicked {

    CoordinatorLayout _layout;
    Handler _handler;
    RecyclerView _recyclerView;
    BillListAdapter _adapter;
    ArrayList<BillListObject> _cards;
    SwipeRefreshLayout _swipeRefreshLayout;
    MainMenuActivity _activity;

    PeoplePopup _peopleListPopup;

    private Runnable contactWebsite = new Runnable() {
        @Override
        public void run() {
            WebHandler.Instance().GetBills(getContext(), BillsFragment.this);
        }
    };

    private Runnable updateList = new Runnable() {
        @Override
        public void run() {
            BillRepository billRepository = BillRepository.Instance();
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
        _activity = ((MainMenuActivity)getActivity());
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        _swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        _layout = (CoordinatorLayout)view.findViewById(R.id.coordlayout);

        _handler = new Handler();
        _recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        _recyclerView.setHasFixedSize(true);

        ArrayList<BillListObject> bills = BillRepository.Instance().Get();
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
    public void OnSuccess(RequestType requestType, Object o)
    {
        _handler.postDelayed(updateList, 1000);
    }

    @Override
    public void OnFail(RequestType requestType, String message)
    {
        _handler.removeCallbacksAndMessages(null);
        Snackbar.make(_activity._layout, message, Snackbar.LENGTH_LONG).show();
        _swipeRefreshLayout.setRefreshing(false);
    }
}
