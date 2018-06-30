package hoppingvikings.housefinancemobile.UserInterface.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.HouseFinanceClass;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.Services.SaltVault.ToDo.ToDoEndpoint;
import hoppingvikings.housefinancemobile.Services.SaltVault.ToDo.TodoRepository;
import hoppingvikings.housefinancemobile.UserInterface.Activities.EditTodoItemActivity;
import hoppingvikings.housefinancemobile.UserInterface.Items.TodoListObject;
import hoppingvikings.housefinancemobile.UserInterface.Lists.TodoList.TodoListAdapter;
import hoppingvikings.housefinancemobile.UserInterface.Activities.MainMenuActivity;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Josh on 02/10/2017.
 */

public class TodoListFragment extends Fragment
        implements CommunicationCallback, TodoListAdapter.DeleteCallback, TodoListAdapter.EditPressedCallback
{
    private ToDoEndpoint _toDoEndpoint;

    CoordinatorLayout _layout;
    Handler _handler;
    RecyclerView _recyclerView;
    TodoListAdapter _adapter;
    ArrayList<TodoListObject> _items;
    SwipeRefreshLayout _swipeRefreshLayout;
    MainMenuActivity _activity;

    private Runnable contactWebsite = new Runnable()
    {
        @Override
        public void run()
        {
            _toDoEndpoint.Get(getContext(), TodoListFragment.this);
        }
    };

    private Runnable updateList = new Runnable()
    {
        @Override
        public void run()
        {
            TodoRepository todoRepository = TodoRepository.Instance();
            if(todoRepository.Get() != null)
            {
                if(_items != null)
                {
                    _items.clear();
                    _items.addAll(todoRepository.Get());
                    _adapter.AddItems(_items);
                }
                else
                {
                    _items = new ArrayList<>();
                    _items.addAll(todoRepository.Get());
                    _adapter.AddItems(_items);
                }

                if(_adapter.getItemCount() != _items.size())
                {
                    _handler.post(contactWebsite);
                }
                else
                {
                    _swipeRefreshLayout.setRefreshing(false);
                }
            }
            else
            {
                _handler.post(contactWebsite);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        _toDoEndpoint = HouseFinanceClass.GetToDoComponent().GetToDoEndpoint();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState)
    {
        _activity = (MainMenuActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        _swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        _layout = (CoordinatorLayout) view.findViewById(R.id.coordlayout);

        _handler = new Handler();
        _recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        _recyclerView.setHasFixedSize(false);

        ArrayList<TodoListObject> todos = TodoRepository.Instance().Get();
        _items = new ArrayList<>();
        if(todos != null && todos.size() != 0)
        {
            _items.addAll(todos);
        }

        if(_recyclerView != null)
        {
            _adapter = new TodoListAdapter(getContext(), null, _toDoEndpoint, _items);
            _recyclerView.setAdapter(_adapter);
            _recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        _adapter.setOnTodoClickedListener(new TodoListAdapter.TodoItemClickedListener() {
            @Override
            public void onTodoClicked(View itemView, int pos) {
                TodoListObject task = _adapter.GetItem(pos);

                if(task.ItemExpanded)
                    task.ItemExpanded = false;
                else
                    task.ItemExpanded = true;

                _adapter.notifyItemChanged(pos);
            }
        });

        _adapter.SetDeleteCallback(this);
        _adapter.SetEditPressedCallback(this);

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

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
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
    public void onItemDeleted()
    {
        _swipeRefreshLayout.setRefreshing(true);
        _handler.postDelayed(contactWebsite, 200);
    }

    @Override
    public void onEditPressed(int itemid)
    {
        Intent edititem = new Intent(getContext(), EditTodoItemActivity.class);
        edititem.putExtra("id", itemid);
        startActivityForResult(edititem, 0);
    }

    @Override
    public void OnSuccess(RequestType requestType, Object o)
    {
        _handler.postDelayed(updateList, 100);
    }

    @Override
    public void OnFail(RequestType requestType, String message)
    {
        _handler.removeCallbacksAndMessages(null);
        //Snackbar.make(_activity._layout, message, Snackbar.LENGTH_LONG).show();
        _swipeRefreshLayout.setRefreshing(false);
    }
}
