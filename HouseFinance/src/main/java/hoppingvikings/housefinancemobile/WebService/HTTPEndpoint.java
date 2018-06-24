package hoppingvikings.housefinancemobile.WebService;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HTTPEndpoint
{
    String value();
}