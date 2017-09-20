package hoppingvikings.housefinancemobile.MemoryRepositories;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;

public class BillMemoryRepository
{
    private ArrayList<BillListObject> _bills;

    public BillMemoryRepository()
    {
        _bills = new ArrayList<>();
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