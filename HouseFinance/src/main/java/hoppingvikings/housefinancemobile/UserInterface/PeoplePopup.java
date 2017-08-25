package hoppingvikings.housefinancemobile.UserInterface;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Fragments.Interfaces.ButtonPressedCallback;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObjectPeople;
import hoppingvikings.housefinancemobile.UserInterface.Lists.AllPeopleAdapter;

/**
 * Created by iView on 25/08/2017.
 */

public class PeoplePopup {
    private TextView _title;
    private RecyclerView _rv;
    private Button _dismiss;
    private AllPeopleAdapter _adapter;
    private View _view;
    private boolean _visible = false;

    private AlertDialog.Builder _popupBuilder;
    private AlertDialog _popup;

    public boolean IsVisible()
    {
        return _visible;
    }

    public PeoplePopup(Context context, ViewGroup parent)
    {
        _popupBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        _view = inflater.inflate(R.layout.dialog_list, parent, false);

        _title = (TextView) _view.findViewById(R.id.dialog_title);
        _rv = (RecyclerView) _view.findViewById(R.id.dialog_recycler);
        _dismiss = (Button) _view.findViewById(R.id.dialog_dismiss);
        _adapter = new AllPeopleAdapter(new ArrayList<BillListObjectPeople>(), context);

        _rv.setAdapter(_adapter);
        _rv.setLayoutManager(new GridLayoutManager(context, 3));

        _dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dismiss();
            }
        });

        _popupBuilder.setView(_view);
        _popupBuilder.setCancelable(false);

        _popup = _popupBuilder.create();
    }

    public void Show(ArrayList<BillListObjectPeople> allPeople)
    {
        /* Set up the adapter and attach to recyclerview */

        _adapter.AddUsersAndRefresh(allPeople);
        _popup.show();
        _visible = true;
    }

    private void Dismiss()
    {
        if(_popup != null)
        {
            _popup.dismiss();
        }

        _visible = false;
    }
}
