package hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingList;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.DisplayMessageActivity;

/**
 * Created by Josh on 06/11/2016.
 */

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.CardViewHolder> {
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        View view;
        LinearLayout cardView;
        TextView shoppingItemName;
        TextView addedDate;
        ImageView addedBy1;
        ImageView addedFor1;
        ImageView addedFor2;
        ImageView addedFor3;

        public CardViewHolder(View v) {
            super(v);
            view = v;
            cardView = (LinearLayout) v.findViewById(R.id.shoppingItemCard);
            shoppingItemName = (TextView) v.findViewById(R.id.shoppingItemName);
            addedDate = (TextView) v.findViewById(R.id.addedDate);
            addedBy1 = (ImageView) v.findViewById(R.id.addedBy);
            addedFor1 = (ImageView) v.findViewById(R.id.addedFor1);
            addedFor2 = (ImageView) v.findViewById(R.id.addedFor2);
            addedFor3 = (ImageView) v.findViewById(R.id.addedFor3);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.getContext().startActivity(new Intent(view.getContext(), DisplayMessageActivity.class));
                }
            });
        }
    }

    ArrayList<ShoppingListObject> _shoppingItems = new ArrayList<>();

    public ShoppingListAdapter(ArrayList<ShoppingListObject> items) {
        _shoppingItems = items;
    }

    @Override
    public int getItemCount() {
        if (_shoppingItems != null)
            return _shoppingItems.size();
        else
            return 0;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shopping_item_layout, viewGroup, false);
        CardViewHolder cvh = new CardViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(CardViewHolder cvh, int i)
    {
        cvh.shoppingItemName.setText(_shoppingItems.get(i).itemName);
        cvh.addedDate.setText(_shoppingItems.get(i).addedDate);

        if(_shoppingItems.get(i).done)
        {
            cvh.cardView.setBackgroundResource(R.color.bill_paid);
        }
        else
        {
            cvh.cardView.setBackgroundColor(Color.WHITE);
        }

        cvh.addedBy1.setImageResource(_shoppingItems.get(i).addedBy);
        cvh.addedFor1.setImageResource(_shoppingItems.get(i).addedFor1);
        cvh.addedFor2.setImageResource(_shoppingItems.get(i).addedFor2);
        cvh.addedFor3.setImageResource(_shoppingItems.get(i).addedFor3);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView rv)
    {
        super.onAttachedToRecyclerView(rv);
    }

    public void addAll(ArrayList<ShoppingListObject> items)
    {
        if(_shoppingItems != null) {
            _shoppingItems.clear();
            notifyDataSetChanged();
            _shoppingItems.addAll(items);
        }
    }
}
