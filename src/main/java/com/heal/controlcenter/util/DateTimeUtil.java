package com.heal.controlcenter.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
@Component
public class DateTimeUtil {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public Date getDateInGMT(long time) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateTime = simpleDateFormat.format(time);
        return simpleDateFormat.parse(dateTime);
    }

    public String getTimeInGMT(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpleDateFormat.format(time);
    }

    public Long getGMTToEpochTime(String time) {
        DateFormat simpleDateFormat;
        try {
            simpleDateFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = simpleDateFormat.parse(time.trim());
            return date.getTime();
        }catch (Exception e)    {
            return 0L;
        }
    }

    public Timestamp getCurrentTimestampInGMT() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat localDateFormat = new SimpleDateFormat(Constants.DATE_TIME);
            return new Timestamp(localDateFormat.parse( simpleDateFormat.format(new Date())).getTime());
        } catch (ParseException e) {
            log.error("Error in getting current time stamp in GMT");
        }
        return null;
    }
}
