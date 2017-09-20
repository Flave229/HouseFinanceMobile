package hoppingvikings.housefinancemobile.MemoryRepositories;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.UserInterface.Items.ShoppingListObject;

public class ShoppingMemoryRepository
{
    private ArrayList<ShoppingListObject> _shoppingItems = new ArrayList<>();

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