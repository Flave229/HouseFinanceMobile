package hoppingvikings.housefinancemobile.WebService;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.Person;

/**
 * Created by iView on 23/08/2017.
 */

public interface DownloadPeopleCallback {
    void UsersDownloadSuccess(ArrayList<Person> users);
    void UsersDownloadFailed(String err);
}
