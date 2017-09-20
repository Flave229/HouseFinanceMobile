package hoppingvikings.housefinancemobile.UserInterface.Lists.UserSelectList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.BitmapCache;
import hoppingvikings.housefinancemobile.Person;
import hoppingvikings.housefinancemobile.R;

public class UserSelectAdapter extends RecyclerView.Adapter<UserSelectAdapter.CardViewHolder> {
    private static IUserClickedListener _listener;

    public void setOnUserClickedListener(IUserClickedListener listener)
    {
        _listener = listener;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        TextView userName;
        ImageView userImage;
        CheckBox userSelected;

        public CardViewHolder(View v)
        {
            super(v);
            view = v;
            userName = (TextView) view.findViewById(R.id.userName);
            userImage = (ImageView) view.findViewById(R.id.userImage);
            userSelected = (CheckBox) view.findViewById(R.id.userSelected);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(_listener != null)
                    {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION)
                        {
                            _listener.onUserClicked(v, pos);
                        }
                    }
                }
            });
        }
    }

    ArrayList<Person> _users = new ArrayList<>();
    Context _context;
    BitmapCache imgCache;

    public UserSelectAdapter(ArrayList<Person> users, Context context)
    {
        _users.addAll(users);
        _context = context;
        long maxMem = (Runtime.getRuntime().maxMemory() / 1024 / 1024);
        imgCache = new BitmapCache((maxMem / 4L) * 1024L * 1024L, _context);
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_user, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Person user = _users.get(position);
        holder.userName.setText(user.FirstName + " " + user.Surname);
        imgCache.PutBitmap(user.ImageUrl, holder.userImage);

        if(user.selected)
            holder.userSelected.setChecked(true);
        else
            holder.userSelected.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return _users.size();
    }

    public Person GetUser(int pos)
    {
        return _users.get(pos);
    }

    public void AddUsers(ArrayList<Person> users)
    {
        _users.addAll(users);
        notifyItemRangeChanged(0, _users.size());
    }
}