package hoppingvikings.housefinancemobile.UserInterface.Fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Lists.BillList.BillListAdapter;
import hoppingvikings.housefinancemobile.UserInterface.Lists.BillList.BillListObject;

/**
 * Created by Josh on 24/09/2016.
 */

public class StatisticsFragment extends Fragment {

    public StatisticsFragment()
    {

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

        ((FloatingActionButton)view.findViewById(R.id.addItem)).hide();
        Snackbar.make(view.findViewById(R.id.coordlayout), "Coming never", Snackbar.LENGTH_INDEFINITE).show();
        return view;
    }
}
