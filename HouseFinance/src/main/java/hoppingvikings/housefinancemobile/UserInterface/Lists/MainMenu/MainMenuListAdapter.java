package hoppingvikings.housefinancemobile.UserInterface.Lists.MainMenu;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Items.MainMenuItem;

public class MainMenuListAdapter extends RecyclerView.Adapter<MainMenuListAdapter.CardViewHolder> {

    private static MainMenuItemClickedListener _listener;

    public interface MainMenuItemClickedListener
    {
        void onItemClicked(View itemView, int pos);
    }

    public void SetMainMenuItemClickedListener(MainMenuItemClickedListener listener) { _listener = listener; }

    public static class CardViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        LinearLayout itemLayout;
        ImageView itemImage;
        TextView itemText;

        public CardViewHolder(View v)
        {
            super(v);
            view = v;
            itemLayout = v.findViewById(R.id.mainMenuItem);
            itemImage = v.findViewById(R.id.listitem_image);
            itemText = v.findViewById(R.id.listitem_name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(_listener != null)
                    {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION)
                        {
                            _listener.onItemClicked(view, pos);
                        }
                    }
                }
            });
        }
    }

    ArrayList<MainMenuItem> _menuItems = new ArrayList<>();
    Context _context;

    public MainMenuListAdapter(ArrayList<MainMenuItem> items, Context context)
    {
        _menuItems.addAll(items);
        _context = context;
    }

    @Override
    public int getItemCount() {
        return _menuItems.size();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_mainmenu, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.itemImage.setBackgroundResource(_menuItems.get(position).itemImageID);
        holder.itemText.setText(_menuItems.get(position).itemNameString);

        //holder.itemLayout.setBackgroundColor(Color.WHITE);
    }

    public void AddAll(ArrayList<MainMenuItem> newItems)
    {
        if(_menuItems != null)
        {
            int oldsize = _menuItems.size();
            _menuItems.clear();
            _menuItems.addAll(newItems);

            notifyItemRangeRemoved(0, oldsize);
            notifyItemRangeInserted(0, _menuItems.size());
        }
    }

    public MainMenuItem GetItem(int pos)
    {
        return _menuItems.get(pos);
    }
}
