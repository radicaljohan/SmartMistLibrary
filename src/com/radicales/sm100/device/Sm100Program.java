/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.device;

import com.radicales.sm100.protocol.X4smSequence;
import com.radicales.sm100.protocol.X4smStartTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author JanZwiegers
 */
public class Sm100Program {

    private static final long CW_NONE = 0x00000000;
    private static final long CW_AUTO = 0x00000001;
    private static final long CW_TRIGGERA = 0x00000002;
    private static final long CW_TRIGGERB = 0x00000004;
    private static final long CW_RAINSENSOR = 0x00100000;
    private static final long CW_STOPINPUT = 0x00200000;

    private String gName;
    private List<StartTime> gStartTimes = new ArrayList<>();
    private List<Sequence> gSequences = new ArrayList<>();
    private int[] gBudget = new int[12];
    private long gControlWord;
    private int gCycleDays;

    public Sm100Program( String Name ) {
        gName = Name;
        gControlWord = 0;
        for(int b : gBudget) {
            b = 100;
        }
    }

    public Sm100Program( String Name, long ControlWord ) {
        gName = Name;
        gControlWord = ControlWord;
         for(int b : gBudget) {
            b = 100;
        }
    }

    public Sm100Program( String Name, long ControlWord, int[] WaterBudget, StartTime[] StartTimes ) {
        gName = Name;
        gBudget = WaterBudget;
        gControlWord = ControlWord;
        gStartTimes.clear();
        gStartTimes.addAll(Arrays.asList(StartTimes));
    }

    public String getName() {
        return gName;
    }

    public List<StartTime> getStartTimesList() {
        return gStartTimes;
    }

    public StartTime[] getStartTimes() {
        StartTime[] sta = new StartTime[gStartTimes.size()];
        for(int i=0; i<sta.length; i++) {
            sta[i] = gStartTimes.get(i);
        }

        return sta;
    }

    public int[] getWaterBudget() {
        return gBudget;
    }

    public long getControlWord() {
        return gControlWord;
    }

    public void setStartTimes( StartTime[] Times ) {
       gStartTimes.addAll(Arrays.asList(Times));
    }

    public void setStartTimesList( List<StartTime> TimesList ) {
        gStartTimes = TimesList;
    }

    public void setWaterBudget( int[] Budget ) {
        gBudget = Budget;
    }

    public void setControlWord( long Value ) {
        gControlWord = Value;
    }

    public void addSequence( Sm100Zone Zone, int RunTime ) {
        gSequences.add(new Sequence(Zone, RunTime));
    }

    public void setSequenceList( List<Sequence> SeqList ) {
        gSequences = SeqList;
    }

    public List<Sequence> getSequenceList() {
        return gSequences;
    }

    public Sm100ProgramDays getProgramDays() {
        return new Sm100ProgramDays(gControlWord, gCycleDays);
    }

    public boolean isAuto() {
        if((gControlWord & CW_AUTO) == CW_AUTO) {
            return true;
        }

        return false;
    }

    public boolean isTriggerA() {
        if((gControlWord & CW_TRIGGERA) == CW_TRIGGERA) {
            return true;
        }

        return false;
    }

    public boolean isTriggerB() {
        if((gControlWord & CW_TRIGGERB) == CW_TRIGGERB) {
            return true;
        }

        return false;
    }

    public boolean isRainSensor() {
        if((gControlWord & CW_RAINSENSOR) == CW_RAINSENSOR) {
            return true;
        }

        return false;
    }

    public boolean isStopInput() {
        if((gControlWord & CW_STOPINPUT) == CW_STOPINPUT) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return gName;
    }
}
