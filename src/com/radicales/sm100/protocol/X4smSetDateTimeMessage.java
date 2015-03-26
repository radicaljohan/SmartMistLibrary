/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

/**
 *
 * @author JanZwiegers
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
        String msg = "<SetDateTime Date=\"";
        msg = msg + Integer.toString(gDay) + "/" + Integer.toString(gWDay) + "/" + Integer.toString(gMonth) + "/" + Integer.toString(gYear) + "\" ";
        msg = msg + " Time=\"" + Integer.toString(gHour) + ":" + Integer.toString(gMinute) + ":" + Integer.toString(gSeconds) + "\" ";
        msg = msg + "/>";
        setData(msg);
    }

}
