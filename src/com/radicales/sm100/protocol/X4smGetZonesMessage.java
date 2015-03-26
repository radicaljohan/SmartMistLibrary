/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

/**
 *
 * @author JanZwiegers
 */
public class X4smGetZonesMessage extends X4smMessage {

    public X4smGetZonesMessage() {
        super(X4smMessage.MSG_TYPE_GETZONES);
        encode();
    }

    private void encode() {
        String msg = "<GetZones />";
        setData(msg);
    }

}
