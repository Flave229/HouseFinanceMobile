package flaveandmalnub.housefinancemobile;

import java.util.ArrayList;

import flaveandmalnub.housefinancemobile.UserInterface.Lists.BillList.BillListObject;
import flaveandmalnub.housefinancemobile.UserInterface.Lists.BillList.BillListObjectPeople;
import flaveandmalnub.housefinancemobile.UserInterface.Lists.ShoppingList.ShoppingListObject;
import flaveandmalnub.housefinancemobile.UserInterface.Lists.ShoppingList.ShoppingListPeople;
import flaveandmalnub.housefinancemobile.WebService.BackgroundService;

/**
 * Created by Josh on 25/09/2016.
 */

public class GlobalObjects{

    static ArrayList<BillListObject> _bills;
    static ArrayList<BillListObjectPeople> _billsPeople;

    static ArrayList<ShoppingListObject> _shoppingItems;
    static ArrayList<ShoppingListPeople> _shoppingPeople;

    public static BackgroundService _service;
    public static boolean _bound = false;

    public static boolean downloading = false;

    public static void SetBills(ArrayList<BillListObject> bills)
    {
        _bills = bills;
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
        _billsPeople = people;
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
        _shoppingItems = items;
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
        _shoppingPeople = people;
    }
}
