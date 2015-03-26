/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

/**
 *
 * @author JanZwiegers
 */
public class X4smGetProgramMessage extends X4smMessage {

    private String gName;

    public X4smGetProgramMessage( String Name ) {
        super(X4smMessage.MSG_TYPE_GET_PROGRAM);
        gName = Name;
        encode();
    }

    private void encode() {
        String msg = "<GetProgram Name=\"";
        msg = msg + gName + "\" />";
        setData(msg);
    }

}
