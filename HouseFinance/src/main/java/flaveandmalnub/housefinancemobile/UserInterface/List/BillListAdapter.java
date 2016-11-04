package flaveandmalnub.housefinancemobile.UserInterface.List;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import flaveandmalnub.housefinancemobile.R;
import flaveandmalnub.housefinancemobile.UserInterface.DisplayMessageActivity;


/**
 * Created by Josh on 17/09/2016.
 */
public class BillListAdapter extends RecyclerView.Adapter<BillListAdapter.CardViewHolder> {
    public static class CardViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        CardView cardObject;
        TextView cardName;
        TextView cardDate;
        TextView cardAmount;
        ImageView cardImage;
        ImageView cardImage2;
        ImageView cardImage3;

        public CardViewHolder(View v)
        {
            super(v);
            view = v;
            cardObject = (CardView) v.findViewById(R.id.general_card);
            cardName = (TextView)v.findViewById(R.id.item_name);
            cardDate = (TextView)v.findViewById(R.id.item_date);
            cardAmount = (TextView)v.findViewById(R.id.item_amount);
            cardImage = (ImageView)v.findViewById(R.id.card_image);
            cardImage2 = (ImageView)v.findViewById(R.id.card_image_2);
            cardImage3 = (ImageView)v.findViewById(R.id.card_image_3);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.getContext().startActivity(new Intent(view.getContext(), DisplayMessageActivity.class));
                }
            });
        }
    }

    ArrayList<BillListObject> _cards = new ArrayList<>();

    public BillListAdapter(ArrayList<BillListObject> cards){
        _cards = cards;
    }

    @Override
    public int getItemCount()
    {
        if(_cards != null)
            return _cards.size();
        else
            return 0;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.general_list_item, viewGroup, false);
        CardViewHolder cvh = new CardViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(CardViewHolder cvh, int i)
    {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        cvh.cardName.setText(_cards.get(i).cardName);

        if(_cards.get(i).paid)
        {
            cvh.cardObject.setCardBackgroundColor(Color.GREEN);
            cvh.cardAmount.setText("PAID");
            cvh.cardDate.setText(_cards.get(i).cardDesc);
        }
        else if(_cards.get(i).overdue)
        {
            cvh.cardObject.setCardBackgroundColor(Color.parseColor("#E77471"));
            cvh.cardDate.setText(_cards.get(i).cardDesc + " OVERDUE");
            cvh.cardAmount.setText("£" + _cards.get(i).cardAmount);
        }
        else
        {
            cvh.cardDate.setText(_cards.get(i).cardDesc);
            cvh.cardAmount.setText("£" + _cards.get(i).cardAmount);
            cvh.cardObject.setCardBackgroundColor(Color.WHITE);
        }

        cvh.cardImage.setImageResource(_cards.get(i).cardImage);
        cvh.cardImage2.setImageResource(_cards.get(i).cardImage);
        cvh.cardImage3.setImageResource(_cards.get(i).cardImage);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView rv)
    {
        super.onAttachedToRecyclerView(rv);
    }

    public void clear()
    {
        if(_cards != null) {
            _cards.clear();
            notifyDataSetChanged();
        }
    }

    public void addAll(ArrayList<BillListObject> bills)
    {
        if(_cards != null) {
            _cards.addAll(bills);
        }
    }
}
