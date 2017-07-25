package hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingCartList;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingList.ShoppingListObject;

/**
 * Created by iView on 25/07/2017.
 */

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.CardViewHolder> {

    public interface DeleteCallback
    {
        void onItemDeleted(int item);
    }

    private static DeleteCallback _deletecallback;

    public static class CardViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        CardView cardView;
        TextView itemName;
        TextView date;
        ImageButton delete;

        public CardViewHolder(View v)
        {
            super(v);
            view = v;
            cardView = (CardView) view.findViewById(R.id.itemCard);
            itemName = (TextView) view.findViewById(R.id.itemName);
            date = (TextView) view.findViewById(R.id.itemDate);
            delete = (ImageButton) view.findViewById(R.id.deleteitem);
        }
    }

    public void SetDeleteCallback(DeleteCallback owner)
    {
        _deletecallback = owner;
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
        holder.date.setText(_items.get(position).date);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _deletecallback.onItemDeleted(holder.getAdapterPosition());
                _items.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), _items.size());
            }
        });
    }

    public void AddAll()
    {

    }
}
