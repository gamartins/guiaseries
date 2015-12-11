package br.com.martinsdev.guiadeseries.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by gabriel on 09/12/15.
 */
public class Converter {
    static public Calendar convertDate(String unformattedDate){
        Calendar cal = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            cal = Calendar.getInstance();
            cal.setTime(sdf.parse(unformattedDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return cal;
    }
}