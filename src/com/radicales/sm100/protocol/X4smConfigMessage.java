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
public class X4smConfigMessage extends X4smMessage {

    private List<X4smAttribute> gAttributes;
    private String gFilename;
    private int gZones;
    private int gPrograms;

    public X4smConfigMessage( List<X4smAttribute> Attributes ) {
        super(MSG_TYPE_CONFIG);
        gAttributes = Attributes;
        gFilename = "Uknown.csv";
        gZones = -1;
        gPrograms = -1;
    }

    public void parse() throws X4smException  {

        for(X4smAttribute attr : gAttributes) {
            if(attr.Name.matches("FileName")) {
                gFilename = attr.Value;
            }
            else if(attr.Name.matches("Zones")) {
                gZones = Integer.parseInt(attr.Value);
            }
            else if(attr.Name.matches("Programs")) {
                gPrograms = Integer.parseInt(attr.Value);
            }
        }
    }

    public String getFilename() {
        return gFilename;
    }

    public int getPrograms() {
        return gPrograms;
    }

    public int getZones() {
        return gZones;
    }




}
