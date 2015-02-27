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

import com.radicales.sm100.device.StartTime;

/**
 * XML For Smart Mist Add Program Message
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
public class X4smAddProgramMessage extends X4smMessage {

    private String gName;
    private long gControlWord;
    private int[] gWaterBudget;
    private StartTime[] gStartTimes;

    public X4smAddProgramMessage( String Name, long ControlWord, int[] WaterBudget, StartTime[] StartTimes) {
        super(MSG_TYPE_ADD_PROGRAM);
        gName = Name;
        gControlWord = ControlWord;
        gWaterBudget = WaterBudget;
        gStartTimes = StartTimes;
        encode();
    }

    private void encode() {
        String wbs = Integer.toString(gWaterBudget[0]);
        for(int i=1; i<gWaterBudget.length; i++) {
            wbs = wbs + "," + Integer.toString(gWaterBudget[i]);
        }

        String sts = gStartTimes[0].toString();
        for(int i=1; i<gStartTimes.length; i++) {
            sts = sts + "," + gStartTimes[i].toString();
        }

        String msg = "<AddProgram name=\"" + gName + "\" controlword=\"0x" + Long.toHexString(gControlWord) +
                "\" waterbudget=\"" + wbs +
                "\" starttimes=\"" + sts + "\" />";
        setData(msg);
    }

}
