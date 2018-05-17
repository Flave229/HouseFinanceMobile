package hoppingvikings.housefinancemobile.UserInterface.Lists.BillList;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import hoppingvikings.housefinancemobile.BitmapCache;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObjectPeople;


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

    public interface ViewAllPeopleClicked
    {
        void onViewAllPressed(ArrayList<BillListObjectPeople> allPeople);
    }

    private static ViewAllPeopleClicked _viewAllCallback;

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
        ImageView viewMore;

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
            viewMore = (ImageView)v.findViewById(R.id.viewAllPeople);
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

    public void SetViewAllCallback(ViewAllPeopleClicked owner)
    {
        _viewAllCallback = owner;
    }

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
    public void onBindViewHolder(final CardViewHolder cvh, int i)
    {
        cvh.cardName.setText(_cards.get(i).name);

        if(_cards.get(i).paid)
        {
            cvh.cardObject.setBackgroundResource(R.color.bill_paid);
            cvh.cardAmount.setText("PAID");
            cvh.cardDate.setText(_cards.get(i).date);

            cvh.cardName.setTextColor(_context.getResources().getColor(R.color.bill_paid_text));
            cvh.cardAmount.setTextColor(_context.getResources().getColor(R.color.bill_paid_text));
            cvh.cardDate.setTextColor(_context.getResources().getColor(R.color.bill_paid_text));
        }
        else if(_cards.get(i).overdue)
        {
            cvh.cardObject.setBackgroundResource(R.color.bill_overdue);
            cvh.cardDate.setText(_cards.get(i).date + " OVERDUE");
            cvh.cardAmount.setText("£" + String.format(Locale.getDefault(), "%.2f", _cards.get(i).remainingAmount));

            cvh.cardName.setTextColor(Color.BLACK);
            cvh.cardAmount.setTextColor(Color.BLACK);
            cvh.cardDate.setTextColor(Color.BLACK);
        }
        else
        {
            cvh.cardDate.setText(_cards.get(i).date);
            cvh.cardAmount.setText("£" + String.format(Locale.getDefault(), "%.2f", _cards.get(i).remainingAmount));
            cvh.cardObject.setBackgroundColor(Color.WHITE);

            cvh.cardName.setTextColor(Color.BLACK);
            cvh.cardAmount.setTextColor(Color.BLACK);
            cvh.cardDate.setTextColor(Color.BLACK);
        }

        try {
            switch (_cards.get(i).people.size())
            {
                case 1:
                    imgCache.PutBitmap(_cards.get(i).people.get(0).URL, cvh.cardImage);

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
                    cvh.viewMore.setVisibility(View.GONE);
                    break;

                case 2:
                    imgCache.PutBitmap(_cards.get(i).people.get(0).URL, cvh.cardImage);
                    imgCache.PutBitmap(_cards.get(i).people.get(1).URL, cvh.cardImage2);
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
                    cvh.viewMore.setVisibility(View.GONE);
                    break;

                case 3:
                    imgCache.PutBitmap(_cards.get(i).people.get(0).URL, cvh.cardImage);
                    imgCache.PutBitmap(_cards.get(i).people.get(1).URL, cvh.cardImage2);
                    imgCache.PutBitmap(_cards.get(i).people.get(2).URL, cvh.cardImage3);

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
                    cvh.viewMore.setVisibility(View.GONE);
                    break;

                default:
                    imgCache.PutBitmap(_cards.get(i).people.get(0).URL, cvh.cardImage);
                    imgCache.PutBitmap(_cards.get(i).people.get(1).URL, cvh.cardImage2);
                    imgCache.PutBitmap(_cards.get(i).people.get(2).URL, cvh.cardImage3);

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

                    cvh.viewMore.setVisibility(View.VISIBLE);
                    cvh.viewMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _viewAllCallback.onViewAllPressed(_cards.get(cvh.getAdapterPosition()).people);
                        }
                    });
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
