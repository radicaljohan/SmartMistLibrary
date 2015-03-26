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
import java.net.*;

/**
 * TCP/IP Client Driver
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
public class TCPClient extends CalDriver {

    private String gIpAddress;
    private int gPort;
    private int gTTL;
    private Socket gSocket = null;

    public TCPClient( String IpAddress, int Port ) {
        super(CalDriver.DRIVER_TYPE_TCPIP, true);
        gSocket = null;
        gIpAddress = IpAddress;
        gPort = Port;
        gTTL = 5000;
    }

    public void setIpAddress( String Value ) {
        gIpAddress = Value;
    }

    public void setPort( int Value ) {
        gPort = Value;
    }

    // local functions
    private void Connect() throws CalDriverException {
        InetAddress iaddr;
        InetSocketAddress sockaddr;

        System.out.println("TCPClient connect...");

        try {
            iaddr = InetAddress.getByName(gIpAddress);
            sockaddr = new InetSocketAddress(iaddr, gPort);
            //gSocket = new Socket("localhost", 3000);
            gSocket = new Socket();
            gSocket.setSoLinger(false, 1);
            gSocket.setKeepAlive(false);
            gSocket.setTcpNoDelay(true);
            gSocket.setSoTimeout(3000);
            gSocket.bind(null);
            gSocket.connect(sockaddr, 10000);
           // gSocket.setSoTimeout(gTTL);
        } catch (UnknownHostException ex) {
           throw new CalDriverException("TCP Client Unknown host");
        }catch (SocketTimeoutException e) {
            throw new CalDriverException("TCP Client Socket timeout");
        } catch (IOException ex) {
            throw new CalDriverException("TCP Client Connection failed");
        }

        System.out.println("TCPClient connected");

        drvInputStream = null;
        drvOutputStream = null;
        try {
            drvInputStream = gSocket.getInputStream();
            drvOutputStream = gSocket.getOutputStream();
        } catch (IOException ex) {
            throw new CalDriverException("TCP Client failed to get streams");
        }
    }

    private void DisConnect() throws CalDriverException {
        if(gSocket != null) {
            try {
                gSocket.close();
            } catch (IOException ex) {
                throw new CalDriverException("Failed to close socket");
            }
            drvInputStream = null;
            drvOutputStream = null;
        }
    }

    // exported functions
    @Override
    synchronized public void Open() throws CalDriverException {
        super.Open();
    }

    @Override
    synchronized public void Close() throws CalDriverException {
        super.Close();
    }

    @Override
    synchronized public void Start() throws CalDriverException {
        super.Start();
        Connect();
    }

    @Override
    synchronized public void Stop() throws CalDriverException {
        super.Stop();
        DisConnect();
    }

   @Override
   synchronized public int WriteRead( byte[] Buffer, int Length ) throws CalDriverException {
       int ret;
       Connect();
       ret = super.WriteRead(Buffer, Length);
       DisConnect();
       return ret;
   }

    @Override
    public void run() {

       // while(super.drvRunning) {

       //}

    }

}
