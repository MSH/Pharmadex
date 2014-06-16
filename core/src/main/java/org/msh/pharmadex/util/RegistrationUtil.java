package org.msh.pharmadex.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by usrivastava on 06/16/2014.
 */
public class RegistrationUtil {

    public String generateAppNo(Long prodAppID) {
        String prodAppNo = "";
        Calendar calendar = Calendar.getInstance(Locale.US);
        String year = ""+calendar.get(Calendar.YEAR);
        int m = (calendar.get(Calendar.MONTH)+1);
        String month = String.format("%02d", m);
        String no = String.format("%04d", prodAppID);

        prodAppNo = year+month+no;
        return prodAppNo;
    }
}
