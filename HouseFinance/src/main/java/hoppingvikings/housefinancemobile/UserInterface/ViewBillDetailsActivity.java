package hoppingvikings.housefinancemobile.UserInterface;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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

import java.util.Locale;

import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Lists.BillList.BillListObject;
import hoppingvikings.housefinancemobile.UserInterface.Lists.BillList.BillObjectDetailed;
import hoppingvikings.housefinancemobile.WebService.DownloadDetailsCallback;

/**
 * Created by iView on 06/07/2017.
 */

public class ViewBillDetailsActivity extends AppCompatActivity implements DownloadDetailsCallback {

    TextView billAmountText;
    TextView totalPaidText;
    TextView dueDateText;

    Handler _handler;

    String billID = "";

    LinearLayout tableContainer;
    FloatingActionButton addPayment;

    BillObjectDetailed _currentBill = null;
    private Runnable contactWebsite = new Runnable() {
        @Override
        public void run() {
            GlobalObjects.webHandler.RequestBillDetails(ViewBillDetailsActivity.this, ViewBillDetailsActivity.this, billID);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bill);

        billAmountText = (TextView) findViewById(R.id.billAmount);
        totalPaidText = (TextView) findViewById(R.id.totalPaid);
        dueDateText = (TextView) findViewById(R.id.billDueDate);
        tableContainer = (LinearLayout) findViewById(R.id.tableContainer);
        addPayment = (FloatingActionButton) findViewById(R.id.addPaymentButton);
        addPayment.hide();

        addPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BillListObject bill = GlobalObjects.GetBillFromID(_currentBill.id);
                Intent i = new Intent(ViewBillDetailsActivity.this, AddPaymentActivity.class);
                i.putExtra("bill_id", billID);
                i.putExtra("bill_name", _currentBill.name);
                i.putExtra("suggested_amount", String.format(Locale.getDefault(), "%.2f", Double.valueOf(bill.totalAmount) / bill.people.size()));
                startActivityForResult(i, 0);
            }
        });

        if(getIntent() != null)
        {
            if(getIntent().hasExtra("bill_id"))
            {
                billID = getIntent().getStringExtra("bill_id");
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

        this.tableContainer.addView(view);

        return table;
    }

    private void ClearTable()
    {
        tableContainer.removeAllViews();
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
        //getMenuInflater().inflate(R.menu.viewdetailsmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.delete_bill:
                //todo Add code to delete bill when ready
                return true;


            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void OnDownloadFailed(String err) {
        Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
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

        if(billObjectDetailed.paymentDetails.size() > 0)
        {
            for (JSONObject paymentInfo: billObjectDetailed.paymentDetails) {
                try {
                    table = this.CreateNewTable(paymentInfo.getString("personName"));
                    this.AddRow(table, "Date Paid", paymentInfo.getString("datePaid"));
                    this.AddRow(table, "Amount Paid", "£" + String.valueOf(paymentInfo.getDouble("amountPaid")));
                } catch (JSONException e)
                {
                    Log.v("Error payment info: ", e.getMessage());
                }
            }
        }
        else
        {
            table = this.CreateNewTable("No Payments Found");
            this.AddRow(table, "Press the button below to add a payment!", "");
        }

        addPayment.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
            case RESULT_OK:
                ClearTable();
                _handler.postDelayed(contactWebsite, 100);
                break;

            case RESULT_CANCELED:

                break;
        }
    }
}
