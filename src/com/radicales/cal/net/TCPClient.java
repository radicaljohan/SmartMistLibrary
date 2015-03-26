/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.cal.net;

import android.util.Log;

import com.radicales.cal.CalDriver;
import com.radicales.cal.CalDriverException;
import java.io.IOException;
import java.net.*;

/**
 *
 * @author JanZwiegers
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

    // local functions
    private void Connect() throws CalDriverException {
        InetAddress iaddr;
        InetSocketAddress sockaddr;

        Log.d("SmartMist", "TCPClient connect...");

        try {
            iaddr = InetAddress.getByName(gIpAddress);
            sockaddr = new InetSocketAddress(iaddr, gPort);
            //gSocket = new Socket("localhost", 3000);
            gSocket = new Socket();
            //gSocket = new Socket(iaddr, gPort);
            gSocket.setSoLinger(false, 1);
            gSocket.setKeepAlive(false);
            gSocket.setTcpNoDelay(true);
            gSocket.setSoTimeout(3000);
            gSocket.bind(null);
            gSocket.connect(sockaddr, 10000);
            gSocket.setSoTimeout(gTTL);
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
