package hoppingvikings.housefinancemobile.WebService;

/**
 * Created by Josh on 25/03/2017.
 */

public interface DownloadCallback {
    void OnSuccessfulDownload();
    void OnFailedDownload(String failReason);
}
