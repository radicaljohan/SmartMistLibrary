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
package com.radicales.sm100.device;

/**
 * Watering Program Start Time
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

    public boolean matches( StartTime Time ) {
        return ((Time.Hour == this.Hour) &&
                (Time.Minute == this.Minute));
    }

    public boolean overlaps( StartTime Time, int RunTime ) {
        int min = this.Minute + RunTime;
        int hr = this.Hour + (min / 60);
        min = min % 60;

        if((Time.Hour >= this.Hour ) && (Time.Minute >= this.Minute)) {
            if((Time.Hour <= hr) && (Time.Minute < min)) {
                return true;
            }
        }

        return false;
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
