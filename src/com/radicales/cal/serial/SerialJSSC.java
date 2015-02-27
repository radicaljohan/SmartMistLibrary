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
package com.radicales.cal.serial;

import com.radicales.cal.CalDriver;
import com.radicales.cal.CalDriverException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.*;

/**
 * CAL Serial Driver for JSSC
 * Provides a CAL serial port driver for JSSC
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
public class SerialJSSC extends CalDriver implements SerialPortEventListener {

    private SerialParameters gParameters;
    private SerialPort gSerialPort;
   // private CommPortIdentifier gPortIdentifyer;
    private static final boolean debug = true;

    public SerialJSSC( String Name ) {
        super(CalDriver.DRIVER_TYPE_JSSC, false);
        gParameters = new SerialParameters(Name);
    }

    public SerialJSSC( String Name, String PortName ) {
        super(CalDriver.DRIVER_TYPE_JSSC, false);
        gParameters = new SerialParameters(Name);
        gParameters.setPortName(PortName);
    }

    public SerialJSSC( SerialParameters Parameters ) {
        super(CalDriver.DRIVER_TYPE_JSSC, false);
        gParameters = Parameters;
    }

    public SerialJSSC( int Id, boolean Start, SerialParameters Parameters ) {
        super(CalDriver.DRIVER_TYPE_JSSC, Id,Start,false);
        gParameters = Parameters;
    }
    public String getPortName() {
        return gParameters.getPortName();
    }

      /**
   * Opens the communication port.
   *
   * @throws Exception if an error occurs.
   */
    @Override
    public void Open() throws CalDriverException {
        boolean match = false;

        super.Open();
        //1. obtain a CommPortIdentifier instance
        String[] portNames = SerialPortList.getPortNames();
        for(String s : portNames) {
            if(s.matches(gParameters.getPortName())) {
                match = true;
                break;
            }
        }
        match = true;

        if(!match) {
            if(debug) {
                System.out.println("Serial: No such port: " + gParameters.getPortName());
            }
            throw new CalDriverException("No such serial port " + gParameters.getPortName());
        }


        //2. open the port, wait for given timeout
        gSerialPort = new SerialPort(gParameters.getPortName());
        try {
            gSerialPort.openPort();
        } catch (SerialPortException e) {
            if(debug) {
                System.out.println("Serial port open error: " + gParameters.getPortName() + "(" + e.getMessage() +")");
            }
            throw new CalDriverException(e.getMessage());
        }

        //3. set the parameters
        try {
            setConnectionParameters();
        } catch (Exception e) {
            try {
                //ensure it is closed
                gSerialPort.closePort();
                if(debug) {
                    System.out.println("Serial: Failed to set port parameters");
                    System.out.println(e.getMessage());
                }
                throw new CalDriverException(e.getMessage());
            } catch (SerialPortException ex) {
                throw new CalDriverException(e.getMessage());
            }
        }

        // Add this object as an event listener for the serial port.
        try {
            gSerialPort.addEventListener(this);
        } catch (SerialPortException e) {
            try {
                gSerialPort.closePort();
                if(debug) {
                    System.out.println("Serial: Too many event listeners");
                    System.out.println(e.getMessage());
                }
                throw new CalDriverException(e.getMessage());
            } catch (SerialPortException ex) {
                throw new CalDriverException(e.getMessage());
            }
        }

        // Set notifyOnBreakInterrup to allow event driven break handling.
        try {
            int mask = SerialPort.MASK_CTS;// | SerialPort.MASK_RXCHAR | SerialPort.MASK_TXEMPTY | SerialPort.MASK_CTS;
            gSerialPort.setEventsMask(mask);
        }
        catch(SerialPortException e) {
            throw new CalDriverException(e.getMessage());
        }
    }

    @Override
    public void Flush() throws CalDriverException {
        try {
            gSerialPort.purgePort(SerialPort.PURGE_RXCLEAR | SerialPort.PURGE_TXCLEAR);
        }
        catch(SerialPortException e) {
            throw new CalDriverException(e.getMessage());
        }
    }

    /**
    * Close the port and clean up associated elements.
    */
    @Override
    public void Close() throws CalDriverException {
        super.Close();
        try {
            gSerialPort.closePort();
        }
        catch(SerialPortException e){
            throw new CalDriverException(e.getMessage());
        }

    }//close


        /**
   * Sets the connection parameters to the setting in the parameters object.
   * If set fails return the parameters object to origional settings and
   * throw exception.
   *
   * @throws Exception if the configured parameters cannot be set properly
   *         on the port.
   */
    public void setConnectionParameters() throws Exception {


        // Set connection parameters, if set fails return parameters object
        // to original state.
        try {
            gSerialPort.setParams(gParameters.getBaudRate(),
                gParameters.getDatabits(),
                gParameters.getStopbits(),
                gParameters.getParity());
        } catch (SerialPortException e) {

            if(debug) {
                System.out.println("Serial: Unsupported parameter");
                System.out.println(e.getMessage());
            }
            throw new Exception("Unsupported parameter");
        }

        // Set flow control.
        try {
            gSerialPort.setFlowControlMode(gParameters.getFlowControlIn() | gParameters.getFlowControlOut());
        } catch (SerialPortException e) {
           if(debug) {
                System.out.println("Serial: Failed to set flow control");
                System.out.println(e.getMessage());
           }
            throw new Exception("Unsupported flow control");
        }

    }//setConnectionParameters


    @Override
    public int Write( byte[] Buffer, int Length ) throws CalDriverException {
        byte[] b = new byte[Length];
        System.arraycopy(Buffer, 0, b, 0, Length);
        try {
            gSerialPort.writeBytes(b);
        } catch (SerialPortException ex) {
            throw new CalDriverException(ex.getMessage());
        }
        return Length;
    }

    @Override
    public int Write( byte[] Buffer ) throws CalDriverException {
        try {
            gSerialPort.writeBytes(Buffer);
        } catch (SerialPortException ex) {
            throw new CalDriverException(ex.getMessage());
        }
        return Buffer.length;
    }

    @Override
    public int Read( byte[] Buffer, int Length ) throws CalDriverException {
        int ret = 0;
        byte[] b;
        try {
            if(gParameters.getReceiveTimeout() > 0) {
                b = gSerialPort.readBytes(Length, gParameters.getReceiveTimeout());
            }
            else {
                int ab = gSerialPort.getInputBufferBytesCount();
                if(ab > Length) {
                    b = gSerialPort.readBytes(Length);
                }
                else {
                    b = gSerialPort.readBytes();
                }
            }
            if(b != null) {
                try {
                    System.arraycopy(b, 0, Buffer, 0, b.length);
                    ret = b.length;
                }
                catch(ArrayIndexOutOfBoundsException ex) {
                    ret = 0;
                }
            }
        } catch (SerialPortException ex) {
            throw new CalDriverException(ex.getMessage());
        } catch( SerialPortTimeoutException ext ) {
            ret = -1;
        }

        return ret;
    }

    @Override
    public int Read( byte[] Buffer ) throws CalDriverException {
        int ret = 0;
        try {
            byte[] b;
            if(gParameters.getReceiveTimeout() > 0) {
                b = gSerialPort.readBytes(Buffer.length, gParameters.getReceiveTimeout());
            }
            else {
                int ab = gSerialPort.getInputBufferBytesCount();
                if(ab > Buffer.length) {
                    b = gSerialPort.readBytes(Buffer.length);
                }
                else {
                    b = gSerialPort.readBytes();
                }
            }
            if(b != null) {
                try {
                    System.arraycopy(b, 0, Buffer, 0, b.length);
                    ret = b.length;
                }
                catch(ArrayIndexOutOfBoundsException ex) {
                    ret = 0;
                }
            }
        } catch (SerialPortException ex) {
            throw new CalDriverException(ex.getMessage());
        } catch(SerialPortTimeoutException ext) {
            ret = -1;
        }
        return ret;
    }

    @Override
    public int Read() throws CalDriverException {
        int ret = 0;
        try {
            byte[] b;
            if(gParameters.getReceiveTimeout() > 0) {
                b = gSerialPort.readBytes(1, gParameters.getReceiveTimeout());
            }
            else {
                b = gSerialPort.readBytes(1);
            }
            if(b.length > 0) {
                ret = b[0];
            }
        } catch (SerialPortException ex ) {
            throw new CalDriverException(ex.getMessage());
        } catch(SerialPortTimeoutException ext) {
            ret = -1;
        }

        return ret;
    }

    @Override
    public void serialEvent(SerialPortEvent spe) {
        if(spe.isRXCHAR()){
        }
        else if(spe.isCTS()){
            drvEventListener.DriverEventNotify(SerialPort.MASK_CTS, spe.getEventValue());
        }
        else if(spe.isDSR()){
            drvEventListener.DriverEventNotify(SerialPort.MASK_DSR, spe.getEventValue());
        }
    }

    @Override
    public int IoControl( int Code, int Arg ) throws CalDriverException {
        int ret = -1;
        try {
            switch(Code) {
                case Serial.SERIAL_SET_RTS:
                    if(Arg > 0) {
                        gSerialPort.setRTS(true);
                    }
                    else {
                        gSerialPort.setRTS(false);
                    }
                    ret = 0;
                    break;
            }
        } catch (SerialPortException ex) {
            throw new CalDriverException(ex.getMessage());
        }

        return ret;
    }
}
