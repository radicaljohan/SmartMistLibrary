/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

/**
 *
 * @author JanZwiegers
 */
public class X4smGetProgramsMessage extends X4smMessage {

    public X4smGetProgramsMessage() {
        super(X4smMessage.MSG_TYPE_GET_PROGRAM);
        encode();
    }

    private void encode() {
        String msg = "<GetPrograms />";
        setData(msg);
    }


}
