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
import flaveandmalnub.housefinancemobile.UserInterface.Lists.BillList.BillListAdapter;
import flaveandmalnub.housefinancemobile.UserInterface.Lists.BillList.BillListObject;
import flaveandmalnub.housefinancemobile.UserInterface.Lists.ListItemDivider;

/**
 * Created by Josh on 24/09/2016.
 */

public class BillsFragment extends Fragment {

    Handler _handler;
    RecyclerView rv;
    BillListAdapter adapter;
    ArrayList<BillListObject> cards;
    SwipeRefreshLayout swipeRefreshLayout;

    private Runnable contactWebsite = new Runnable() {
        @Override
        public void run() {

            GlobalObjects._service.contactWebsiteBills();
            // After calling the website, allow 3 seconds before we update the list. Can be reduced if needed
            _handler.postDelayed(Populate, 1000);
        }
    };

    private Runnable Populate = new Runnable() {
        @Override
        public void run() {
            adapter.addAll(cards);
            adapter.notifyItemRangeInserted(0, cards.size());

            _handler.postDelayed(updateList, 500);
        }
    };

    private Runnable updateList = new Runnable() {
        @Override
        public void run() {

            if(!GlobalObjects.downloading) {
                if (GlobalObjects.GetBills() != null) {
                    cards = GlobalObjects.GetBills();
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

        _handler = new Handler();
        rv = (RecyclerView) view.findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);

        if(GlobalObjects.GetBills() != null && GlobalObjects.GetBills().size() != 0)
        {
            cards = GlobalObjects.GetBills();
        }
        else
        {
            cards = new ArrayList<>();
        }


        if(rv != null) {
            adapter = new BillListAdapter(cards);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(getActivity()));
            rv.addItemDecoration(new ListItemDivider(getContext()));
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _handler.post(updateList);
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
