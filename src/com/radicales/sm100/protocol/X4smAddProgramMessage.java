/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

import com.radicales.sm100.device.StartTime;

/**
 *
 * @author JanZwiegers
 */
public class X4smAddProgramMessage extends X4smMessage {

    private String gName;
    private long gControlWord;
    private int[] gWaterBudget;
    private StartTime[] gStartTimes;

    public X4smAddProgramMessage( String Name, long ControlWord, int[] WaterBudget, StartTime[] StartTimes) {
        super(MSG_TYPE_ADD_PROGRAM);
        gName = Name;
        gControlWord = ControlWord;
        gWaterBudget = WaterBudget;
        gStartTimes = StartTimes;
        encode();
    }

    private void encode() {
        String wbs = Integer.toString(gWaterBudget[0]);
        for(int i=1; i<gWaterBudget.length; i++) {
            wbs = wbs + "," + Integer.toString(gWaterBudget[i]);
        }

        String sts = gStartTimes[0].toString();
        for(int i=1; i<gStartTimes.length; i++) {
            sts = sts + "," + gStartTimes[i].toString();
        }

        String msg = "<AddProgram Name=\"" + gName + "\" ControlWord=\"0x" + Long.toHexString(gControlWord) +
                "\" WaterBudget=\"" + wbs +
                "\" StartTimes=\"" + sts + "\" />";
        setData(msg);
    }

}
