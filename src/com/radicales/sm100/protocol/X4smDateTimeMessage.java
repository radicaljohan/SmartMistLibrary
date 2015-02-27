/*
 * Copyright (C) 2012-2015 Radical Electronic Systems, South Africa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.radicales.sm100.protocol;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * XML For Smart Mist Time Message
 *
 * @author
 * Jan Zwiegers,
 * <a href="mailto:jan@radicalsystems.co.za">jan@radicalsystems.co.za</a>,
 * <a href="http://www.radicalsystems.co.za">www.radicalsystems.co.za</a>
 *
 * @version
 * <b>1.0 01/11/2014</b><br>
 * Original release.
 */
public class X4smDateTimeMessage extends X4smMessage {

    private List<X4smAttribute> gAttributes;
    private Calendar gCalendar;
    private Date gDateTime;

    public X4smDateTimeMessage( List<X4smAttribute> Attributes ) {
        super(MSG_TYPE_DATETIME);
        gAttributes = Attributes;
        gDateTime = new Date();
        gCalendar = Calendar.getInstance();
    }

    public Date getDateTime() {
        return gDateTime;
    }

    public Calendar getCalender() {
        return gCalendar;
    }

    private void parseTime( String Time ) {
        int s1 = Time.indexOf(":");
        int s2 = Time.indexOf(":", s1 + 1);
        String h = Time.substring(0, s1);
        String m = Time.substring(s1 + 1, s2);
        String s = Time.substring(s2 + 1);
        gCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(h));
        gCalendar.set(Calendar.MINUTE, Integer.parseInt(m));
        gCalendar.set(Calendar.SECOND, Integer.parseInt(s));
    }

    private void parseDate( String Date ) {
        int s1 = Date.indexOf("/");
        int s2 = Date.indexOf("/", s1 + 1);
        int s3 = Date.indexOf("/", s2 + 1);
        String d = Date.substring(0, s1);
        String wd = Date.substring(s1 + 1, s2);
        String m = Date.substring(s2 + 1, s3);
        String y = Date.substring(s3 + 1);
        gCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(d));
        gCalendar.set(Calendar.MONTH, Integer.parseInt(m));
        gCalendar.set(Calendar.YEAR, Integer.parseInt(y));
    }

    public void parse() throws X4smException  {

        for(X4smAttribute attr : gAttributes) {

            if(attr.Name.matches("date")) {
                parseDate(attr.Value);
            }
            else if(attr.Name.matches("time")) {
                parseTime(attr.Value);
            }
        }

        gDateTime = gCalendar.getTime();
    }

}
