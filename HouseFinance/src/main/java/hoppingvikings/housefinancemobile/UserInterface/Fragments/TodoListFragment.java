package hoppingvikings.housefinancemobile.UserInterface.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.UserInterface.Items.TodoListObject;
import hoppingvikings.housefinancemobile.UserInterface.Lists.TodoList.TodoListAdapter;
import hoppingvikings.housefinancemobile.UserInterface.MainActivity;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

/**
 * Created by Josh on 02/10/2017.
 */

public class TodoListFragment extends Fragment
        implements CommunicationCallback{

    CoordinatorLayout _layout;
    Handler _handler;
    RecyclerView _recyclerView;
    TodoListAdapter _adapter;
    ArrayList<TodoListObject> _items;
    SwipeRefreshLayout _swipeRefreshLayout;
    MainActivity _activity;

    private Runnable contactWebsite = new Runnable() {
        @Override
        public void run() {
            // todo create and call the api endpoint for todo items
        }
    };

    private Runnable updateList = new Runnable() {
        @Override
        public void run() {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void OnSuccess(RequestType requestType, Object o) {

    }

    @Override
    public void OnFail(RequestType requestType, String message) {

    }
}
