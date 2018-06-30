package hoppingvikings.housefinancemobile;

import java.util.HashMap;
import java.util.Map;

public enum ApiErrorCodes {
    NONE(-1),
    SESSION_EXPIRED(100001),
    SESSION_INVALID(100002),
    USER_NOT_IN_HOUSEHOLD(100003);

    private int value;

    private static final Map lookup = new HashMap();

    static
    {
        for(ApiErrorCodes e : ApiErrorCodes.values())
            lookup.put(e.getValue(), e);
    }

    ApiErrorCodes(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
    public static ApiErrorCodes get(int value)
    {
        return (ApiErrorCodes)lookup.get(value);
    }
}
