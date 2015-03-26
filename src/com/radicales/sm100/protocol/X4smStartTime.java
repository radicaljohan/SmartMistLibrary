/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

import com.radicales.sm100.device.StartTime;

/**
 *
 * @author JanZwiegers
 */
public class X4smStartTime {

    public int Hour;
    public int Minute;

    public X4smStartTime( int hour, int minute ) {
        Hour = hour;
        Minute = minute;
    }

    public void parseTime( String Time ) {
        String[] ts = Time.split(":");
        Hour = Integer.parseInt(ts[0]);
        Minute = Integer.parseInt(ts[1]);
    }

    @Override
    public String toString() {
        return Integer.toString(Hour) + ":" + Integer.toString(Minute);
    }

}
