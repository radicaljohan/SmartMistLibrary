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
 * Smart Mist 100 Watering Program Day Object
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
public class Sm100ProgramDays {

    /* Program Control Words */
    private static final long CW_EVENDAY = 0x00000010;
    private static final long CW_ODDDAY = 0x00000020;
    private static final long CW_ODD31DAY = 0x00000040;
    private static final long CW_DAYCYCLE = 0x00000080;

    private static final long CW_SUNDAY = 0x00000100;
    private static final long CW_MONDAY = 0x00000200;
    private static final long CW_TUESDAY = 0x00000400;
    private static final long CW_WEDNESDAY = 0x00000800;
    private static final long CW_THURSDAY = 0x00001000;
    private static final long CW_FRIDAY = 0x000002000;
    private static final long CW_SATURDAY = 0x000004000;
    private static final long CW_ALLDAYS = 0x00007f000;

    public boolean EvenDays;
    public boolean OddDays;
    public boolean Odd31Days;
    public boolean DayCycle;
    public boolean Sundays;
    public boolean Mondays;
    public boolean Tuesdays;
    public boolean Wednesdays;
    public boolean Thursdays;
    public boolean Fridays;
    public boolean Saturdays;
    public boolean AllDays;
    public int CycleDays;

    private long gControlWord;

    public Sm100ProgramDays( long ControlWord, int cycleDays ) {
        gControlWord = ControlWord;
        CycleDays = cycleDays;
        decode();
    }

    public Sm100ProgramDays() {
        gControlWord = 0;
        CycleDays = 1;
        decode();
    }

    public void Encode() {
        gControlWord = 0;

        if(EvenDays) {
            gControlWord |= CW_EVENDAY;
        }

        if(OddDays) {
            gControlWord |= CW_ODDDAY;
        }

        if(Odd31Days) {
            gControlWord |= CW_ODD31DAY;
        }

        if(DayCycle) {
            gControlWord |= CW_DAYCYCLE;
        }

        if(Sundays) {
            gControlWord |= CW_SUNDAY;
        }

        if(Mondays) {
            gControlWord |= CW_MONDAY;
        }

        if(Tuesdays) {
            gControlWord |= CW_TUESDAY;
        }

        if(Wednesdays) {
            gControlWord |= CW_WEDNESDAY;
        }

        if(Thursdays) {
            gControlWord |= CW_THURSDAY;
        }

        if(Fridays) {
            gControlWord |= CW_FRIDAY;
        }

        if(Saturdays) {
            gControlWord |= CW_SATURDAY;
        }

        if(AllDays) {
            gControlWord |= CW_ALLDAYS;
        }

    }

    public void Decode() {
        decode();
    }

    private void decode() {
        EvenDays = false;
        OddDays = false;
        Odd31Days = false;
        DayCycle = false;
        Mondays = false;
        Tuesdays = false;
        Wednesdays = false;
        Thursdays = false;
        Fridays = false;
        Saturdays = false;
        AllDays = false;
        CycleDays= 1;

        if((gControlWord & CW_EVENDAY) == CW_EVENDAY) {
            EvenDays = true;
        }

        if((gControlWord & CW_ODDDAY) == CW_ODDDAY) {
            OddDays = true;
        }

        if((gControlWord & CW_ODD31DAY) == CW_ODD31DAY) {
            Odd31Days = true;
        }

        if((gControlWord & CW_DAYCYCLE) == CW_DAYCYCLE) {
            DayCycle = true;
        }

        if((gControlWord & CW_SUNDAY) == CW_SUNDAY) {
            Sundays = true;
        }

        if((gControlWord & CW_MONDAY) == CW_MONDAY) {
            Mondays = true;
        }

        if((gControlWord & CW_TUESDAY) == CW_TUESDAY) {
            Tuesdays = true;
        }

        if((gControlWord & CW_WEDNESDAY) == CW_WEDNESDAY) {
            Wednesdays = true;
        }

        if((gControlWord & CW_THURSDAY) == CW_THURSDAY) {
            Thursdays = true;
        }

        if((gControlWord & CW_FRIDAY) == CW_FRIDAY) {
            Fridays = true;
        }

        if((gControlWord & CW_SATURDAY) == CW_SATURDAY) {
            Saturdays = true;
        }

        if((gControlWord & CW_ALLDAYS) == CW_ALLDAYS) {
            AllDays = true;
        }

    }
}
