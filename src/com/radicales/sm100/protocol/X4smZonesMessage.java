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
public class X4smZonesMessage extends X4smMessage {

    public static final int ZONE_MSG_TYPE_NAMES = 0;
    public static final int ZONE_MSG_TYPE_STATUS = 1;
    private final List<X4smAttribute> gAttributes;
    private int gType;
    private String[] gNames;
    private boolean[] gStatus;

    public X4smZonesMessage( List<X4smAttribute> Attributes ) {
        super(MSG_TYPE_ZONES);
        gAttributes = Attributes;
        gNames = null;
        gStatus = null;
        gType = ZONE_MSG_TYPE_NAMES;
    }

    public void parse() throws X4smException  {

        for(X4smAttribute attr : gAttributes) {

            if(attr.Name.matches("Names")) {
                gNames = attr.Value.split(",");
            }

            if(attr.Name.matches("status")) {
                String[] sa = attr.Value.split(",");
                gStatus = new boolean[sa.length];
                for(int i=0; i<sa.length; i++) {

                    gStatus[i] = sa[i].matches("on");

                }
                gType = ZONE_MSG_TYPE_STATUS;
            }

        }

    }

    public String[] getNames() {
        return gNames;
    }

    public boolean[] getStatus() {
        return gStatus;
    }

    public int getMessageType() {
        return gType;
    }

}
