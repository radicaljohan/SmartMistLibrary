/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

/**
 *
 * @author JanZwiegers
 */
public class X4smGetConfigMessage extends X4smMessage {

    public X4smGetConfigMessage() {
    super(X4smMessage.MSG_TYPE_GET_CONFIG);
        encode();
    }

    private void encode() {
        String msg = "<GetConfig />";
        setData(msg);
    }

}
