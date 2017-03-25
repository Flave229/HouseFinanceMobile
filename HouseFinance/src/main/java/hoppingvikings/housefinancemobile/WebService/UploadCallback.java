package hoppingvikings.housefinancemobile.WebService;

/**
 * Created by Josh on 25/03/2017.
 */

public interface UploadCallback {
    void OnSuccessfulUpload();
    void OnFailedUpload(String failReason);
}
