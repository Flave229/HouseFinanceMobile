package hoppingvikings.housefinancemobile;

public enum ApiErrorCodes {
    SESSION_EXPIRED(100001),
    SESSION_INVALID(100002);

    private int value;

    ApiErrorCodes(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
