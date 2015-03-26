/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

/**
 *
 * @author JanZwiegers
 */
public class X4smStopProgramMessage extends X4smMessage {

    public String gName;

    public X4smStopProgramMessage( String Name ) {
        super(X4smMessage.MSG_TYPE_STOP_PROGRAM);
        gName = Name;
        encode();
    }

    private void encode() {
        String msg = "<StopProgram Name=\"" + gName + "\" />";
        setData(msg);
    }

}
