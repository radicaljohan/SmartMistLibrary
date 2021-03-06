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
package com.radicales.sm100.protocol;

import com.radicales.cal.CalDriver;
import com.radicales.cal.CalDriverEvent;
import com.radicales.cal.CalDriverException;
import com.radicales.cal.net.TCPClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

/**
 * XML For Smart Mist Main Communications Object
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
public class X4smComm implements CalDriverEvent, Runnable {

    private String gName;
    private String gIpAddress;
    private int gPort;
    private TCPClient gDriver;
    private Thread gThread;
    private boolean gRunning;
    private boolean gDecode;
    private byte[] gBuffer;
    private List<X4smEvent> gEventListeners;

    public X4smComm( String IpAddress, int Port ) {
        gName = "X4smComm";
        gBuffer = new byte[1001];
        gIpAddress = IpAddress;
        gPort = Port;
        gDriver = new TCPClient(IpAddress, Port);
        gEventListeners = new ArrayList();
    }

    public void setIpAddress( String Value ) {
        gIpAddress = Value;
        gDriver.setIpAddress(Value);
    }

    public boolean Start() {
        if(gRunning) {
          return false;
       }

        try {
            gDriver.Open();
            gDriver.Start();
            gDriver.addEventListener(this);
        } catch (CalDriverException ex) {
            if(gDriver.isOpen()) {
              try {
                  gDriver.Close();
              } catch (CalDriverException exx) {
              }
            }
            return false;
        }

        gThread = new Thread(this);
        gThread.setName(gName + "::Process");
        gRunning = true;
        gThread.start();
        return true;
    }

    public void Stop() {

        if(!gRunning) {
          return;
       }

        gRunning = false;
        gThread.interrupt();
        try {
            gThread.join(2000);
        } catch (InterruptedException iex) {
        }

        try {
            gDriver.removeEventListener(this);
            gDriver.Stop();
            gDriver.Close();
        } catch (CalDriverException ex) {
        }
    }

    public void registerEventListener( X4smEvent Listener ) {
        gEventListeners.add(Listener);
    }

    public void removeEventListener( X4smEvent Listener ) {
        gEventListeners.remove(Listener);
    }

    public String getIpAddress() {
        return gIpAddress;
    }

    public int getPort() {
        return gPort;
    }

     public boolean ping() {
        try {
            X4smPingMessage msg = new X4smPingMessage();
            sendMessage(msg);
        } catch (CalDriverException ex) {
            return false;
        }

        return true;
    }

    public boolean setZone( int Channel, int Status ) {
        try {
            X4smSetZoneMessage msg = new X4smSetZoneMessage(Channel, Status);
            sendMessage(msg);
        } catch (CalDriverException ex) {
            return false;
        }

        return true;
    }

    public boolean setDateTime( Date DateTime ) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateTime);
        try {
            X4smSetDateTimeMessage msg =
                    new X4smSetDateTimeMessage(
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.DAY_OF_WEEK),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.HOUR),
                        cal.get(Calendar.MINUTE),
                        cal.get(Calendar.SECOND));
            sendMessage(msg);
        } catch (CalDriverException ex) {
            return false;
        }

        return true;
    }

    public boolean getProgram( String Name ) {
        try {
            X4smGetProgramMessage msg = new X4smGetProgramMessage(Name);
            sendMessage(msg);
        } catch (CalDriverException ex) {
            return false;
        }

        return true;
    }

     public boolean getConfig() {
        try {
            X4smGetConfigMessage msg = new X4smGetConfigMessage();
            sendMessage(msg);
        } catch (CalDriverException ex) {
            return false;
        }

        return true;
    }

    public boolean getInformation() {
        try {
            X4smGetInfoMessage msg = new X4smGetInfoMessage();
            sendMessage(msg);
        } catch (CalDriverException ex) {
            return false;
        }

        return true;
    }

    @Override
    public void DriverEventRead(byte[] Buffer, int Length) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void DriverEventWrite(byte[] Buffer, int Lenght) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void DriverEventRun() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void DriverEventData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void DriverEventNotify(int Id, int Value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public synchronized void sendMessage( X4smMessage Message ) throws CalDriverException {
        byte[] buf = Message.getData().getBytes();
        this.gDriver.Write(buf);
    }

    /**
     * Process incoming characters.
     * Will look for # to start the packet and \r\n to end the packet.
     * Packet will then be forwarded to the packet parser.
     */
    private void Process( byte[] Buffer, int Length ) {
        int i = 0;
        byte[] b = new byte[Length];
        System.arraycopy(Buffer, 0, b, 0, Length);
        String st = new String(b);
        String[] stp = st.split("\r\n");

        for(String s : stp) {
            System.out.println("X4SM PROCESS[" + s + "]");
            X4smMessage rbd = new X4smMessage(s);
            X4smMessage pm;
            try {
                pm = rbd.parseData();
                if(pm != null) {
                    pm.fireEvent(gEventListeners);
                }
            } catch (X4smException ex) {
                System.out.println("Message parse error: " + ex.getMessage());
            }
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1000];
        int len;

        System.out.println(gName + "::Process - Starting");

        while(this.gRunning) {
            try {
                len = this.gDriver.Read(buffer);
                if(len > 0) {
                    this.Process(buffer, len);
                }
            } catch (CalDriverException ex) {
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }

        System.out.println(gName + "::Process - Stopping");
    }


}
