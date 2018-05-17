package hoppingvikings.housefinancemobile.UserInterface.Items;

import hoppingvikings.housefinancemobile.ItemType;

public class MainMenuItem {

    public int itemImageID;
    public String itemNameString;
    public String menuItemType;

    public MainMenuItem(String itemName, int imageID, String type)
    {
        itemImageID = imageID;
        itemNameString = itemName;
        menuItemType = type;
    }
}
