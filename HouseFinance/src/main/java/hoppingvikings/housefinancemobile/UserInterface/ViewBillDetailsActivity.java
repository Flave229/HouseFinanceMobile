package hoppingvikings.housefinancemobile.UserInterface;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillObjectDetailed;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillObjectDetailedPayments;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ListItemDivider;
import hoppingvikings.housefinancemobile.WebService.DeleteItemCallback;
import hoppingvikings.housefinancemobile.WebService.DownloadDetailsCallback;
import hoppingvikings.housefinancemobile.WebService.UploadCallback;

/**
 * Created by iView on 06/07/2017.
 */

public class ViewBillDetailsActivity extends AppCompatActivity
        implements DownloadDetailsCallback, UploadCallback, DeleteItemCallback,
        PaymentsListAdapter.DeleteCallback, PaymentsListAdapter.EditPressedCallback{

    TextView billAmountText;
    TextView totalPaidText;
    TextView dueDateText;
    TextView noPaymentsText;

    Handler _handler;

    int billID = 0;

    RecyclerView paymentsList;
    PaymentsListAdapter adapter;

    //LinearLayout tableContainer;
    FloatingActionButton addPayment;

    boolean somethingChanged = false;
    CoordinatorLayout layout;

    BillObjectDetailed _currentBill = null;
    private Runnable contactWebsite = new Runnable() {
        @Override
        public void run() {
            GlobalObjects.webHandler.RequestBillDetails(ViewBillDetailsActivity.this, ViewBillDetailsActivity.this, billID);
        }
    };

    @Override
    public void onItemDeleted() {
        somethingChanged = true;
        _handler.postDelayed(contactWebsite, 100);
    }

    @Override
    public void onEditPressed(BillObjectDetailedPayments itemid) {
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
        adapter = new PaymentsListAdapter(new ArrayList<BillObjectDetailedPayments>(), this);
        adapter.SetDeleteCallback(this);
        adapter.SetEditPressedCallback(this);
        paymentsList.setAdapter(adapter);
        paymentsList.setLayoutManager(new LinearLayoutManager(this));
        //paymentsList.addItemDecoration(new ListItemDivider(this));

        addPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BillListObject bill = GlobalObjects.GetBillFromID(_currentBill.id);
                Intent i = new Intent(ViewBillDetailsActivity.this, AddPaymentActivity.class);
                i.putExtra("bill_id", _currentBill.id);
                i.putExtra("bill_name", _currentBill.name);
                double suggestedamount;
                if(bill.people.size() > 1)
                {
                    suggestedamount = Double.valueOf(bill.billTotalAmount) / bill.people.size();
                }
                else
                {
                    suggestedamount = (Double.valueOf(bill.billTotalAmount) - _currentBill.amountPaid);
                }
                i.putExtra("suggested_amount", String.format(Locale.getDefault(), "%.2f", suggestedamount));
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

    private TableLayout CreateNewTable(String header)
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.table_template, null);

        TextView title = (TextView) view.findViewById(R.id.paymentsTable_Header);
        title.setText(header);
        title.setPadding(2,2,2,2);

        TableLayout table = (TableLayout)view.findViewById(R.id.paymentsTable);

        //this.tableContainer.addView(view);

        return table;
    }

    private void ClearTable()
    {
        //tableContainer.removeAllViews();
    }

    private void AddRow(TableLayout table, String header, String value)
    {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        TableRow.LayoutParams tvlayout = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
        tvlayout.weight = 1;

        // Header
        TextView th = new TextView(this);
        th.setTextColor(Color.BLACK);
        th.setLayoutParams(tvlayout);
        th.setText(header);
        row.addView(th);

        // Value
        tvlayout.weight = 2;
        TextView tv = new TextView(this);
        tv.setLayoutParams(tvlayout);
        tv.setTextColor(Color.BLACK);
        tv.setText(value);
        row.addView(tv);

        // Add the row
        table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
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
                            GlobalObjects.webHandler.DeleteItem(ViewBillDetailsActivity.this, ViewBillDetailsActivity.this, billidjson, GlobalObjects.ITEM_TYPE_BILL);
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
    public void OnDownloadFailed(String err) {
        Snackbar.make(layout, err, Snackbar.LENGTH_SHORT).show();
        billAmountText.setText("N/A");
        totalPaidText.setText("N/A");
        dueDateText.setText("N/A");
        getSupportActionBar().setTitle("Cannot load bill");
    }

    @Override
    public void OnDownloadSuccessful(BillObjectDetailed billObjectDetailed) {
        TableLayout table;
        _currentBill = billObjectDetailed;
        getSupportActionBar().setTitle("Bill Details");
        getSupportActionBar().setSubtitle(_currentBill.name);
        this.billAmountText.setText("£" + String.format(Locale.getDefault(), "%.2f", Double.valueOf(_currentBill.amountDue)));
        this.totalPaidText.setText("£" + String.format(Locale.getDefault(), "%.2f", Double.valueOf(_currentBill.amountPaid)));
        this.dueDateText.setText(_currentBill.dateDue);

        adapter.AddPaymentsToList(billObjectDetailed.paymentDetails);

        if(adapter.getItemCount() > 0)
        {
            noPaymentsText.setVisibility(View.GONE);
        }
        else
        {
            noPaymentsText.setVisibility(View.VISIBLE);
        }

        addPayment.show();
    }

    @Override
    public void OnSuccessfulUpload() {

    }

    @Override
    public void OnFailedUpload(String failReason) {

    }

    @Override
    public void OnSuccessfulDelete() {
        Toast.makeText(getApplicationContext(), "Bill deleted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void OnFailedDelete(String err) {
        addPayment.show();
        Snackbar.make(layout, err, Snackbar.LENGTH_SHORT).show();
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
}
