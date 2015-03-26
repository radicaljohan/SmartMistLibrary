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
public class X4smInfoMessage extends X4smMessage {

    private List<X4smAttribute> gAttributes;
    private String gName;
    private String gFamily;
    private String gRevision;
    private int gChannels;

    public X4smInfoMessage( List<X4smAttribute> Attributes ) {
        super(MSG_TYPE_INFO);
        gAttributes = Attributes;
    }

    public void parse() throws X4smException  {

        for(X4smAttribute attr : gAttributes) {

            if(attr.Name.matches("Name")) {
                gName = attr.Value;
            }
            else if(attr.Name.matches("Family")) {
                gFamily = attr.Value;
            }
            else if(attr.Name.matches("Revision")) {
                gRevision = attr.Value;
            }
            else if(attr.Name.matches("Channels")) {
                gChannels = Integer.parseInt(attr.Value);
            }
        }
    }

    public String getName() {
        return gName;
    }

    public int getChannels() {
        return gChannels;
    }

    public String getFamily() {
        return gFamily;
    }

    public String getRevision() {
        return gRevision;
    }



}
