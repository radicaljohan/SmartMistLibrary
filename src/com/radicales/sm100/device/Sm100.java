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

import com.radicales.cal.CalDriverException;
import com.radicales.sm100.protocol.*;
import java.util.*;

/**
 * Smart Mist 100 Device Object
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
public class Sm100 implements Runnable, X4smEvent, Sm100ProgramEvent {

    private static final int STATE_INIT = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_GETINFO = 3;
    private static final int STATE_PING = 4;
    private static final int STATE_PING_WAIT = 5;

    private static final int STATE_GETCONFIG = 20;
    private static final int STATE_GETPROGRAM = 21;
    private static final int STATE_SETPROGRAM = 22;
    private static final int STATE_GETZONE = 23;
    private static final int STATE_SETZONE = 24;
    private static final int STATE_GET_PROGRAMLIST = 25;
    private static final int STATE_PROGRAM_WAIT = 26;
    private static final int STATE_GET_PROGRAM_DONE = 27;
    private static final int STATE_ZONE_WAIT = 28;
    private static final int STATE_GET_ZONELIST = 29;
    private static final int STATE_MONITOR_WAIT = 30;
    private static final int STATE_MONITOR_IDLE = 31;
    private static final int STATE_REMOVE_SETUP_WAIT = 40;
    private static final int STATE_ADDZONE = 41;
    private static final int STATE_ADDZONE_WAIT = 42;
    private static final int STATE_ADDPROG = 43;
    private static final int STATE_ADDPROG_WAIT = 44;
    private static final int STATE_ADDSEQ = 45;
    private static final int STATE_ADDSEQ_WAIT = 46;
    private static final int STATE_DOWNLOAD_WAIT = 47;

    private static final int STATE_CLOSE = 50;
    private static final int STATE_IDLE = 60;

    private static final int CMD_UPLOAD = 10;
    private static final int CMD_DOWNLOAD = 11;
    private static final int CMD_START_MONITOR = 13;
    private static final int CMD_STOP_MONITOR = 14;

    private String gName;
    private String gDescription;
    private String gLocation;
    private String gMode;
    private boolean gEnabled;
    private final X4smComm gComm;
    private boolean gOnline;
    private int gState;
    private int gIndex;
    private Thread gThread;
    private volatile boolean gRunning;
    private int gTimer;
    private int gChannels;
    private Queue<X4smMessage> gTxQueue = new LinkedList<>();
    private Queue<Integer> gCmdQueue = new LinkedList<>();
    private List<Sm100Event> gEventListeners = new ArrayList<>();
    private List<Sm100Program> gPrograms = new ArrayList<>();
    private List<Sm100Zone> gZones = new ArrayList<>();

    /**
     * Constructor of a SM100 device object
     *
     * @param Name Text name of the device
     * @param IpAddress Text based IP address of the device
     * @param Port Port that the device must be connected to
     * @param Enabled If the device is enabled
     */

    public Sm100( String Name, String IpAddress, int Port, boolean Enabled ) {
        gName = Name;
        gEnabled = Enabled;
        gComm = new X4smComm(IpAddress, Port);
        gState = STATE_INIT;
        gOnline = false;
        gChannels = -1;
        gDescription = "Unknown";
        gLocation = "Unknown";
    }

     public void registerEventListener( Sm100Event Listener ) {
        gEventListeners.add(Listener);
    }

    public void removeEventListener( Sm100Event Listener ) {
        gEventListeners.remove(Listener);
    }

    public String getIpAddress() {
        return gComm.getIpAddress();
    }

    public void setIpAddress( String Value ) {
        gComm.setIpAddress(Value);
    }

    public String getDescription() {
        return gDescription;
    }

    public String getName() {
        return gName;
    }

    public boolean getEnabled() {
        return gEnabled;
    }

    public String getMode() {
        return gMode;
    }

    public void setMode( String Value ) {
        gMode = Value;
    }

    public void setDescription( String Value ) {
        gDescription = Value;
    }

    public String getLocation() {
        return gLocation;
    }

    public void setLocation( String Value ) {
        this.gLocation = Value;
    }

    public int getPort() {
        return gComm.getPort();
    }

    public Sm100Program findProgram( String Name ) {

        for(Sm100Program p : gPrograms) {
            if(p.getName().matches(Name)) {
                return p;
            }
        }

        return null;
    }

    public Sm100Zone findZone( String Name ) {

        for(Sm100Zone z : gZones) {
            if(z.getName().matches(Name)) {
                return z;
            }
        }

        return null;
    }

    public void removeZone( Sm100Zone zone ) {

        if(gZones.remove(zone)) {
            for(Sm100Event ev : gEventListeners) {
                ev.eventZonesUpdate(gZones);
            }
        }
    }

    public void addNextZone() throws Sm100Exception {

        if(gChannels < 0) {
            throw new Sm100Exception("Device has no channels");
        }

        List<Integer> chlst = new ArrayList<>();
        for(int c=0; c<gChannels; c++) {
            chlst.add(c + 1);
        }

        for(Sm100Zone z : gZones) {
            int c = z.getChannel();

            for(Integer i : chlst) {
                if(c == i.intValue()) {
                    chlst.remove(i);
                    break;
                }
            }
        }

        if(chlst.isEmpty()) {
            throw new Sm100Exception("No open channels");
        }

        Integer c = chlst.get(0);
        Sm100Zone z = new Sm100Zone("Zone " + Integer.toString(c));
        z.setChannel(c);
        gZones.add(z);
        for(Sm100Event ev : gEventListeners) {
            ev.eventZonesUpdate(gZones);
        }
    }

    public Sm100Zone findZoneByChannel( int Channel ) {

        for(Sm100Zone z : gZones) {
            if(z.getIndex() == Channel) {
                return z;
            }
        }

        return null;
    }

    public void addZone(String Name, int Channel, long ControlWord, boolean InitValue, int OffDelay, boolean Enabled) {
        Sm100Zone z = new Sm100Zone(Name, Channel, ControlWord, InitValue, OffDelay, Enabled);
        gZones.add(z);
    }

    public void addProgram(String Name, long ControlWord, int[] WaterBudget, StartTime[] StartTimes ) {
        Sm100Program p = new Sm100Program(Name, ControlWord, WaterBudget, StartTimes, this);
        gPrograms.add(p);
    }

    public void addProgramObject( Sm100Program Program ) {
        gPrograms.add(Program);
    }

    public synchronized boolean start() {
        if(gRunning) {
            return false;
        }
        for(Sm100Event ev : gEventListeners) {
            ev.eventStatus("Connecting");
        }
        gComm.registerEventListener(this);
        gRunning = true;
        gThread = new Thread(this);
        gThread.start();

        return true;
    }

    public boolean setDateTime( Date DateTime ) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateTime);
        X4smSetDateTimeMessage msg =
                    new X4smSetDateTimeMessage(
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.DAY_OF_WEEK) - 1,
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        cal.get(Calendar.SECOND));

        return gTxQueue.add(msg);
    }

    public boolean getDateTime() {
        X4smGetDateTimeMessage msg = new X4smGetDateTimeMessage();
        return gTxQueue.add(msg);
    }

    public boolean getProgramNames() {
        X4smGetProgramsMessage msg = new X4smGetProgramsMessage();
        return gTxQueue.add(msg);
    }

    public Sm100Program getProgram( String Name ) {
        for(Sm100Program p : gPrograms) {
            if(p.getName().matches(Name)) {
                return p;
            }
        }
        return null;
    }

    public List<Sm100Program> getProgramsList() {
        return gPrograms;
    }

    public List<Sm100Zone> getZoneList() {
        return gZones;
    }

    public boolean startMonitor() {
        return gCmdQueue.add(CMD_START_MONITOR);
    }

    public boolean stopMonitor() {
        return gCmdQueue.add(CMD_STOP_MONITOR);
    }

    public boolean startProgram( String Name ) {
        X4smStartProgramMessage msg = new X4smStartProgramMessage(Name);
        return gTxQueue.add(msg);
    }

    public boolean stopProgram( String Name ) {
        X4smStopProgramMessage msg = new X4smStopProgramMessage(Name);
        return gTxQueue.add(msg);
    }

    public Sm100Zone getZone( String Name ) {
        for(Sm100Zone z : gZones) {
            if(z.getName().matches(Name)) {
                return z;
            }
        }

        return null;
    }

    public boolean getZoneNames() {
         X4smGetZonesMessage msg = new X4smGetZonesMessage();
        return gTxQueue.add(msg);
    }

    public boolean syncDownloadPrograms() {
        return gCmdQueue.add(CMD_DOWNLOAD);
    }

    public boolean syncUploadPrograms() {
        return gCmdQueue.add(CMD_UPLOAD);
    }

    public synchronized void stop() {
        if(!gRunning) {
            return;
        }

        for(Sm100Event ev : gEventListeners) {
            ev.eventStatus("Disconnecting");
        }

        gComm.removeEventListener(this);
        this.gRunning = false;
        try {
            this.gThread.interrupt();
            this.gThread.join(5000);
        } catch (InterruptedException ex) {
        }
    }

    private synchronized boolean setState( int NewState ) {

        boolean ret = false;

        if(NewState > 100) {
            System.out.println("Sm100 SM: Old State = " + Integer.toString(this.gState) + " New event state = " + Integer.toString(NewState));
            NewState -= 100;
            ret = true;
        }
        else if(this.gState  != NewState) {
            System.out.println("Sm100 SM: Old State = " + Integer.toString(this.gState) + " New state = " + Integer.toString(NewState));
            ret = true;
        }

        this.gState = NewState;

        return ret;
    }

    private synchronized int getState() {
        return this.gState;
    }

    private int processCommand( int State, int Cmd ) {
        try {
            if(Cmd == CMD_UPLOAD) {
                X4smRemoveSetupMessage msg = new X4smRemoveSetupMessage();
                this.gComm.sendMessage(msg);
                this.gIndex = 0;
                return STATE_REMOVE_SETUP_WAIT;
            }
            else if(Cmd == CMD_DOWNLOAD) {
                X4smGetZonesMessage msg = new X4smGetZonesMessage();
                this.gComm.sendMessage(msg);
                return STATE_GET_ZONELIST;
            }
            else if(Cmd == CMD_START_MONITOR) {
                X4smGetStatusMessage msg = new X4smGetStatusMessage();
                this.gComm.sendMessage(msg);
                return STATE_MONITOR_WAIT;
            }
            else if(Cmd == CMD_STOP_MONITOR) {
                return STATE_CONNECTED;
            }
        }
        catch(CalDriverException e) {
            return STATE_CLOSE;
        }

        return State;
    }


    @Override
    public void run() {

        System.out.println("SM100 SM Starting...");
        while(this.gRunning) {

            int state = getState();
            synchronized(this) {

                try {
                    if(state == STATE_INIT) {
                        this.gTimer = 0;
                        if(gComm.Start()) {
                            state = STATE_CONNECTING;
                        }
                    }
                    else if(state == STATE_CONNECTING) {
                        if(gComm.getInformation()) {
                            state = STATE_GETINFO;
                            this.gTimer = 0;
                        }
                    }
                    else if(state == STATE_GETINFO) {
                        if(this.gTimer > 2000) {
                            state = STATE_CLOSE;
                        }
                    }
                    else if(state == STATE_PING) {
                        if(gComm.ping()) {
                            state = STATE_PING_WAIT;
                        }
                        else {
                            state = STATE_CLOSE;
                        }
                        this.gTimer = 0;
                    }
                    else if(state == STATE_PING_WAIT) {
                        if(this.gTimer > 2000) {
                            state = STATE_CLOSE;
                        }
                    }
                    else if(state == STATE_CLOSE) {
                        gComm.Stop();
                        state = STATE_INIT;
                        this.gOnline = false;
                        this.gTimer = 0;
                        for(Sm100Event ev : gEventListeners) {
                            ev.eventStatus("Offline");
                        }
                    }
                    else if(state == STATE_CONNECTED ) {

                        if(!this.gCmdQueue.isEmpty()) {
                            state = processCommand(state, this.gCmdQueue.remove());
                            this.gTimer = 0;
                        }
                        else if(!this.gTxQueue.isEmpty()) {
                            this.gComm.sendMessage(this.gTxQueue.remove());
                            this.gTimer = 0;
                        }
                        else if(this.gTimer > 5000) {
                            state = STATE_PING;
                        }
                    }
                    else if(state == STATE_MONITOR_WAIT) {
                        if(this.gTimer > 5000) {
                            state = STATE_CLOSE;
                        }
                    }
                    else if(state == STATE_MONITOR_IDLE) {
                        if(!this.gCmdQueue.isEmpty()) {
                            state = processCommand(state, this.gCmdQueue.remove());
                            this.gTimer = 0;
                        }
                        if(this.gTimer > 2000) {
                            X4smGetStatusMessage msg = new X4smGetStatusMessage();
                            this.gComm.sendMessage(msg);
                            state = STATE_MONITOR_WAIT;
                        }
                    }
                    else if(state == STATE_GET_ZONELIST) {
                        if(this.gTimer > 2000) {
                            state = STATE_PING;
                        }
                    }
                    else if(state == STATE_GETZONE) {
                        if(this.gIndex < gZones.size()) {
                            X4smGetZoneMessage msg = new X4smGetZoneMessage(gZones.get(this.gIndex).getName());
                            this.gComm.sendMessage(msg);
                            state = STATE_ZONE_WAIT;
                        }
                        else {
                            for(Sm100Event ev : gEventListeners) {
                                ev.eventZonesUpdate(gZones);
                            }
                            X4smGetProgramsMessage msg = new X4smGetProgramsMessage();
                            this.gComm.sendMessage(msg);
                            state = STATE_GET_PROGRAMLIST;
                        }
                        this.gTimer = 0;
                    }
                    else if(state == STATE_PROGRAM_WAIT) {
                        if(this.gTimer > 2000) {
                            state = STATE_PING;
                        }
                    }
                    else if(state == STATE_GET_PROGRAMLIST) {
                        if(this.gTimer > 2000) {
                            state = STATE_PING;
                        }
                    }
                    else if(state == STATE_GETPROGRAM) {
                        if(this.gIndex < gPrograms.size()) {
                            X4smGetProgramMessage msg = new X4smGetProgramMessage(gPrograms.get(this.gIndex).getName());
                            this.gComm.sendMessage(msg);
                            state = STATE_PROGRAM_WAIT;
                        }
                        else {
                            state = STATE_GET_PROGRAM_DONE;
                        }
                        this.gTimer = 0;
                    }
                    else if(state == STATE_PROGRAM_WAIT) {
                        if(this.gTimer > 2000) {
                            state = STATE_PING;
                        }
                    }
                    else if(state == STATE_GET_PROGRAM_DONE) {
                        for(Sm100Event ev : gEventListeners) {
                             ev.eventProgramsUpdate(gPrograms);
                        }
                        state = STATE_CONNECTED;
                    }
                    else if(state == STATE_REMOVE_SETUP_WAIT) {
                        if(this.gTimer > 5000) {
                            state = STATE_CLOSE;
                        }
                    }
                    else if(state == STATE_ADDZONE) {
                        if(gIndex < gZones.size()) {
                            Sm100Zone z = gZones.get(gIndex++);
                            X4smAddZoneMessage msg = new X4smAddZoneMessage(z.getName(), z.getChannel(), z.getControlWord(), z.isInitValue(), z.getOffDelay());
                            this.gComm.sendMessage(msg);
                            state = STATE_ADDZONE_WAIT;
                        }
                        else {
                            this.gIndex = 0;
                            state = STATE_ADDPROG;
                        }
                        this.gTimer = 0;
                    }
                    else if(state == STATE_ADDZONE_WAIT) {
                        if(this.gTimer > 5000) {
                            state = STATE_CLOSE;
                        }
                    }
                    else if(state == STATE_ADDPROG) {
                        if(gIndex < gPrograms.size()) {
                            Sm100Program p = gPrograms.get(gIndex++);
                            X4smAddProgramMessage msg = new X4smAddProgramMessage(p.getName(), p.getControlWord(), p.getWaterBudget(), p.getStartTimes());
                            this.gComm.sendMessage(msg);
                            state = STATE_ADDPROG_WAIT;
                        }
                        else {
                            this.gIndex = 0;
                            state = STATE_ADDSEQ;
                        }
                        this.gTimer = 0;
                    }
                    else if(state == STATE_ADDPROG_WAIT) {
                        if(this.gTimer > 5000) {
                            state = STATE_CLOSE;
                        }
                    }
                    else if(state == STATE_ADDSEQ) {
                        if(gIndex < gPrograms.size()) {
                            Sm100Program p = gPrograms.get(gIndex++);
                            //X4smAddProgramMessage msg = new X4smAddProgramMessage(p.getName(), p.getControlWord(), p.getWaterBudget(), p.getStartTimes());
                            //this.gComm.sendMessage(msg);
                            state = STATE_ADDSEQ_WAIT;
                        }
                        else {
                            this.gIndex = 0;
                            state = STATE_DOWNLOAD_WAIT;
                        }
                        this.gTimer = 0;
                    }
                    else if(state == STATE_ADDSEQ_WAIT) {
                        if(this.gTimer > 5000) {
                            state = STATE_CLOSE;
                        }
                    }
                    else if(state == STATE_DOWNLOAD_WAIT) {
                        if(this.gTimer > 5000) {
                            state = STATE_CLOSE;
                        }
                    }
                } catch (CalDriverException ex) {
                    state = STATE_CLOSE;
                }

            } // synchronized

            if(!setState(state)) {
                try {
                    Thread.sleep(100);
                    this.gTimer += 100;
                } catch (InterruptedException ex) {
                    this.gRunning = false;
                }
            }

        }

        System.out.println("SM100 SM Stoppping...");

        gComm.Stop();
        setState(STATE_INIT);
        this.gOnline = false;
        for(Sm100Event ev : gEventListeners) {
            ev.eventStatus("Disconnected");
        }

        System.out.println("SM100 SM Stopped");
    }

    @Override
    public void eventZone(int Channel, int Status) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void eventFirmware(String Version) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void eventConfig(String FileName, int Zones, int Programs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void eventAck() {
        if(getState() == STATE_PING_WAIT) {
            setState(STATE_CONNECTED + 100);
            this.gOnline = true;
            for(Sm100Event ev : gEventListeners) {
                ev.eventStatus("Online");
            }
        }
        this.gTimer = 0;
    }

    @Override
    public void eventDateTime(Date Time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(Time);
        for(Sm100Event ev : gEventListeners) {
            ev.eventDataTime(Time);
        }
    }

    @Override
    public void eventPrograms(String[] Names) {
       gPrograms.clear();
       for(String n : Names) {
           Sm100Program p = new Sm100Program(n, this);
           gPrograms.add(p);
       }

       if(getState() == STATE_GET_PROGRAMLIST) {
           setState(STATE_GETPROGRAM + 100);
           this.gIndex = 0;
       }
    }

    @Override
    public void eventProgram(String Name, long ControlWord, int[] WaterBudget, X4smStartTime[] StartTimes, X4smSequence[] Sequences) {
        Sm100Program p = findProgram(Name);
        if(p != null) {
            p.setControlWord(ControlWord);
            p.setWaterBudget(WaterBudget);

            StartTime[] st = new StartTime[StartTimes.length];
            for(int i=0; i<StartTimes.length; i++) {
                st[i] = new StartTime(StartTimes[i].Hour, StartTimes[i].Minute);
            }
            p.setStartTimes(st);

            for(X4smSequence sq : Sequences) {
                Sm100Zone z = findZoneByChannel(sq.Channel);
                p.addSequence(z, sq.RunTime);
            }
        }
        if(getState() == STATE_PROGRAM_WAIT) {
           setState(STATE_GETPROGRAM + 100);
           this.gIndex++;
       }
    }

    @Override
    public void eventZones(String[] Names) {
       gZones.clear();
       for(String n : Names) {
           Sm100Zone z = new Sm100Zone(n);
           gZones.add(z);
       }

       if(getState() == STATE_GET_ZONELIST) {
           setState(STATE_GETZONE + 100);
           this.gIndex = 0;
       }


    }

    @Override
    public void eventZone(String Name, int Channel, long ControlWord, int InitValue, int OffDelay) {
        Sm100Zone z = findZone(Name);

        if(z != null) {
            z.setChannel(Channel);
            z.setControlWord(ControlWord);
            z.setInitValue(InitValue);
            z.setOffDelay(OffDelay);
        }

        if(getState() == STATE_ZONE_WAIT) {
            setState(STATE_GETZONE + 100);
            this.gIndex++;
        }
    }

    @Override
    public void eventInformation(String Name, String Family, String Revision, int Channels) {

        this.gChannels = Channels;

        if(getState() == STATE_GETINFO) {
            setState(STATE_PING + 100);

            for(Sm100Event ev : gEventListeners) {
                ev.eventInformation(Name, Family, Revision, Channels);
            }
        }
    }

    @Override
    public String toString() {
        return gName;
    }

    @Override
    public void eventZonesStatus(String[] Names, boolean[] Status) {

        for(int i=0; i<Names.length; i++) {
            String n = Names[i];
            for(Sm100Zone s : gZones) {
                if(s.getName().matches(n)) {
                    s.setStatus(Status[i]);
                }
            }
        }

        if(getState() == STATE_MONITOR_WAIT) {

            for(Sm100Event ev : gEventListeners) {
                ev.eventZoneStatusUpdate(gZones);
            }

            setState(STATE_MONITOR_IDLE + 100);
        }
    }

    @Override
    public void eventActiveProgram(boolean Active, String Name, String Zone, String Status, int RunTime, int TimeToRun) {
        Sm100Program prog = null;
        Sm100Zone zone = null;

        if(Active) {
            prog = findProgram(Name);
            zone = findZone(Zone);
        }

        for(Sm100Event ev : gEventListeners) {
            ev.eventActiveProgram(Active, prog, zone, Status, RunTime, TimeToRun);
        }
    }

    @Override
    public void eventInputs(boolean[] Status) {
        for(Sm100Event ev : gEventListeners) {
            ev.eventInputs(Status);
        }
    }

    @Override
    public void eventReady() {
        if(getState() == STATE_REMOVE_SETUP_WAIT) {
            setState(STATE_ADDZONE + 100);
        }
        else if(getState() == STATE_ADDZONE_WAIT) {
            setState(STATE_ADDZONE + 100);
        }
        else if(getState() == STATE_ADDPROG_WAIT) {
            setState(STATE_ADDPROG + 100);
        }
        else if(getState() == STATE_ADDSEQ_WAIT) {
            setState(STATE_ADDSEQ + 100);
        }
        else if(getState() == STATE_DOWNLOAD_WAIT) {
            for(Sm100Event ev : gEventListeners) {
                ev.eventUploadComplete();
            }
            setState(STATE_IDLE + 100);
        }
    }

    @Override
    public void eventStartListChanged(Sm100Program Program, List<StartTime> StartTimeList) {

         System.out.println("eventStartListChanged");

        for(Sm100Event ev : gEventListeners) {
                ev.eventProgramStartTimesUpdate(Program, StartTimeList);
            }
    }

}
