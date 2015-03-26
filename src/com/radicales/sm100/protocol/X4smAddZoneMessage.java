/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

/**
 *
 * @author JanZwiegers
 */
public class X4smAddZoneMessage extends X4smMessage {

    private String gName;
    private int gChannel;
    private long gControlWord;
    private boolean gInitValue;
    private int gOffDelay;

    public X4smAddZoneMessage( String Name, int Channel, long ControlWord, boolean InitValue, int OffDelay ) {
        super(MSG_TYPE_ADD_ZONE);
        gName = Name;
        gChannel = Channel;
        gControlWord = ControlWord;
        gInitValue = InitValue;
        gOffDelay = OffDelay;
        encode();
    }

    private void encode() {
        String msg = "<AddZone Name=\"" + gName + "\" Channel=\"" + Integer.toString(gChannel) +
                "\" ControlWord=\"0x" + Long.toHexString(gControlWord) + "\" InitValue=\"" + Boolean.toString(gInitValue) +
                "\" OffDelay=\"" + Integer.toString(gOffDelay) + "\" />";
        setData(msg);
    }

}
