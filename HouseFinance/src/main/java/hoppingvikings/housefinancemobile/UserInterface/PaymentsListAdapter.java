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

import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.Services.SaltVault.Bills.PaymentsEndpoint;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillPayment;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;

public class PaymentsListAdapter extends RecyclerView.Adapter<PaymentsListAdapter.CardViewHolder>
        implements CommunicationCallback
{
    private final PaymentsEndpoint _paymentEndpoint;

    public interface DeleteCallback
    {
        void OnItemDeleted();
    }

    private static PaymentsListAdapter.DeleteCallback _deleteCallback;
    private static PaymentsListAdapter.EditPressedCallback _editCallback;

    public interface EditPressedCallback
    {
        void onEditPressed(BillPayment item);
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

    ArrayList<BillPayment> _payments = new ArrayList<>();
    Context _context;

    public void SetDeleteCallback(DeleteCallback owner)
    {
        _deleteCallback = owner;
    }

    public void SetEditPressedCallback(EditPressedCallback owner)
    {
        _editCallback = owner;
    }

    public PaymentsListAdapter(Context context, PaymentsEndpoint paymentEndpoint, ArrayList<BillPayment> payments)
    {
        _context = context;
        _paymentEndpoint = paymentEndpoint;
        _payments.addAll(payments);
    }

    @Override
    public int getItemCount()
    {
        return _payments.size();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_billpayment, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position)
    {
        final BillPayment item = _payments.get(position);
        holder.paymentName.setText(item.Person.FirstName + " " + item.Person.Surname);
        holder.paymentDate.setText("Date: " + item.Date);
        holder.paymentAmount.setText("Paid: £" + String.format(Locale.getDefault(), "%.2f", item.AmountPaid));
        holder.editPayment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                _editCallback.onEditPressed(item);
            }
        });
        holder.deletePayment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try
                {
                    JSONObject paymentToDelete = new JSONObject();
                    paymentToDelete.put("PaymentId", item.PaymentID);
                    _paymentEndpoint.Delete(_context, PaymentsListAdapter.this, paymentToDelete);
                }
                catch (Exception e)
                {

                }
            }
        });
    }

    @Override
    public void OnSuccess(RequestType requestType, Object s)
    {
        if (requestType == RequestType.DELETE)
            _deleteCallback.OnItemDeleted();
    }

    @Override
    public void OnFail(RequestType requestType, String message)
    {
        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
    }

    public void AddPaymentsToList(ArrayList<BillPayment> newPayments)
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