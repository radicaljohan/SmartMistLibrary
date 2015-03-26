/*
 * Copyright (C) 2012-2015 Radical Electronic Systems, South Africa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.radicales.sm100.device;

import com.radicales.sm100.protocol.X4smSequence;
import com.radicales.sm100.protocol.X4smStartTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Smart Mist 100 Watering Program Object
 *
 * @author
 * Jan Zwiegers,
 * <a href="mailto:jan@radicalsystems.co.za">jan@radicalsystems.co.za</a>,
 * <a href="http://www.radicalsystems.co.za">www.radicalsystems.co.za</a>
 *
 * @version
 * <b>1.0 01/11/2014</b><br>
 * Original release.
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
    private Sm100ProgramEvent gListener;

    public Sm100Program( String Name, Sm100ProgramEvent Listener ) {
        gName = Name;
        gListener = Listener;
        gControlWord = 0;
        for(int b : gBudget) {
            b = 100;
        }
    }

    public Sm100Program( String Name, long ControlWord, Sm100ProgramEvent Listener ) {
        gName = Name;
        gControlWord = ControlWord;
        gListener = Listener;
         for(int b : gBudget) {
            b = 100;
        }
    }

    public Sm100Program( String Name, long ControlWord, int[] WaterBudget, StartTime[] StartTimes, Sm100ProgramEvent Listener ) {
        gName = Name;
        gBudget = WaterBudget;
        gControlWord = ControlWord;
        gListener = Listener;
        gStartTimes.clear();
        gStartTimes.addAll(Arrays.asList(StartTimes));
    }

    public String getName() {
        return gName;
    }

    public int getRunTime() {
        int rt = 0;
        Sm100Zone z;
        for(Sequence s : gSequences) {
            z = s.Zone;
            rt += (s.RunTime + z.getOffDelay());
        }

        return rt;
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

    public void addStartTime( StartTime Time ) throws Sm100ProgramException {
        // check if start time already exist
        for(StartTime t : gStartTimes) {
            if(t.matches(Time)) {
                throw new Sm100ProgramException("Start time already exists");
            }
            if(t.overlaps(Time, getRunTime())) {
                throw new Sm100ProgramException("Start overlaps with " + t.toString() + " and total program runtime of " + Integer.toString(getRunTime()) + " minutes");
            }
        }
        gStartTimes.add(Time);
        gListener.eventStartListChanged(this, gStartTimes);
    }

    public void removeStartTime( StartTime Time ) throws Sm100ProgramException {
        if(!gStartTimes.remove(Time)) {
            throw new Sm100ProgramException("Start time does not exist or list is empty");
        }
        gListener.eventStartListChanged(this, gStartTimes);
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
