/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

/**
 *
 * @author JanZwiegers
 */
public class X4smPingMessage extends X4smMessage {

    public X4smPingMessage() {
        super(X4smMessage.MSG_TYPE_PING);
        encode();
    }

    private void encode() {
        String msg = "<Ping />";
        setData(msg);
    }
}
