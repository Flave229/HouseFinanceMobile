package hoppingvikings.housefinancemobile.UserInterface.Lists.TodoList;

import android.content.Context;
import android.media.Image;
import android.support.constraint.solver.ArrayLinkedVariables;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.EmptyStackException;

import hoppingvikings.housefinancemobile.BitmapCache;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Items.TodoListObject;

/**
 * Created by Josh on 02/10/2017.
 */

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.CardViewHolder> {

    private static TodoItemClickedListener _listener;

    public interface TodoItemClickedListener
    {
        void onTodoClicked(View itemView, int pos);
    }

    public void setOnTodoClickedListener(TodoItemClickedListener listener)
    {
        _listener = listener;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        LinearLayout cardObject;
        TextView todoTitle;
        TextView todoDueDate;
        ImageView todoPerson1;
        ImageView todoPerson2;
        ImageView todoPerson3;

        public CardViewHolder(View v)
        {
            super(v);
            view = v;
            cardObject = v.findViewById(R.id.todoItemCard);
            todoTitle = v.findViewById(R.id.todoTitle);
            todoDueDate = v.findViewById(R.id.todoDueDate);
            todoPerson1 = v.findViewById(R.id.todoPerson1);
            todoPerson2 = v.findViewById(R.id.todoPerson2);
            todoPerson3 = v.findViewById(R.id.todoPerson3);

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
    BitmapCache imgCache;

    public TodoListAdapter(ArrayList<TodoListObject> items, Context context)
    {
        _todos.addAll(items);
        _context = context;
        long maxMem = (Runtime.getRuntime().maxMemory() / 1024 / 1024);
        imgCache = new BitmapCache((maxMem / 4L) * 1024L * 1024L, _context);
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_todo, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.todoTitle.setText(_todos.get(position).title);
        holder.todoDueDate.setText(_todos.get(position).dueDate);

        try {
            switch (_todos.get(position).peopleForTask.size())
            {
                case 1:
                    imgCache.PutBitmap(_todos.get(position).peopleForTask.get(0).ImageUrl, holder.todoPerson1);
                    holder.todoPerson2.setVisibility(View.GONE);
                    holder.todoPerson3.setVisibility(View.GONE);
                    break;

                case 2:
                    imgCache.PutBitmap(_todos.get(position).peopleForTask.get(0).ImageUrl, holder.todoPerson1);
                    imgCache.PutBitmap(_todos.get(position).peopleForTask.get(1).ImageUrl, holder.todoPerson2);

                    holder.todoPerson2.setVisibility(View.VISIBLE);
                    holder.todoPerson3.setVisibility(View.GONE);
                    break;

                case 3:
                    imgCache.PutBitmap(_todos.get(position).peopleForTask.get(0).ImageUrl, holder.todoPerson1);
                    imgCache.PutBitmap(_todos.get(position).peopleForTask.get(1).ImageUrl, holder.todoPerson2);
                    imgCache.PutBitmap(_todos.get(position).peopleForTask.get(2).ImageUrl, holder.todoPerson3);

                    holder.todoPerson2.setVisibility(View.VISIBLE);
                    holder.todoPerson3.setVisibility(View.VISIBLE);
                    break;

                default:
                    imgCache.PutBitmap(_todos.get(position).peopleForTask.get(0).ImageUrl, holder.todoPerson1);
                    imgCache.PutBitmap(_todos.get(position).peopleForTask.get(1).ImageUrl, holder.todoPerson2);
                    imgCache.PutBitmap(_todos.get(position).peopleForTask.get(2).ImageUrl, holder.todoPerson3);

                    holder.todoPerson2.setVisibility(View.VISIBLE);
                    holder.todoPerson3.setVisibility(View.VISIBLE);

                    break;
            }
        } catch (Exception e)
        {
            Log.v("Img Load Error: ", e.getMessage());
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
}
