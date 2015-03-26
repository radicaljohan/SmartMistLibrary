/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

/**
 *
 * @author JanZwiegers
 */
public class X4smGetInfoMessage extends X4smMessage {

    public X4smGetInfoMessage() {
        super(X4smMessage.MSG_TYPE_GET_INFO);
        encode();
    }

    private void encode() {
        String msg = "<GetInfo />";
        setData(msg);
    }

}
