package hoppingvikings.housefinancemobile.UserInterface;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillObjectDetailedPayments;
import hoppingvikings.housefinancemobile.WebService.DeleteItemCallback;
import hoppingvikings.housefinancemobile.WebService.UploadCallback;

/**
 * Created by iView on 15/08/2017.
 */

public class PaymentsListAdapter extends RecyclerView.Adapter<PaymentsListAdapter.CardViewHolder>
        implements DeleteItemCallback, UploadCallback {

    public interface DeleteCallback
    {
        void onItemDeleted();
    }

    private static PaymentsListAdapter.DeleteCallback _deletecallback;
    private static PaymentsListAdapter.EditPressedCallback _editCallback;

    public interface EditPressedCallback
    {
        void onEditPressed(BillObjectDetailedPayments item);
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        TextView paymentName;
        TextView paymentDate;
        TextView paymentAmount;
        ImageButton editPayment;
        ImageButton deletePayment;

        public CardViewHolder(View v)
        {
            super(v);
            view = v;
            paymentName = (TextView) view.findViewById(R.id.paymentName);
            paymentAmount = (TextView) view.findViewById(R.id.paymentAmount);
            paymentDate = (TextView) view.findViewById(R.id.paymentDate);
            editPayment = (ImageButton) view.findViewById(R.id.editPayment);
            deletePayment = (ImageButton) view.findViewById(R.id.deletePayment);
        }
    }

    ArrayList<BillObjectDetailedPayments> _payments = new ArrayList<>();
    Context _context;

    public void SetDeleteCallback(DeleteCallback owner)
    {
        _deletecallback = owner;
    }
    public void SetEditPressedCallback(EditPressedCallback owner)
    {_editCallback = owner;}

    public PaymentsListAdapter(ArrayList<BillObjectDetailedPayments> payments, Context context)
    {
        _payments.addAll(payments);
        _context = context;
    }

    @Override
    public int getItemCount() {
        return _payments.size();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_billpayment, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        final BillObjectDetailedPayments item = _payments.get(position);
        holder.paymentName.setText(item.personName);
        holder.paymentDate.setText("Date: " + item.Date);
        holder.paymentAmount.setText("Paid: £" + String.format(Locale.getDefault(), "%.2f", item.AmountPaid));
        holder.editPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editCallback.onEditPressed(item);
            }
        });
        holder.deletePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject paymentToDelete = new JSONObject();
                    paymentToDelete.put("BillId", item.BillID);
                    paymentToDelete.put("PaymentId", item.PaymentID);
                    GlobalObjects.webHandler.DeleteItem(_context, PaymentsListAdapter.this, paymentToDelete, GlobalObjects.ITEM_TYPE_BILLPAYMENT);
                } catch (Exception e)
                {

                }
            }
        });
    }


    @Override
    public void OnSuccessfulUpload() {

    }

    @Override
    public void OnFailedUpload(String failReason) {

    }

    @Override
    public void OnSuccessfulDelete() {
        _deletecallback.onItemDeleted();
    }

    @Override
    public void OnFailedDelete(String err) {
        Toast.makeText(_context, "Failed to delete payment", Toast.LENGTH_SHORT).show();
    }

    public void AddPaymentsToList(ArrayList<BillObjectDetailedPayments> newPayments)
    {
        if(_payments.size() > 0)
        {
            int oldsize = _payments.size();
            _payments.clear();
            _payments.addAll(newPayments);

            notifyItemRangeRemoved(0, oldsize);
            notifyItemRangeInserted(0, _payments.size());
        }
        else
        {
            _payments.addAll(newPayments);
            notifyItemRangeInserted(0, _payments.size());
        }
    }
}
