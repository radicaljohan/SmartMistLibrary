/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

/**
 *
 * @author JanZwiegers
 */
public class X4smSequence {
    public int Channel;
    public int RunTime;

    public X4smSequence( int channel, int runTime ) {
        Channel = channel;
        RunTime = runTime;
    }

    public void parseSequence( String Str ) {
        String[] ts = Str.split(":");
        Channel = Integer.parseInt(ts[0]);
        RunTime = Integer.parseInt(ts[1]);
    }

    @Override
    public String toString() {
        return Integer.toString(Channel) + ":" + Integer.toString(RunTime);
    }
    
}
