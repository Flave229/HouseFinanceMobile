package hoppingvikings.housefinancemobile.Services.SaltVault.ToDo;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.UserInterface.Items.TodoListObject;

/**
 * Created by Josh on 02/10/2017.
 */

public class TodoRepository {
    private static TodoRepository _instance;
    private ArrayList<TodoListObject> _todos;

    private TodoRepository()
    {
        _todos = new ArrayList<>();
    }

    public static TodoRepository Instance()
    {
        if(_instance != null)
            return _instance;

        _instance = new TodoRepository();
        return _instance;
    }

    public void Set(ArrayList<TodoListObject> newTodos)
    {
        _todos.clear();
        _todos.addAll(newTodos);
    }

    public ArrayList<TodoListObject> Get()
    {
        return _todos;
    }

    public TodoListObject GetFromId(int id)
    {
        for (TodoListObject todoitem: _todos) {
            if(id == todoitem.id)
            {
                return todoitem;
            }
        }
        return null;
    }
}
