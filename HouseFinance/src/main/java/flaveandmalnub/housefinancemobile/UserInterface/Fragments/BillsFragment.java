package flaveandmalnub.housefinancemobile.UserInterface.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import flaveandmalnub.housefinancemobile.GlobalObjects;
import flaveandmalnub.housefinancemobile.R;
import flaveandmalnub.housefinancemobile.UserInterface.List.BillListAdapter;
import flaveandmalnub.housefinancemobile.UserInterface.List.BillListObject;

/**
 * Created by Josh on 24/09/2016.
 */

public class BillsFragment extends Fragment {

    Handler _handler;
    RecyclerView rv;
    BillListAdapter adapter;
    ArrayList<BillListObject> cards;
    SwipeRefreshLayout swipeRefreshLayout;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            GlobalObjects._service.contactWebsite();

            cards = GlobalObjects.GetBills();

            if (GlobalObjects.GetBills() != null) {
                if (adapter.getItemCount() != GlobalObjects.GetBills().size()) {
                    adapter.addAll(GlobalObjects.GetBills());
                    adapter.notifyItemRangeInserted(0, GlobalObjects.GetBills().size());
                }
            }
            _handler.post(updateList);
        }
    };

    private Runnable updateList = new Runnable() {
        @Override
        public void run() {

            if (GlobalObjects.GetBills() != null) {

                cards = GlobalObjects.GetBills();
                if (adapter.getItemCount() != GlobalObjects.GetBills().size()) {
                    if (adapter.getItemCount() != GlobalObjects.GetBills().size()) {
                        adapter.addAll(cards);
                        adapter.notifyItemRangeInserted(0, GlobalObjects.GetBills().size());
                    }
                    _handler.postDelayed(updateList, 100);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            else
            {
                _handler.postDelayed(updateList, 100);
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

        _handler = new Handler();
        rv = (RecyclerView) view.findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        cards = new ArrayList<>();

        if(rv != null) {
            adapter = new BillListAdapter(cards);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _handler.post(runnable);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //_handler.postDelayed(runnable, 1000);
        return view;
    }
}
