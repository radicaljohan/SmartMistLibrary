/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

/**
 *
 * @author JanZwiegers
 */
public class X4smGetDateTimeMessage extends X4smMessage {

    public X4smGetDateTimeMessage() {
        super(X4smMessage.MSG_TYPE_GETDATETIME);
        encode();
    }

    private void encode() {
        String msg = "<GetDateTime />";
        setData(msg);
    }


}
