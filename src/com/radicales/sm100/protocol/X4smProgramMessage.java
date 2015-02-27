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

import java.util.List;

/**
 * XML For Smart Mist Program Message
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
public class X4smProgramMessage extends X4smMessage {

    private List<X4smAttribute> gAttributes;
    private String gName;
    private long gControlWord;
    private int[] gWaterBudget = new int[12];
    private X4smStartTime[] gStartTimes;
    private X4smSequence[] gSequences;

    /*
     * <Program
     * Name="Program F"
     * ControlWord="0x8000"
     * StartTimes="08:00"
     * WaterBudget="080,090,090,090,100,100,100,100,090,090,080,080" />
     *
     */
    public X4smProgramMessage( List<X4smAttribute> Attributes ) {
        super(MSG_TYPE_PROGRAM);
        gAttributes = Attributes;
        gStartTimes = new X4smStartTime[0];
        gSequences = new X4smSequence[0];
    }

    public void parse() throws X4smException  {

        for(X4smAttribute attr : gAttributes) {

            if(attr.Name.matches("name")) {
                gName = attr.Value;
            }
            else if(attr.Name.matches("controlword")) {
                gControlWord = Long.decode(attr.Value);
            }
            else if(attr.Name.matches("starttimes")) {
                String[] st = attr.Value.split(",");
                gStartTimes = new X4smStartTime[st.length];
                for(int i=0; i<st.length; i++) {
                    gStartTimes[i] = new X4smStartTime(0,0);
                    gStartTimes[i].parseTime(st[i]);
                }
            }
            else if(attr.Name.matches("waterbudget")) {
                String[] ss = attr.Value.split(",");

                for(int i=0; (i<ss.length) && (i<12); i++) {
                    gWaterBudget[i] = Integer.parseInt(ss[i]);
                }
            }
            else if(attr.Name.matches("sequence")) {
                String[] ss = attr.Value.split(",");
                gSequences = new X4smSequence[ss.length];
                for(int i=0; i<ss.length; i++) {
                    gSequences[i] = new X4smSequence(0,0);
                    gSequences[i].parseSequence(ss[i]);
                }
            }
        }
    }

    public String getName() {
        return gName;
    }

    public long getControlWord() {
        return gControlWord;
    }

    public int[] getWaterBudget() {
        return gWaterBudget;
    }

    public X4smStartTime[] getStartTimes() {
        return gStartTimes;
    }

    public X4smSequence[] getSequences() {
        return gSequences;
    }

}
