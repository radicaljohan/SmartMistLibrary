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
 * XML For Smart Mist Active Program Message
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
public class X4smActiveProgramMessage extends X4smMessage {

    private List<X4smAttribute> gAttributes;
    private String gProgramName;
    private String gZoneName;
    private String gStatus;
    private int gRunTime;
    private int gTimeToRun;
    private boolean gActive;

    //"empty:none:off:0:0"
    public X4smActiveProgramMessage( List<X4smAttribute> Attributes ) {
        super(MSG_TYPE_ACTIVEPROGRAM);
        gAttributes = Attributes;
        gProgramName = "Unknown";
        gZoneName = "Unknown";
        gStatus = "Unknown";
        gRunTime = 0;
        gTimeToRun = 0;
        gActive = false;
    }

     public void parse() throws X4smException  {

        for(X4smAttribute attr : gAttributes) {

            if(attr.Name.matches("status")) {
                String[] sa = attr.Value.split(":");
                gProgramName = sa[0];
                gZoneName = sa[1];
                gStatus = sa[2];
                gRunTime = Integer.parseInt(sa[3]);
                gTimeToRun = Integer.parseInt(sa[4]);

                if(!gProgramName.matches("empty")) {
                    gActive = true;
                }
            }
        }
    }

    public String getProgramName() {
        return gProgramName;
    }

    public String getZoneName() {
        return gZoneName;
    }

    public String getStatus() {
        return gStatus;
    }

    public int getRunTime() {
        return gRunTime;
    }

    public int getTimeToRun() {
        return gTimeToRun;
    }

    public boolean isActive() {
        return gActive;
    }





}
