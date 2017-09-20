package hoppingvikings.housefinancemobile.UserInterface.Lists;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.BitmapCache;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObjectPeople;

/**
 * Created by iView on 25/08/2017.
 */

public class AllPeopleAdapter extends RecyclerView.Adapter<AllPeopleAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        ImageView _userImage;

        public ViewHolder(View v)
        {
            super(v);
            view = v;
            _userImage = (ImageView) v.findViewById(R.id.userImage);
        }
    }

    ArrayList<BillListObjectPeople> _users = new ArrayList<>();
    private Context _context;
    BitmapCache imgCache;

    public AllPeopleAdapter(ArrayList<BillListObjectPeople> users, Context context)
    {
        _users.addAll(users);
        _context = context;

        long maxmem = (Runtime.getRuntime().maxMemory() / 1024 / 1024);
        imgCache = new BitmapCache((maxmem / 4L) * 1024L * 1024L, _context);
    }

    @Override
    public int getItemCount() {
        return _users.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_userimg, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        imgCache.PutBitmap(_users.get(holder.getAdapterPosition()).URL, holder._userImage);
    }

    public void AddUsersAndRefresh(ArrayList<BillListObjectPeople> users)
    {
        int oldsize = _users.size();
        if(oldsize > 0)
        {
            _users.clear();
            _users.addAll(users);

            notifyItemRangeRemoved(0, oldsize);
            notifyItemRangeInserted(0, _users.size());
        }
        else
        {
            _users.addAll(users);

            notifyItemRangeInserted(0, _users.size());
        }
    }
}
