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

/**
 * XML For Smart Mist Set Date/Time Message
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
public class X4smSetDateTimeMessage extends X4smMessage {

    private int gDay;
    private int gWDay;
    private int gMonth;
    private int gYear;
    private int gHour;
    private int gMinute;
    private int gSeconds;

    public X4smSetDateTimeMessage( int Day, int WeekDay, int Month, int Year, int Hour, int Minute, int Seconds ) {
        super(X4smMessage.MSG_TYPE_SET_ZONE);
       gDay = Day;
       gWDay = WeekDay;
       gMonth = Month;
       gYear = Year;
       gHour = Hour;
       gMinute = Minute;
       gSeconds = Seconds;
       encode();
    }

    private void encode() {
        String msg = "<SetDateTime date=\"";
        msg = msg + Integer.toString(gDay) + "/" + Integer.toString(gWDay) + "/" + Integer.toString(gMonth) + "/" + Integer.toString(gYear) + "\" ";
        msg = msg + " time=\"" + Integer.toString(gHour) + ":" + Integer.toString(gMinute) + ":" + Integer.toString(gSeconds) + "\" ";
        msg = msg + "/>";
        setData(msg);
    }

}
