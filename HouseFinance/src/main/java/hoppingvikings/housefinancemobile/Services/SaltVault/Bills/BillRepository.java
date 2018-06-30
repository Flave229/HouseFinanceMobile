package hoppingvikings.housefinancemobile.Services.SaltVault.Bills;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;

public class BillRepository
{
    private static BillRepository _instance;
    private ArrayList<BillListObject> _bills;

    private BillRepository()
    {
        _bills = new ArrayList<>();
    }

    public static BillRepository Instance()
    {
        if (_instance != null)
            return _instance;

        _instance = new BillRepository();
        return _instance;
    }

    public void Set(ArrayList<BillListObject> bills)
    {
        _bills.clear();
        _bills.addAll(bills);
    }

    public ArrayList<BillListObject> Get()
    {
        return _bills;
    }

    public BillListObject GetFromId(int id)
    {
        for (BillListObject bill: _bills) {
            if(id == bill.id)
            {
                return bill;
            }
        }
        return null;
    }
}