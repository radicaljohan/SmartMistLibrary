/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

import java.util.List;

/**
 *
 * @author JanZwiegers
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

            if(attr.Name.matches("Name")) {
                gName = attr.Value;
            }
            else if(attr.Name.matches("ControlWord")) {
                gControlWord = Long.decode(attr.Value);
            }
            else if(attr.Name.matches("InitValue")) {
                gInitValue = Integer.parseInt(attr.Value);
            }
            else if(attr.Name.matches("Channel")) {
                gChannel = Integer.parseInt(attr.Value);
            }
            else if(attr.Name.matches("OffDelay")) {
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
