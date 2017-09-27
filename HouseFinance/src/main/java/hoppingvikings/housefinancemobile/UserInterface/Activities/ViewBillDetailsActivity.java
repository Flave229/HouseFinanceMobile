package hoppingvikings.housefinancemobile.UserInterface.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.Repositories.BillRepository;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillObjectDetailed;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillPayment;
import hoppingvikings.housefinancemobile.UserInterface.PaymentsListAdapter;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

public class ViewBillDetailsActivity extends AppCompatActivity
        implements CommunicationCallback, PaymentsListAdapter.DeleteCallback, PaymentsListAdapter.EditPressedCallback
{
    TextView billAmountText;
    TextView totalPaidText;
    TextView dueDateText;
    TextView noPaymentsText;

    Handler _handler;

    int billID = 0;

    RecyclerView paymentsList;
    PaymentsListAdapter adapter;

    FloatingActionButton addPayment;

    boolean somethingChanged = false;
    CoordinatorLayout layout;

    BillObjectDetailed _currentBill = null;
    private Runnable contactWebsite = new Runnable() {
        @Override
        public void run() {
            WebHandler.Instance().RequestBillDetails(ViewBillDetailsActivity.this, ViewBillDetailsActivity.this, billID);
        }
    };

    @Override
    public void OnItemDeleted() {
        somethingChanged = true;
        _handler.postDelayed(contactWebsite, 100);
    }

    @Override
    public void onEditPressed(BillPayment itemid) {
        Intent edititem = new Intent(this, EditPaymentActivity.class);
        edititem.putExtra("payment_id", itemid.PaymentID);
        edititem.putExtra("payment_amount", itemid.AmountPaid);
        edititem.putExtra("payment_date", itemid.Date);
        startActivityForResult(edititem, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bill);

        layout = (CoordinatorLayout) findViewById(R.id.viewBillLayout);
        billAmountText = (TextView) findViewById(R.id.billAmount);
        totalPaidText = (TextView) findViewById(R.id.totalPaid);
        dueDateText = (TextView) findViewById(R.id.billDueDate);
        noPaymentsText = (TextView) findViewById(R.id.noPaymentsText);
        /*tableContainer = (LinearLayout) findViewById(R.id.tableContainer);*/
        addPayment = (FloatingActionButton) findViewById(R.id.addPaymentButton);
        addPayment.hide();

        paymentsList = (RecyclerView) findViewById(R.id.paymentsList);
        paymentsList.setNestedScrollingEnabled(true);
        adapter = new PaymentsListAdapter(new ArrayList<BillPayment>(), this);
        adapter.SetDeleteCallback(this);
        adapter.SetEditPressedCallback(this);
        paymentsList.setAdapter(adapter);
        paymentsList.setLayoutManager(new LinearLayoutManager(this));
        //paymentsList.addItemDecoration(new ListItemDivider(this));

        addPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BillListObject bill = BillRepository.Instance().GetFromId(_currentBill.id);
                Intent i = new Intent(ViewBillDetailsActivity.this, AddPaymentActivity.class);
                i.putExtra("bill_id", _currentBill.id);
                i.putExtra("bill_name", _currentBill.name);
                double suggestedAmount;
                if(bill.people.size() > 1)
                {
                    suggestedAmount = bill.totalAmount / bill.people.size();
                }
                else
                {
                    suggestedAmount = (bill.totalAmount - _currentBill.amountPaid);
                }
                i.putExtra("suggested_amount", String.format(Locale.getDefault(), "%.2f", suggestedAmount));
                startActivityForResult(i, 0);
            }
        });

        if(getIntent() != null)
        {
            if(getIntent().hasExtra("bill_id"))
            {
                billID = getIntent().getIntExtra("bill_id", -1);
            }
        }

        Toolbar toolbar = (Toolbar)findViewById(R.id.appToolbar);
        toolbar.setTitle("Loading Bill...");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _handler = new Handler();

        _handler.postDelayed(contactWebsite, 200);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.viewdetailsmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.editBill:
                Intent editBillIntent = new Intent(ViewBillDetailsActivity.this, EditBillDetailsActivity.class);
                editBillIntent.putExtra("bill_id", _currentBill.id);
                startActivityForResult(editBillIntent, 10);
                return true;

            case R.id.delete_bill:
                final AlertDialog deleteconfirm = new AlertDialog.Builder(ViewBillDetailsActivity.this).create();
                deleteconfirm.setMessage("Delete the bill? This action cannot be reversed");
                deleteconfirm.setButton(DialogInterface.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addPayment.hide();
                        deleteconfirm.dismiss();
                        try {
                            JSONObject billidjson = new JSONObject();
                            billidjson.put("BillId", billID);
                            WebHandler.Instance().DeleteItem(ViewBillDetailsActivity.this, ViewBillDetailsActivity.this, billidjson, ItemType.BILL);
                        } catch (Exception e)
                        {
                            addPayment.show();
                        }
                    }
                });

                deleteconfirm.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteconfirm.dismiss();
                    }
                });

                deleteconfirm.show();
                return true;


            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        if(somethingChanged)
            setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
            case RESULT_OK:
                somethingChanged = true;
                _handler.postDelayed(contactWebsite, 100);
                break;

            case RESULT_CANCELED:

                break;
        }
    }

    @Override
    public void OnSuccess(RequestType requestType, Object result)
    {
        switch (requestType)
        {
            case GET:
                BillObjectDetailed billDetails = (BillObjectDetailed) result;
                if (billDetails == null)
                    return;

                _currentBill = billDetails;
                getSupportActionBar().setTitle("Bill Details");
                getSupportActionBar().setSubtitle(_currentBill.name);
                ViewBillDetailsActivity.this.billAmountText.setText("£" + String.format(Locale.getDefault(), "%.2f", Double.valueOf(_currentBill.amountDue)));
                ViewBillDetailsActivity.this.totalPaidText.setText("£" + String.format(Locale.getDefault(), "%.2f", Double.valueOf(_currentBill.amountPaid)));
                ViewBillDetailsActivity.this.dueDateText.setText(_currentBill.dateDue);

                adapter.AddPaymentsToList(_currentBill.paymentDetails);

                if(adapter.getItemCount() > 0)
                {
                    noPaymentsText.setVisibility(View.GONE);
                }
                else
                {
                    noPaymentsText.setVisibility(View.VISIBLE);
                }

                addPayment.show();
                break;
            case DELETE:
                Toast.makeText(getApplicationContext(), "Bill deleted", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    @Override
    public void OnFail(RequestType requestType, String message)
    {
        Snackbar.make(layout, message, Snackbar.LENGTH_SHORT).show();

        switch (requestType)
        {
            case POST:
                billAmountText.setText("N/A");
                totalPaidText.setText("N/A");
                dueDateText.setText("N/A");
                getSupportActionBar().setTitle("Cannot load bill");
                break;
            case DELETE:
                addPayment.show();
                break;
        }
    }
}
