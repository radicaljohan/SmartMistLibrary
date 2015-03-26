/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

/**
 *
 * @author JanZwiegers
 */
public class X4smSetZoneMessage extends X4smMessage {

    private int gChannel;
    private int gStatus;

    public X4smSetZoneMessage( int Channel, int Status ) {
        super(X4smMessage.MSG_TYPE_SET_ZONE);
        gChannel = Channel;
        gStatus = Status;
        encode();
    }

    private String statusToString() {
        if(gStatus == 1) {
            return "ON";
        }
        else if(gStatus == 2) {
            return "TOGGLE";
        }

        return "OFF";
    }

    private void encode() {
        String msg = "<ControlZone Channel=\"";
        msg = msg + Integer.toString(gChannel) + "\" Value=\"";
        msg = msg + statusToString() + "\" />";
        setData(msg);
    }

}
