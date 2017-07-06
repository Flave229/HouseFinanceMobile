package hoppingvikings.housefinancemobile.WebService;

import hoppingvikings.housefinancemobile.UserInterface.Lists.BillList.BillObjectDetailed;

/**
 * Created by iView on 06/07/2017.
 */

public interface DownloadDetailsCallback {
    void OnDownloadSuccessful(BillObjectDetailed billObjectDetailed);
    void OnDownloadFailed(String err);
}
