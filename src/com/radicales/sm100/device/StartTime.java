/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.device;

/**
 *
 * @author JanZwiegers
 */
public class StartTime {

    public int Hour;
    public int Minute;

    public StartTime( int hour, int minute ) {
        Hour = hour;
        Minute = minute;
    }

    public StartTime( String Time ) {
        parse(Time);
    }

    private void parse( String Value ) {
        String[] st = Value.split(":");
        Hour = Integer.parseInt(st[0]);
        Minute = Integer.parseInt(st[1]);
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", Hour, Minute);
    }

}
