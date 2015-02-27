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

package com.radicales.cal.net;

import com.radicales.cal.CalDriver;
import com.radicales.cal.CalDriverException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TCP/IP Server Driver
 * Part of Communication Abstraction Layer.
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
public class TCPServer extends CalDriver {

    private ServerSocket gServer;
    private boolean gConnected;
    private int gPort;
    private long gTTL;

    public TCPServer( int Port, long TTL ) {
        super(CalDriver.DRIVER_TYPE_TCPIP, true);
        gConnected = false;
        gPort = Port;
        gTTL = TTL;
    }

     // exported functions
    @Override
    synchronized public void Open() throws CalDriverException {
        super.Open();
        try {
            gServer = new ServerSocket(gPort);
        } catch (IOException ex) {
            throw new CalDriverException("Server socket failed");
        }
    }

    @Override
    synchronized public void Close() throws CalDriverException {
        super.Close();
    }

    @Override
    synchronized public void Start() throws CalDriverException {
        super.Start();
    }

    @Override
    synchronized public void Stop() throws CalDriverException {
        super.Stop();
    }

   @Override
   synchronized public int WriteRead( byte[] Buffer, int Length ) throws CalDriverException {
       int ret;
       ret = super.WriteRead(Buffer, Length);
       return ret;
   }

    @Override
    public void run() {
        Socket client;
        byte[] buffer = new byte[1000];
        int ret;

        System.out.println("TCP Server Starting");

        while(super.drvRunning) {
            try {
                this.gConnected = false;
                client = this.gServer.accept();
                drvInputStream = client.getInputStream();
                drvOutputStream = client.getOutputStream();
                this.gConnected = true;
            } catch (IOException ex) {
                continue;
            }

            while((this.gConnected) && (client != null)) {
                try {
                    ret = Read(buffer);
                    if(ret > 0) {
                        byte[] b = new byte[ret];
                        System.arraycopy(buffer, 0, b, 0, ret);
                        System.out.print("TCP DATA: " + new String(b));
                        this.gConnected = false;
                    }
                } catch (CalDriverException ex) {
                    client = null;
                    this.gConnected = false;
                }
            }
            try {
                drvInputStream.close();
                drvOutputStream.close();
                client.close();
            } catch (IOException ex) {
            }
        }

        System.out.println("TCP Server Stopping");

    }

}
