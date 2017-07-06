package hoppingvikings.housefinancemobile.UserInterface.Lists.BillList;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.BitmapCache;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.DisplayMessageActivity;


/**
 * Created by Josh on 17/09/2016.
 */
public class BillListAdapter extends RecyclerView.Adapter<BillListAdapter.CardViewHolder> {

    private static BillClickedListener _listener;

    public interface BillClickedListener
    {
        void onBillClick(View itemView, int pos);
    }

    public void setOnBillClickListener(BillClickedListener listener)
    {
        _listener = listener;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        LinearLayout cardObject;
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
            cardObject = (LinearLayout) v.findViewById(R.id.general_card);
            cardName = (TextView)v.findViewById(R.id.item_name);
            cardDate = (TextView)v.findViewById(R.id.item_date);
            cardAmount = (TextView)v.findViewById(R.id.item_amount);
            cardImage = (ImageView)v.findViewById(R.id.card_image);
            cardImage2 = (ImageView)v.findViewById(R.id.card_image_2);
            cardImage3 = (ImageView)v.findViewById(R.id.card_image_3);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(_listener != null)
                    {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION)
                        {
                            _listener.onBillClick(view, pos);
                        }
                    }
                }
            });
        }
    }

    ArrayList<BillListObject> _cards = new ArrayList<>();
    Context _context;
    BitmapCache imgCache;

    public BillListAdapter(ArrayList<BillListObject> cards, Context context){
        _cards.addAll(cards);
        _context = context;
        long maxMem = (Runtime.getRuntime().maxMemory() / 1024 / 1024);
        imgCache = new BitmapCache((maxMem / 4L) * 1024L * 1024L, _context);
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_bill, viewGroup, false);
        CardViewHolder cvh = new CardViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(CardViewHolder cvh, int i)
    {
        cvh.cardName.setText(_cards.get(i).cardName);

        if(_cards.get(i).paid)
        {
            cvh.cardObject.setBackgroundResource(R.color.bill_paid);
            cvh.cardAmount.setText("PAID");
            cvh.cardDate.setText(_cards.get(i).cardDesc);
        }
        else if(_cards.get(i).overdue)
        {
            cvh.cardObject.setBackgroundResource(R.color.bill_overdue);
            cvh.cardDate.setText(_cards.get(i).cardDesc + " OVERDUE");
            cvh.cardAmount.setText("£" + _cards.get(i).cardAmount);
        }
        else
        {
            cvh.cardDate.setText(_cards.get(i).cardDesc);
            cvh.cardAmount.setText("£" + _cards.get(i).cardAmount);
            cvh.cardObject.setBackgroundColor(Color.WHITE);
        }

        try {
            switch (_cards.get(i).people.size())
            {
                case 1:
                    imgCache.PutBitmap(_cards.get(i).people.get(0).URL, _cards.get(i).people.get(0).URL, cvh.cardImage);

                    if(_cards.get(i).people.get(0).Paid)
                    {
                        cvh.cardImage.setAlpha(1.0f);
                    }
                    else
                    {
                        cvh.cardImage.setAlpha(0.5f);
                    }

                    cvh.cardImage2.setVisibility(View.GONE);
                    cvh.cardImage3.setVisibility(View.GONE);
                    break;

                case 2:
                    imgCache.PutBitmap(_cards.get(i).people.get(0).URL, _cards.get(i).people.get(0).URL, cvh.cardImage);
                    imgCache.PutBitmap(_cards.get(i).people.get(1).URL, _cards.get(i).people.get(1).URL, cvh.cardImage2);
                    if(_cards.get(i).people.get(0).Paid)
                    {
                        cvh.cardImage.setAlpha(1.0f);
                    }
                    else
                    {
                        cvh.cardImage.setAlpha(0.5f);
                    }

                    if(_cards.get(i).people.get(1).Paid)
                    {
                        cvh.cardImage2.setAlpha(1.0f);
                    }
                    else
                    {
                        cvh.cardImage2.setAlpha(0.5f);
                    }

                    cvh.cardImage2.setVisibility(View.VISIBLE);
                    cvh.cardImage3.setVisibility(View.GONE);
                    break;

                case 3:
                    imgCache.PutBitmap(_cards.get(i).people.get(0).URL, _cards.get(i).people.get(0).URL, cvh.cardImage);
                    imgCache.PutBitmap(_cards.get(i).people.get(1).URL, _cards.get(i).people.get(1).URL, cvh.cardImage2);
                    imgCache.PutBitmap(_cards.get(i).people.get(2).URL, _cards.get(i).people.get(2).URL, cvh.cardImage3);

                    if(_cards.get(i).people.get(0).Paid)
                    {
                        cvh.cardImage.setAlpha(1.0f);
                    }
                    else
                    {
                        cvh.cardImage.setAlpha(0.5f);
                    }

                    if(_cards.get(i).people.get(1).Paid)
                    {
                        cvh.cardImage2.setAlpha(1.0f);
                    }
                    else
                    {
                        cvh.cardImage2.setAlpha(0.5f);
                    }

                    if(_cards.get(i).people.get(2).Paid)
                    {
                        cvh.cardImage3.setAlpha(1.0f);
                    }
                    else
                    {
                        cvh.cardImage3.setAlpha(0.5f);
                    }

                    cvh.cardImage2.setVisibility(View.VISIBLE);
                    cvh.cardImage3.setVisibility(View.VISIBLE);
                    break;
            }
        } catch (Exception e)
        {
            Log.v("Img Load Error: ", e.getMessage());
        }



        //cvh.cardImage.setImageBitmap(Glide.with(_context).load(_cards.get(i).people.get(0).URL).asBitmap());
        //cvh.cardImage2.setImageResource(_cards.get(i).cardImage);
        //cvh.cardImage3.setImageResource(_cards.get(i).cardImage);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView rv)
    {
        super.onAttachedToRecyclerView(rv);
    }

    public void AddAll(ArrayList<BillListObject> bills)
    {
        if(_cards != null) {
            int oldsize = _cards.size();

            _cards.clear();
            _cards.addAll(bills);
            notifyItemRangeRemoved(0, oldsize);
            notifyItemRangeInserted(0, _cards.size());

        }
    }

    public BillListObject GetItem(int pos)
    {
        return _cards.get(pos);
    }
}
