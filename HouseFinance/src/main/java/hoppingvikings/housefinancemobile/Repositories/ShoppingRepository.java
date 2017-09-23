package hoppingvikings.housefinancemobile.Repositories;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.UserInterface.Items.ShoppingListObject;

public class ShoppingRepository
{
    private static ShoppingRepository _instance;
    private ArrayList<ShoppingListObject> _shoppingItems = new ArrayList<>();

    private ShoppingRepository()
    {

    }

    public static ShoppingRepository Instance()
    {
        if (_instance != null)
            return _instance;

        _instance = new ShoppingRepository();
        return _instance;
    }

    public void Set(ArrayList<ShoppingListObject> items)
    {
        _shoppingItems.clear();
        _shoppingItems.addAll(items);
    }

    public ArrayList<ShoppingListObject> Get()
    {
        return _shoppingItems;
    }

    public ShoppingListObject GetFromId(int id)
    {
        for(ShoppingListObject item : _shoppingItems)
        {
            if(id == item.Id)
            {
                return item;
            }
        }
        return null;
    }
}