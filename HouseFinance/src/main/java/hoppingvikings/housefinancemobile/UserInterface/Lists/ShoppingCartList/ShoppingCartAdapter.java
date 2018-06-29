package hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingCartList;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.Person;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Items.ShoppingCartItem;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.CardViewHolder> {

    public static class CardViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        TextView itemName;

        public CardViewHolder(View v)
        {
            super(v);
            view = v;
            itemName = (TextView) view.findViewById(R.id.itemName);
        }
    }

    ArrayList<ShoppingCartItem> _items = new ArrayList<>();

    public ShoppingCartAdapter(ArrayList<ShoppingCartItem> items, Context context)
    {
        _items.addAll(items);
    }

    @Override
    public int getItemCount() {
        return _items.size();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_shoppingcartitem, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {
        holder.itemName.setText(_items.get(position).name);
    }

    public ShoppingCartItem GetItem(int position)
    {
        return _items.get(position);
    }

    public void AddItem(ShoppingCartItem item)
    {
        _items.add(0, item);
        notifyItemInserted(0);
    }
}
