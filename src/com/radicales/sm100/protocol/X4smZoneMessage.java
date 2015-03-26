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
 * XML For Smart Mist Zone Message
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
public class X4smZoneMessage extends X4smMessage {

    private List<X4smAttribute> gAttributes;
    private String gName;
    private long gControlWord;
    private int gInitValue;
    private int gChannel;
    private int gOffDelay;

    public X4smZoneMessage( List<X4smAttribute> Attributes ) {
        super(MSG_TYPE_ZONE);
        gAttributes = Attributes;
    }

    public void parse() throws X4smException  {

        for(X4smAttribute attr : gAttributes) {

            if(attr.Name.matches("name")) {
                gName = attr.Value;
            }
            else if(attr.Name.matches("wontrolword")) {
                gControlWord = Long.decode(attr.Value);
            }
            else if(attr.Name.matches("initvalue")) {
                gInitValue = Integer.parseInt(attr.Value);
            }
            else if(attr.Name.matches("channel")) {
                gChannel = Integer.parseInt(attr.Value);
            }
            else if(attr.Name.matches("offdelay")) {
                gOffDelay = Integer.parseInt(attr.Value);
            }
        }
    }

    public String getName() {
        return gName;
    }

    public long getControlWord() {
        return gControlWord;
    }

    public int getChannel() {
        return gChannel;
    }

    public int getInitValue() {
        return gInitValue;
    }

    public int getOffDelay() {
        return gOffDelay;
    }

}
