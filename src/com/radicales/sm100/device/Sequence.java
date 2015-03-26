/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.device;

/**
 *
 * @author JanZwiegers
 */
public class Sequence {

    public Sm100Zone Zone;
    public int RunTime;

    public Sequence( Sm100Zone zone, int runtime ) {
        Zone = zone;
        RunTime = runtime;
    }

    @Override
    public String toString() {
        if(Zone != null) {
            return Zone.getName() + " [" + Integer.toString(RunTime) + "]";
        }

        return "Unknown Zone [" + Integer.toString(RunTime) + "]";
    }

}
