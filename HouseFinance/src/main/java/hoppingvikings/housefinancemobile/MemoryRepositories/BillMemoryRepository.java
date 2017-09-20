package hoppingvikings.housefinancemobile.MemoryRepositories;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;

public class BillMemoryRepository
{
    private static BillMemoryRepository _instance;
    private ArrayList<BillListObject> _bills;

    private BillMemoryRepository()
    {
        _bills = new ArrayList<>();
    }

    public static BillMemoryRepository Instance()
    {
        if (_instance != null)
            return _instance;

        _instance = new BillMemoryRepository();
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