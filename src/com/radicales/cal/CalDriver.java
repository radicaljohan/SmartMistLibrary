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

package com.radicales.cal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * CAL Driver
 * Communication Abstraction Layer Driver Object.
 *
 * @author
 * Jan Zwiegers,
 * <a href="mailto:jan@radicalsystems.co.za">jan@radicalsystems.co.za</a>,
 * <a href="http://www.radicalsystems.co.za">www.radicalsystems.co.za</a>
 *
 * @version
 * <b>1.0 14/12/2013</b><br>
 * Original release.
 */
public abstract class CalDriver implements Runnable {

    public static final int DRIVER_TYPE_UNKNOWN = 0;
    public static final int DRIVER_TYPE_SERIAL = 1;
    public static final int DRIVER_TYPE_JSSC = 2;
    public static final int DRIVER_TYPE_RXTX = 3;
    public static final int DRIVER_TYPE_TCPIP = 4;

    protected InputStream drvInputStream;    //wrap into filter input
    protected OutputStream drvOutputStream;      //wrap into filter output
    protected CalDriverEvent drvEventListener;
    protected boolean drvOpen;
    protected boolean drvRunning;
    protected boolean drvThreading;
    protected Thread drvThread;
    protected int drvId;
    protected boolean drvStart;
    protected int drvType;

    public CalDriver( int Type, boolean Threading ) {
        drvEventListener = null;
        drvInputStream = null;
        drvOutputStream = null;
        drvThread = null;
        drvOpen = false;
        drvRunning = false;
        drvThreading = Threading;
        drvStart = true;
        drvId = 0;
        drvType = Type;
    }

    public CalDriver( int Type, int Id, boolean Start, boolean Threading ) {
        drvEventListener = null;
        drvInputStream = null;
        drvOutputStream = null;
        drvThread = null;
        drvOpen = false;
        drvRunning = false;
        drvThreading = Threading;
        drvStart = Start;
        drvId = Id;
        drvType = Type;
    }

    public int getType() {
        return drvType;
    }

    public boolean isOpen() {
        return drvOpen;
    }

    public boolean isRunning() {
        return drvRunning;
    }

    public void Open() throws CalDriverException {
        if(drvOpen) {
            throw new CalDriverException("Port is open");
        }
        if(drvThreading) {
            drvThread = new Thread(this);
        }
        drvOpen = true;
    }

    public void Close() throws CalDriverException {
        if(!drvOpen) {
            throw new CalDriverException("Port not open");
        }
        drvOpen = false;
    }

    public void Start() throws CalDriverException {
        if(!drvOpen) {
            throw new CalDriverException("Port not open");
        }
        if(!drvStart) {
            if(drvOpen) {
                Close();
            }
            throw new CalDriverException("Port not allowed to start");
        }
        if(drvRunning) {
            throw new CalDriverException("Driver already running");
        }
        if(drvThreading) {
            drvThread.start();
        }

        drvRunning = true;
    }

    public void Stop() throws CalDriverException {
        if(!drvRunning) {
            throw new CalDriverException("Driver not running");
        }
        drvRunning = false;
        if(drvThread != null) {
                 try {
                     drvThread.interrupt();
                     drvThread.join(2000);
                 } catch (InterruptedException ex) {
                     throw new CalDriverException(ex.getMessage());
                 }
        }


    }

    public int Write( byte[] Buffer, int Length ) throws CalDriverException {
        int ret = 0;
        try {
            drvOutputStream.write(Buffer, 0, Length);
            ret = Length;
        } catch (IOException ex) {
            throw new CalDriverException(ex.getMessage());
        }
        return ret;
    }

    public int Write( byte[] Buffer ) throws CalDriverException {
        int ret = 0;
        try {
            drvOutputStream.write(Buffer, 0, Buffer.length);
            ret = Buffer.length;
        } catch (IOException ex) {
            throw new CalDriverException("::Driver::Write:" + ex.getMessage());
        }
        return ret;
    }

    public int Read( byte[] Buffer, int Length ) throws CalDriverException {
        int ret = 0;
        try {
            ret = drvInputStream.read(Buffer, 0, Length);
        } catch (IOException ex) {
            throw new CalDriverException("::Driver::Write:" + ex.getMessage());
        }
        return ret;
    }

    public int Read( byte[] Buffer ) throws CalDriverException {
        int ret = 0;
        try {
            ret = drvInputStream.read(Buffer, 0, Buffer.length);
        } catch (IOException ex) {
            throw new CalDriverException(ex.getMessage());
        }
        return ret;
    }

    public int Read() throws CalDriverException {
        int ret = 0;
        try {
            ret = drvInputStream.read();
        } catch (IOException ex) {
            throw new CalDriverException(ex.getMessage());
        }

        return ret;
    }

    public int WriteRead( byte[] Buffer, int Length ) throws CalDriverException {
        int ret = 0;
        try {
            drvOutputStream.write(Buffer, 0, Length);
            ret = drvInputStream.read(Buffer, 0, Length);
        } catch (IOException ex) {
            throw new CalDriverException(ex.getMessage());
        }
        return ret;
    }

    public int WriteRead( byte[] Buffer ) throws CalDriverException {
        int ret = 0;
        try {
            drvOutputStream.write(Buffer, 0, Buffer.length);
            ret = drvInputStream.read(Buffer, 0, Buffer.length);
        } catch (IOException ex) {
            throw new CalDriverException(ex.getMessage());
        }
        return ret;
    }

    public void Flush() throws CalDriverException {
        try {
            if(drvOutputStream != null) {
                drvOutputStream.flush();
            }
            if(drvInputStream != null) {
                while(drvInputStream.available() > 0) {
                    drvInputStream.read();
                }
            }
        } catch (IOException ex) {
            throw new CalDriverException(ex.getMessage());
        }
    }

    public int IoControl( int Code, int Arg ) throws CalDriverException {
        int ret = 0;
        try {
            switch(Code) {
            case 10:
                ret = drvInputStream.available();
                break;
            }
        } catch (IOException ex) {
            throw new CalDriverException(ex.getMessage());
        }
        return ret;
    }

    public void addEventListener( CalDriverEvent EventListener ) throws CalDriverException {
        if(drvEventListener != null) {
            throw new CalDriverException("Too many listeners");
        }
        drvEventListener = EventListener;
    }

    public void removeEventListener( CalDriverEvent EventListener ) throws CalDriverException {
        if(drvEventListener != EventListener) {
            throw new CalDriverException("Unknown listener");
        }
        drvEventListener = null;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
