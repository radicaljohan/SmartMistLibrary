/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

import java.util.List;

/**
 *
 * @author JanZwiegers
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

            if(attr.Name.matches("Status")) {
                String[] sa = attr.Value.split(":");
                gProgramName = sa[0];
                gZoneName = sa[1];
                gStatus = sa[2];
                gRunTime = Integer.parseInt(sa[3]);
                gTimeToRun = Integer.parseInt(sa[4]);

                if(!gProgramName.matches("Empty")) {
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
