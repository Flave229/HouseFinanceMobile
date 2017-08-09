package hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingList;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.BitmapCache;
import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingCartList.ShoppingCartAdapter;
import hoppingvikings.housefinancemobile.WebService.DeleteItemCallback;
import hoppingvikings.housefinancemobile.WebService.UploadCallback;

/**
 * Created by Josh on 06/11/2016.
 */

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.CardViewHolder>
                                implements DeleteItemCallback, UploadCallback{
    private static ShoppingItemClickedListener _listener;

    public interface ShoppingItemClickedListener
    {
        void onShoppingItemClick(View itemView, int pos);
    }

    public void setOnShoppingItemClickListener(ShoppingItemClickedListener listener)
    {
        _listener = listener;
    }

    public interface DeleteCallback
    {
        void onItemDeleted();
    }

    private static DeleteCallback _deletecallback;

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        View view;
        LinearLayout cardView;
        TextView shoppingItemName;
        TextView addedDate;
        TextView addedForText;
        TextView infoText;
        ImageView addedBy1;
        ImageView addedFor1;
        ImageView addedFor2;
        ImageView addedFor3;

        LinearLayout buttonsContainer;
        ImageButton completeButton;
        ImageButton editButton;
        ImageButton notifyButton;
        ImageButton deleteButton;

        public CardViewHolder(View v) {
            super(v);
            view = v;
            cardView = (LinearLayout) v.findViewById(R.id.shoppingItemCard);
            shoppingItemName = (TextView) v.findViewById(R.id.shoppingItemName);
            addedDate = (TextView) v.findViewById(R.id.addedDate);
            addedForText = (TextView)v.findViewById(R.id.addedForText);
            infoText = (TextView)v.findViewById(R.id.info_text);
            addedBy1 = (ImageView) v.findViewById(R.id.addedBy);
            addedFor1 = (ImageView) v.findViewById(R.id.addedFor1);
            addedFor2 = (ImageView) v.findViewById(R.id.addedFor2);
            addedFor3 = (ImageView) v.findViewById(R.id.addedFor3);

            buttonsContainer = (LinearLayout) v.findViewById(R.id.buttonsContainer);
            completeButton = (ImageButton) v.findViewById(R.id.shopping_complete);
            editButton = (ImageButton) v.findViewById(R.id.shopping_edit);
            notifyButton = (ImageButton) v.findViewById(R.id.shopping_notify);
            deleteButton = (ImageButton) v.findViewById(R.id.shopping_delete);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(_listener != null)
                    {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION)
                        {
                            _listener.onShoppingItemClick(view, pos);
                        }
                    }
                }
            });
        }
    }

    ArrayList<ShoppingListObject> _shoppingItems = new ArrayList<>();
    private Context _context;
    BitmapCache imgCache;
    private int selected;
    private boolean completeAlreadyPressed = false;

    public void SetDeleteCallback(DeleteCallback owner)
    {
        _deletecallback = owner;
    }

    public ShoppingListAdapter(ArrayList<ShoppingListObject> items, Context context) {
        _shoppingItems.addAll(items);
        _context = context;
        long maxMem = (Runtime.getRuntime().maxMemory() / 1024 / 1024);
        imgCache = new BitmapCache((maxMem / 4L) * 1024L * 1024L, _context);
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_shopping, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder cvh, final int i)
    {
        cvh.shoppingItemName.setText(_shoppingItems.get(cvh.getAdapterPosition()).itemName);
        cvh.addedDate.setText(_shoppingItems.get(cvh.getAdapterPosition()).addedDate);

        if(_shoppingItems.get(cvh.getAdapterPosition()).done)
        {
            cvh.cardView.setBackgroundResource(R.color.bill_paid);
        }
        else
        {
            cvh.cardView.setBackgroundColor(Color.WHITE);
        }

        try {
            imgCache.PutBitmap(_shoppingItems.get(cvh.getAdapterPosition()).addedBy, _shoppingItems.get(cvh.getAdapterPosition()).addedBy, cvh.addedBy1);

            if(_shoppingItems.get(cvh.getAdapterPosition()).itemExpanded)
            {
                cvh.editButton.setVisibility(View.GONE);
                cvh.buttonsContainer.setVisibility(View.VISIBLE);
                cvh.addedForText.setVisibility(View.VISIBLE);
                cvh.infoText.setVisibility(View.INVISIBLE);
                cvh.addedFor1.setVisibility(View.VISIBLE);

                imgCache.PutBitmap(_shoppingItems.get(cvh.getAdapterPosition()).addedForImage1, _shoppingItems.get(cvh.getAdapterPosition()).addedForImage1, cvh.addedFor1);

                if (_shoppingItems.get(cvh.getAdapterPosition()).addedForImage2.isEmpty()) {
                    cvh.addedFor2.setVisibility(View.GONE);
                } else
                {
                    cvh.addedFor2.setVisibility(View.VISIBLE);
                    imgCache.PutBitmap(_shoppingItems.get(cvh.getAdapterPosition()).addedForImage2, _shoppingItems.get(cvh.getAdapterPosition()).addedForImage2, cvh.addedFor2);
                }

                if (_shoppingItems.get(cvh.getAdapterPosition()).addedForImage3.isEmpty()) {
                    cvh.addedFor3.setVisibility(View.GONE);
                } else
                {
                    cvh.addedFor3.setVisibility(View.VISIBLE);
                    imgCache.PutBitmap(_shoppingItems.get(cvh.getAdapterPosition()).addedForImage3, _shoppingItems.get(cvh.getAdapterPosition()).addedForImage3, cvh.addedFor3);
                }

                /*cvh.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });*/

                cvh.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            JSONObject itemjson = new JSONObject();
                            itemjson.put("ShoppingItemId", _shoppingItems.get(cvh.getAdapterPosition()).ID);
                            GlobalObjects.webHandler.DeleteItem(_context, ShoppingListAdapter.this, itemjson, GlobalObjects.ITEM_TYPE_SHOPPING);
                        } catch (Exception e)
                        {
                            Toast.makeText(_context, "Failed to delete item", Toast.LENGTH_SHORT).show();
                        }

                        _shoppingItems.remove(cvh.getAdapterPosition());
                        notifyItemRemoved(cvh.getAdapterPosition());
                        notifyItemRangeChanged(cvh.getAdapterPosition(), _shoppingItems.size());
                    }
                });


                cvh.completeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!completeAlreadyPressed)
                        {
                            completeAlreadyPressed = true;
                            selected = cvh.getAdapterPosition();
                            JSONObject editeditem = new JSONObject();
                            try {
                                editeditem.put("id", _shoppingItems.get(cvh.getAdapterPosition()).ID);
                                editeditem.put("purchased", !_shoppingItems.get(cvh.getAdapterPosition()).done);
                                GlobalObjects.webHandler.EditShoppingItem(_context, editeditem, ShoppingListAdapter.this);
                            } catch (Exception e)
                            {
                                OnFailedUpload("");
                            }
                        }
                    }
                });

                if(_shoppingItems.get(cvh.getAdapterPosition()).done) {
                    cvh.notifyButton.setVisibility(View.INVISIBLE);
                    cvh.completeButton.setImageResource(R.drawable.ic_undo_black_24dp);
                    //cvh.completeButton.setVisibility(View.GONE);
                }
                else {
                    cvh.notifyButton.setVisibility(View.VISIBLE);
                    cvh.completeButton.setVisibility(View.VISIBLE);
                    cvh.completeButton.setImageResource(R.drawable.ic_done_black_24dp);

                    cvh.notifyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GlobalObjects.ShowNotif(_context, "Don't forget to buy " + _shoppingItems.get(cvh.getAdapterPosition()).itemName + "!", "Reminder", cvh.getAdapterPosition());
                        }
                    });
                }
            }
            else
            {
                cvh.buttonsContainer.setVisibility(View.GONE);
                cvh.addedForText.setVisibility(View.GONE);
                cvh.infoText.setVisibility(View.VISIBLE);
                cvh.addedFor1.setVisibility(View.GONE);
                cvh.addedFor2.setVisibility(View.GONE);
                cvh.addedFor3.setVisibility(View.GONE);
            }

        } catch (Exception e)
        {
            Log.v("Error: ", e.getMessage());
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView rv)
    {
        super.onAttachedToRecyclerView(rv);
    }

    public void addAll(ArrayList<ShoppingListObject> items)
    {
        if(_shoppingItems != null) {
            int oldsize = _shoppingItems.size();
            _shoppingItems.clear();
            _shoppingItems.addAll(items);

            notifyItemRangeRemoved(0, oldsize);
            notifyItemRangeInserted(0, _shoppingItems.size());
        }
    }

    public ShoppingListObject GetItem(int pos)
    {
        return _shoppingItems.get(pos);
    }

    @Override
    public void OnSuccessfulUpload() {
        completeAlreadyPressed = false;
        _shoppingItems.get(selected).done = !_shoppingItems.get(selected).done;
        notifyItemChanged(selected);
    }

    @Override
    public void OnFailedUpload(String failReason) {
        completeAlreadyPressed = false;
        Toast.makeText(_context, "Failed to update shopping item.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnSuccessfulDelete() {
        Toast.makeText(_context, "Item deleted", Toast.LENGTH_SHORT).show();
        _deletecallback.onItemDeleted();
    }

    @Override
    public void OnFailedDelete(String err) {
        Toast.makeText(_context, "Failed to delete", Toast.LENGTH_SHORT).show();
        _deletecallback.onItemDeleted();
    }
}
