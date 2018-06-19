package hoppingvikings.housefinancemobile.UserInterface.Lists.TodoList;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.support.constraint.solver.ArrayLinkedVariables;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.BitmapCache;
import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.NotificationType;
import hoppingvikings.housefinancemobile.NotificationWrapper;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Items.TodoListObject;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Josh on 02/10/2017.
 */

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.CardViewHolder>
        implements CommunicationCallback {

    private static TodoItemClickedListener _listener;
    private final NotificationWrapper _notificationWrapper;

    public interface TodoItemClickedListener
    {
        void onTodoClicked(View itemView, int pos);
    }

    public void setOnTodoClickedListener(TodoItemClickedListener listener)
    {
        _listener = listener;
    }

    public interface DeleteCallback
    {
        void onItemDeleted();
    }

    private static DeleteCallback _deletecallback;
    private static EditPressedCallback _editCallback;

    public interface EditPressedCallback
    {
        void onEditPressed(int itemid);
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        LinearLayout cardObject;
        TextView todoTitle;
        TextView todoDueDate;
        TextView todoFor;
        ImageView todoPerson1;
        ImageView todoPerson2;
        ImageView todoPerson3;

        LinearLayout buttonsContainer;
        ImageButton completeButton;
        ImageButton editButton;
        ImageButton notifyButton;
        ImageButton deleteButton;
        TextView infoText;

        public CardViewHolder(View v)
        {
            super(v);
            view = v;
            cardObject = v.findViewById(R.id.todoItemCard);
            todoTitle = v.findViewById(R.id.todoTitle);
            todoDueDate = v.findViewById(R.id.todoDueDate);
            todoFor = v.findViewById(R.id.taskFor);
            todoPerson1 = v.findViewById(R.id.todoPerson1);
            todoPerson2 = v.findViewById(R.id.todoPerson2);
            todoPerson3 = v.findViewById(R.id.todoPerson3);

            buttonsContainer = v.findViewById(R.id.buttonsContainer);
            completeButton =  v.findViewById(R.id.task_complete);
            editButton =  v.findViewById(R.id.task_edit);
            notifyButton = v.findViewById(R.id.task_notify);
            deleteButton = v.findViewById(R.id.task_delete);
            infoText = v.findViewById(R.id.info_text);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(_listener != null)
                    {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION)
                        {
                            _listener.onTodoClicked(view, pos);
                        }
                    }
                }
            });
        }
    }

    ArrayList<TodoListObject> _todos = new ArrayList<>();
    Context _context;
    private int _selected;
    private boolean _completeAlreadyPressed = false;
    BitmapCache imgCache;

    public void SetDeleteCallback(DeleteCallback owner)
    {
        _deletecallback = owner;
    }
    public void SetEditPressedCallback(EditPressedCallback owner)
    {_editCallback = owner;}

    public TodoListAdapter(ArrayList<TodoListObject> items, Context context, NotificationWrapper notificationWrapper)
    {
        _todos.addAll(items);
        _context = context;
        long maxMem = (Runtime.getRuntime().maxMemory() / 1024 / 1024);
        imgCache = new BitmapCache((maxMem / 4L) * 1024L * 1024L, _context);
        _notificationWrapper = notificationWrapper;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_todo, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {
        holder.todoTitle.setText(_todos.get(position).title);

        if(_todos.get(position).completed)
        {
            holder.cardObject.setBackgroundResource(R.color.bill_paid);
            holder.todoDueDate.setText("Task Completed");

            holder.todoDueDate.setTextColor(_context.getResources().getColor(R.color.bill_paid_text));
            holder.todoTitle.setTextColor(_context.getResources().getColor(R.color.bill_paid_text));
            holder.infoText.setTextColor(_context.getResources().getColor(R.color.bill_paid_text));
            holder.todoFor.setTextColor(_context.getResources().getColor(R.color.bill_paid_text));

        }
        else
        {
            holder.cardObject.setBackgroundColor(Color.WHITE);
            holder.todoDueDate.setText(_todos.get(position).dueDate + " (In Progress)");

            holder.todoDueDate.setTextColor(Color.BLACK);
            holder.todoTitle.setTextColor(Color.BLACK);
            holder.infoText.setTextColor(Color.BLACK);
            holder.todoFor.setTextColor(Color.BLACK);
        }

        try {

            imgCache.PutBitmap(_todos.get(position).peopleForTask.get(0).ImageUrl, holder.todoPerson1);
            if(_todos.get(holder.getAdapterPosition()).peopleForTask.size() < 2) {
                holder.todoPerson2.setVisibility(View.INVISIBLE);
            }
            else {
                holder.todoPerson2.setVisibility(View.VISIBLE);
                imgCache.PutBitmap(_todos.get(position).peopleForTask.get(1).ImageUrl, holder.todoPerson2);
            }

            if(_todos.get(holder.getAdapterPosition()).peopleForTask.size() < 3) {
                holder.todoPerson3.setVisibility(View.INVISIBLE);
            }
            else {
                holder.todoPerson3.setVisibility(View.VISIBLE);
                imgCache.PutBitmap(_todos.get(position).peopleForTask.get(2).ImageUrl, holder.todoPerson3);
            }
        } catch (Exception e)
        {
            Log.v("Img Load Error: ", e.getMessage());
        }

        if(_todos.get(holder.getAdapterPosition()).ItemExpanded)
        {
            holder.buttonsContainer.setVisibility(View.VISIBLE);
            holder.infoText.setVisibility(View.INVISIBLE);

            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _editCallback.onEditPressed(_todos.get(holder.getAdapterPosition()).id);
                }
            });

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject taskJson = new JSONObject();
                        taskJson.put("Id", _todos.get(holder.getAdapterPosition()).id);
                        WebHandler.Instance().DeleteItem(_context, TodoListAdapter.this, taskJson, ItemType.TODO);
                    } catch (Exception e)
                    {
                        Toast.makeText(_context, "Failed to delete task", Toast.LENGTH_SHORT).show();
                    }
                    NotificationManager man = (NotificationManager) _context.getSystemService(NOTIFICATION_SERVICE);
                    man.cancel(_todos.get(holder.getAdapterPosition()).id);
                    _todos.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), _todos.size());
                }
            });

            holder.completeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!_completeAlreadyPressed)
                    {
                        _completeAlreadyPressed = true;
                        _selected = holder.getAdapterPosition();
                        JSONObject editedTask = new JSONObject();
                        try {
                            editedTask.put("Id", _todos.get(holder.getAdapterPosition()).id);
                            editedTask.put("Complete", !_todos.get(holder.getAdapterPosition()).completed);
                            WebHandler.Instance().EditItem(_context, editedTask, TodoListAdapter.this, ItemType.TODO);
                            NotificationManager man = (NotificationManager) _context.getSystemService(NOTIFICATION_SERVICE);
                            man.cancel(_todos.get(holder.getAdapterPosition()).id);
                        } catch (Exception e)
                        {
                            OnFail(RequestType.PATCH, "");
                        }
                    }
                }
            });

            if(_todos.get(holder.getAdapterPosition()).completed) {
                holder.notifyButton.setVisibility(View.INVISIBLE);
                holder.completeButton.setImageResource(R.drawable.ic_undo_black_24dp);
                holder.editButton.setVisibility(View.GONE);
            }
            else {
                holder.notifyButton.setVisibility(View.VISIBLE);
                holder.completeButton.setVisibility(View.VISIBLE);
                holder.completeButton.setImageResource(R.drawable.ic_done_black_24dp);
                holder.editButton.setVisibility(View.VISIBLE);
                holder.notifyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _notificationWrapper.ShowNotification(NotificationType.TODO,_todos.get(holder.getAdapterPosition()).title, "Reminder", _todos.get(holder.getAdapterPosition()).id);
                    }
                });
            }
        }
        else
        {
            holder.buttonsContainer.setVisibility(View.GONE);
            holder.infoText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return _todos.size();
    }

    public void AddItems(ArrayList<TodoListObject> newItems)
    {
        int oldSize = _todos.size();

        _todos.clear();
        _todos.addAll(newItems);
        notifyItemRangeRemoved(0, oldSize);
        notifyItemRangeInserted(0, _todos.size());
    }

    public TodoListObject GetItem(int pos)
    {
        return _todos.get(pos);
    }

    @Override
    public void OnSuccess(RequestType requestType, Object s)
    {
        switch (requestType)
        {
            case PATCH:
                _completeAlreadyPressed = false;
                _todos.get(_selected).completed = !_todos.get(_selected).completed;
                notifyItemChanged(_selected);
                break;
            case DELETE:
                Toast.makeText(_context, "Item deleted", Toast.LENGTH_SHORT).show();
                _deletecallback.onItemDeleted();
                break;
        }
    }

    @Override
    public void OnFail(RequestType requestType, String message)
    {
        switch (requestType)
        {
            case PATCH:
                _completeAlreadyPressed = false;
                Toast.makeText(_context, "Failed to update shopping item.", Toast.LENGTH_SHORT).show();
                break;
            case DELETE:
                Toast.makeText(_context, "Failed to delete", Toast.LENGTH_SHORT).show();
                _deletecallback.onItemDeleted();
                break;
        }
    }
}
