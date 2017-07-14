package hoppingvikings.housefinancemobile;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.UserInterface.Lists.BillList.BillListObject;
import hoppingvikings.housefinancemobile.UserInterface.Lists.BillList.BillListObjectPeople;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingList.ShoppingListObject;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingList.ShoppingListPeople;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

/**
 * Created by Josh on 25/09/2016.
 */

public class GlobalObjects{

    static ArrayList<BillListObject> _bills = new ArrayList<>();
    static ArrayList<BillListObjectPeople> _billsPeople = new ArrayList<>();

    static ArrayList<ShoppingListObject> _shoppingItems = new ArrayList<>();
    static ArrayList<ShoppingListPeople> _shoppingPeople = new ArrayList<>();

    public static WebHandler webHandler;
    public static BackgroundService backgroundService;
    public static boolean _bound = false;

    public static boolean downloading = false;

    public static void SetBills(ArrayList<BillListObject> bills)
    {
        _bills.clear();
        _bills.addAll(bills);
    }

    public static ArrayList<BillListObject> GetBills()
    {
        return _bills;
    }

    public static BillListObject GetBillFromID(String id)
    {
        for (BillListObject bill: _bills) {
            if(id.equals(bill.ID))
            {
                return bill;
            }
        }
        return null;
    }

    public static void SetBillPeopleList(ArrayList<BillListObjectPeople> people)
    {
        _billsPeople.clear();
        _billsPeople.addAll(people);
    }

    public static BillListObjectPeople GetPersonFromID(String id)
    {
        for (BillListObjectPeople person:_billsPeople) {
            if(id.equals(person.ID))
            {
                return person;
            }
        }

        return null;
    }

    public static void SetShoppingItems(ArrayList<ShoppingListObject> items)
    {
        _shoppingItems.clear();
        _shoppingItems.addAll(items);
    }

    public static ArrayList<ShoppingListObject> GetShoppingItems()
    {
        return _shoppingItems;
    }


    public static ShoppingListObject GetShoppingItemFromID(String id)
    {
        for(ShoppingListObject item : _shoppingItems)
        {
            if(id.equals(item.ID))
            {
                return item;
            }
        }
        return null;
    }

    public static void SetShoppingPeopleList(ArrayList<ShoppingListPeople> people)
    {
        _shoppingPeople.clear();
        _shoppingPeople.addAll(people);
    }

    public static void ShowNotif(String text, String subtext, int notifid)
    {
        AppServiceBinder._service.ShowNotification(text, subtext, notifid);
    }
}
