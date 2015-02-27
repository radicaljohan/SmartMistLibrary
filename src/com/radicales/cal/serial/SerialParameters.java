/*
 * Copyright 2002-2010 jamod development team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.radicales.cal.serial;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Helper class wrapping all serial port communication parameters.
 * Very similar to the javax.comm demos, however, not the same.
 *
 * @author Dieter Wimberger
 * @author John Charlton
 * @version @version@ (@date@)
 */
public class SerialParameters {
    public static final int BAUDRATE_110 = 110;
    public static final int BAUDRATE_300 = 300;
    public static final int BAUDRATE_600 = 600;
    public static final int BAUDRATE_1200 = 1200;
    public static final int BAUDRATE_4800 = 4800;
    public static final int BAUDRATE_9600 = 9600;
    public static final int BAUDRATE_14400 = 14400;
    public static final int BAUDRATE_19200 = 19200;
    public static final int BAUDRATE_38400 = 38400;
    public static final int BAUDRATE_57600 = 57600;
    public static final int BAUDRATE_115200 = 115200;
    public static final int BAUDRATE_128000 = 128000;
    public static final int BAUDRATE_256000 = 256000;

    public static final int DATABITS_5 = 5;
    public static final int DATABITS_6 = 6;
    public static final int DATABITS_7 = 7;
    public static final int DATABITS_8 = 8;

    public static final int FLOWCONTROL_NONE = 0;
    public static final int FLOWCONTROL_RTSCTS_IN = 1;
    public static final int FLOWCONTROL_RTSCTS_OUT = 2;
    public static final int FLOWCONTROL_XONXOFF_IN = 4;
    public static final int FLOWCONTROL_XONXOFF_OUT = 8;

    public static final int STOPBITS_1 = 1;
    public static final int STOPBITS_2 = 2;
    public static final int STOPBITS_1_5 = 3;

    public static final int PARITY_NONE = 0;
    public static final int PARITY_ODD = 1;
    public static final int PARITY_EVEN = 2;
    public static final int PARITY_MARK = 3;
    public static final int PARITY_SPACE = 4;

    //instance attributes
    private String m_Name;
    private String m_PortName;
    private int m_BaudRate;
    private int m_FlowControlIn;
    private int m_FlowControlOut;
    private int m_Databits;
    private int m_Stopbits;
    private int m_Parity;
    private boolean m_Echo;
    private int m_ReceiveTimeout;

  /**
   * Constructs a new <tt>SerialParameters</tt> instance with
   * default values.
   */
  public SerialParameters( String Name ) {
    m_Name = Name;
    m_PortName = "";
    m_BaudRate = BAUDRATE_9600;
    m_FlowControlIn = FLOWCONTROL_NONE;
    m_FlowControlOut = FLOWCONTROL_NONE;
    m_Databits = DATABITS_8;
    m_Stopbits = STOPBITS_1;
    m_Parity = PARITY_NONE;
    m_ReceiveTimeout = 2000; //5 secs
    m_Echo = false;
  }//constructor

  /**
   * Constructs a new <tt>SerialParameters</tt> instance with
   * given parameters.
   *
   * @param portName       The name of the port.
   * @param baudRate       The baud rate.
   * @param flowControlIn  Type of flow control for receiving.
   * @param flowControlOut Type of flow control for sending.
   * @param databits       The number of data bits.
   * @param stopbits       The number of stop bits.
   * @param parity         The type of parity.
   * @param echo           Flag for setting the RS485 echo mode.
   */
  public SerialParameters( String name,
                          String portName,
                          int baudRate,
                          int flowControlIn,
                          int flowControlOut,
                          int databits,
                          int stopbits,
                          int parity,
                          boolean echo,
                          int timeout) {
    m_Name = name;
    m_PortName = portName;
    m_BaudRate = baudRate;
    m_FlowControlIn = flowControlIn;
    m_FlowControlOut = flowControlOut;
    m_Databits = databits;
    m_Stopbits = stopbits;
    m_Parity = parity;
    m_Echo = echo;
    m_ReceiveTimeout = timeout;
  }//constructor

  /**
   * Constructs a new <tt>SerialParameters</tt> instance with
   * parameters obtained from a <tt>Properties</tt> instance.
   *
   * @param props  a <tt>Properties</tt> instance.
   * @param prefix a prefix for the properties keys if embedded into
   *               other properties.
   */
  public SerialParameters(Properties props, String prefix) {
    if (prefix == null) {
      prefix = "";
    }
    setPortName(props.getProperty(prefix + "portName", ""));
    setBaudRate(props.getProperty(prefix + "baudRate", "" + 9600));
    setFlowControlIn(props.getProperty(prefix + "flowControlIn", "" + FLOWCONTROL_NONE));
    setFlowControlOut(props.getProperty(prefix + "flowControlOut", "" + FLOWCONTROL_NONE));
    setParity(props.getProperty(prefix + "parity", "" + PARITY_NONE));
    setDatabits(props.getProperty(prefix + "databits", "" + DATABITS_8));
    setStopbits(props.getProperty(prefix + "stopbits", "" + STOPBITS_1));
    setEcho("true".equals(props.getProperty(prefix + "echo")));
    setReceiveTimeout(props.getProperty(prefix + "timeout", "" + 500));
  }//constructor


  public boolean parseXMLFile( String Name ) {
      File file = new File(Name);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder;
      Document doc = null;

      try {
          dBuilder = dbFactory.newDocumentBuilder();
          doc = dBuilder.parse(file);
      } catch (IOException | ParserConfigurationException | SAXException ex) {
          return false;
      }

       if (doc != null) {
          doc.getDocumentElement().normalize();
          Node pn = doc.getFirstChild();

          if ((pn != null) && (pn.getNodeName().matches("SerialParameters"))) {

              NodeList nList = pn.getChildNodes();

              for (int temp = 0; temp < nList.getLength(); temp++) {
                  Node cNode = nList.item(temp);
                  if (cNode.getNodeType() == Node.ELEMENT_NODE) {
                      Element eElement = (Element) cNode;
                      if (eElement.getNodeName().matches("Name")) {
                          m_Name = eElement.getTextContent();
                      } else if (eElement.getNodeName().matches("Baudrate")) {
                          this.setBaudRate(eElement.getTextContent());
                      } else if (eElement.getNodeName().matches("Port")) {
                          m_PortName = eElement.getTextContent();
                      } else if (eElement.getNodeName().matches("Databits")) {
                          this.setDatabits(eElement.getTextContent());
                      } else if (eElement.getNodeName().matches("Parity")) {
                          this.setParity(eElement.getTextContent());
                      } else if (eElement.getNodeName().matches("FlowcontrolIn")) {
                          this.setFlowControlIn(eElement.getTextContent());
                      } else if (eElement.getNodeName().matches("FlowcontrolOut")) {
                          this.setFlowControlOut(eElement.getTextContent());
                      } else {
                          //ignore
                      }
                  }
              }

          } else {
              return false;
          }
      } else {
          return false;
      }

       return true;
  }


  /**
   * Sets the name.
   *
   * @param name the new name.
   */
  public void setName(String name) {
    m_Name = name;
  }//setName

  /**
   * Returns the name.
   *
   * @return the name.
   */
  public String getName() {
    return m_Name;
  }//getName


  /**
   * Sets the port name.
   *
   * @param name the new port name.
   */
  public void setPortName(String name) {
    m_PortName = name;
  }//setPortName

  /**
   * Returns the port name.
   *
   * @return the port name.
   */
  public String getPortName() {
    return m_PortName;
  }//getPortName

  /**
   * Sets the baud rate.
   *
   * @param rate the new baud rate.
   */
  public void setBaudRate(int rate) {
    m_BaudRate = rate;
  }//setBaudRate

  /**
   * Sets the baud rate.
   *
   * @param rate the new baud rate.
   */
  public void setBaudRate(String rate) {
      System.out.println("Baudrate: " + rate);
    m_BaudRate = Integer.parseInt(rate);
  }//setBaudRate

  /**
   * Return the baud rate as <tt>int</tt>.
   *
   * @return the baud rate as <tt>int</tt>.
   */
  public int getBaudRate() {
    return m_BaudRate;
  }//getBaudRate

  /**
   * Returns the baud rate as a <tt>String</tt>.
   *
   * @return the baud rate as <tt>String</tt>.
   */
  public String getBaudRateString() {
    return Integer.toString(m_BaudRate);
  }//getBaudRateString

  /**
   * Sets the type of flow control for the input
   * as given by the passed in <tt>int</tt>.
   *
   * @param flowcontrol the new flow control type.
   */
  public void setFlowControlIn(int flowcontrol) {
    m_FlowControlIn = flowcontrol;
  }//setFlowControl

  /**
   * Sets the type of flow control for the input
   * as given by the passed in <tt>String</tt>.
   *
   * @param flowcontrol the flow control for reading type.
   */
  public void setFlowControlIn(String flowcontrol) {
    m_FlowControlIn = stringToFlow(flowcontrol);
  }//setFlowControlIn

  /**
   * Returns the input flow control type as <tt>int</tt>.
   *
   * @return the input flow control type as <tt>int</tt>.
   */
  public int getFlowControlIn() {
    return m_FlowControlIn;
  }//getFlowControlIn

  /**
   * Returns the input flow control type as <tt>String</tt>.
   *
   * @return the input flow control type as <tt>String</tt>.
   */
  public String getFlowControlInString() {
    return flowToString(m_FlowControlIn);
  }//getFlowControlIn

  /**
   * Sets the output flow control type as given
   * by the passed in <tt>int</tt>.
   *
   * @param flowControlOut new output flow control type as <tt>int</tt>.
   */
  public void setFlowControlOut(int flowControlOut) {
    m_FlowControlOut = flowControlOut;
  }//setFlowControlOut

  /**
   * Sets the output flow control type as given
   * by the passed in <tt>String</tt>.
   *
   * @param flowControlOut the new output flow control type as <tt>String</tt>.
   */
  public void setFlowControlOut(String flowControlOut) {
    m_FlowControlOut = stringToFlow(flowControlOut);
  }//setFlowControlOut

  /**
   * Returns the output flow control type as <tt>int</tt>.
   *
   * @return the output flow control type as <tt>int</tt>.
   */
  public int getFlowControlOut() {
    return m_FlowControlOut;
  }//getFlowControlOut

  /**
   * Returns the output flow control type as <tt>String</tt>.
   *
   * @return the output flow control type as <tt>String</tt>.
   */
  public String getFlowControlOutString() {
    return flowToString(m_FlowControlOut);
  }//getFlowControlOutString

  /**
   * Sets the number of data bits.
   *
   * @param databits the new number of data bits.
   */
  public void setDatabits(int databits) {
    m_Databits = databits;
  }//setDatabits

  /**
   * Sets the number of data bits from the given <tt>String</tt>.
   *
   * @param databits the new number of data bits as <tt>String</tt>.
   */
  public void setDatabits(String databits) {
    if (databits.equals("5")) {
      m_Databits = DATABITS_5;
    }
    if (databits.equals("6")) {
      m_Databits = DATABITS_6;
    }
    if (databits.equals("7")) {
      m_Databits = DATABITS_7;
    }
    if (databits.equals("8")) {
      m_Databits = DATABITS_8;
    }
  }//setDatabits

  /**
   * Returns the number of data bits as <tt>int</tt>.
   *
   * @return the number of data bits as <tt>int</tt>.
   */
  public int getDatabits() {
    return m_Databits;
  }//getDatabits

  /**
   * Returns the number of data bits as <tt>String</tt>.
   *
   * @return the number of data bits as <tt>String</tt>.
   */
  public String getDatabitsString() {
    switch (m_Databits) {
      case DATABITS_5:
        return "5";
      case DATABITS_6:
        return "6";
      case DATABITS_7:
        return "7";
      case DATABITS_8:
        return "8";
      default:
        return "8";
    }
  }//getDataBits

  /**
   * Sets the number of stop bits.
   *
   * @param stopbits the new number of stop bits setting.
   */
  public void setStopbits(int stopbits) {
    m_Stopbits = stopbits;
  }//setStopbits

  /**
   * Sets the number of stop bits from the given <tt>String</tt>.
   *
   * @param stopbits the number of stop bits as <tt>String</tt>.
   */
  public void setStopbits(String stopbits) {
    if (stopbits.equals("1")) {
      m_Stopbits = STOPBITS_1;
    }
    if (stopbits.equals("1.5")) {
      m_Stopbits = STOPBITS_1_5;
    }
    if (stopbits.equals("2")) {
      m_Stopbits = STOPBITS_2;
    }
  }//setStopbits

  /**
   * Returns the number of stop bits as <tt>int</tt>.
   *
   * @return the number of stop bits as <tt>int</tt>.
   */
  public int getStopbits() {
    return m_Stopbits;
  }//getStopbits

  /**
   * Returns the number of stop bits as <tt>String</tt>.
   *
   * @return the number of stop bits as <tt>String</tt>.
   */
  public String getStopbitsString() {
    switch (m_Stopbits) {
      case STOPBITS_1:
        return "1";
      case STOPBITS_1_5:
        return "1.5";
      case STOPBITS_2:
        return "2";
      default:
        return "1";
    }
  }//getStopbitsString

  /**
   * Sets the parity schema.
   *
   * @param parity the new parity schema as <tt>int</tt>.
   */
  public void setParity(int parity) {
    m_Parity = parity;
  }//setParity

  /**
   * Sets the parity schema from the given
   * <tt>String</tt>.
   *
   * @param parity the new parity schema as <tt>String</tt>.
   */
  public void setParity(String parity) {
    parity = parity.toLowerCase();
    if (parity.equals("none")) {
      m_Parity = PARITY_NONE;
    }
    if (parity.equals("even")) {
      m_Parity = PARITY_EVEN;
    }
    if (parity.equals("odd")) {
      m_Parity = PARITY_ODD;
    }
  }//setParity

  /**
   * Returns the parity schema as <tt>int</tt>.
   *
   * @return the parity schema as <tt>int</tt>.
   */
  public int getParity() {
    return m_Parity;
  }//getParity

  /**
   * Returns the parity schema as <tt>String</tt>.
   *
   * @return the parity schema as <tt>String</tt>.
   */
  public String getParityString() {
    switch (m_Parity) {
      case PARITY_NONE:
        return "none";
      case PARITY_EVEN:
        return "even";
      case PARITY_ODD:
        return "odd";
      default:
        return "none";
    }
  }//getParityString

  /**
   * Get the Echo value.
   *
   * @return the Echo value.
   */
  public boolean isEcho() {
    return m_Echo;
  }//getEcho

  /**
   * Set the Echo value.
   *
   * @param newEcho The new Echo value.
   */
  public void setEcho(boolean newEcho) {
    m_Echo = newEcho;
  }//setEcho

  /**
   * Returns the receive timeout for serial communication.
   *
   * @return the timeout in milliseconds.
   */
  public int getReceiveTimeout() {
    return m_ReceiveTimeout;
  }//getReceiveTimeout

  /**
   * Sets the receive timeout for serial communication.
   *
   * @param receiveTimeout the receiveTimeout in milliseconds.
   */
  public void setReceiveTimeout(int receiveTimeout) {
    m_ReceiveTimeout = receiveTimeout;
  }//setReceiveTimeout

  /**
   * Sets the receive timeout for the serial communication
   * parsing the given String using <tt>Integer.parseInt(String)</tt>.
   *
   * @param str the timeout as String.
   */
  public void setReceiveTimeout(String str) {
   m_ReceiveTimeout = Integer.parseInt(str);
  }//setReceiveTimeout

  /**
   * Converts a <tt>String</tt> describing a flow control type to the
   * <tt>int</tt> which is defined in SerialPort.
   *
   * @param flowcontrol the <tt>String</tt> describing the flow control type.
   * @return the <tt>int</tt> describing the flow control type.
   */
  private int stringToFlow(String flowcontrol) {
    flowcontrol = flowcontrol.toLowerCase();
    if (flowcontrol.equals("none")) {
      return FLOWCONTROL_NONE;
    }
    if (flowcontrol.equals("xon/xoff out")) {
      return FLOWCONTROL_XONXOFF_OUT;
    }
    if (flowcontrol.equals("xon/xoff in")) {
      return FLOWCONTROL_XONXOFF_IN;
    }
    if (flowcontrol.equals("rts/cts in")) {
      return FLOWCONTROL_RTSCTS_IN;
    }
    if (flowcontrol.equals("rts/cts out")) {
      return FLOWCONTROL_RTSCTS_OUT;
    }
    return FLOWCONTROL_NONE;
  }//stringToFlow

  /**
   * Converts an <tt>int</tt> describing a flow control type to a
   * String describing a flow control type.
   *
   * @param flowcontrol the <tt>int</tt> describing the
   *                    flow control type.
   * @return the <tt>String</tt> describing the flow control type.
   */
  private String flowToString(int flowcontrol) {
    switch (flowcontrol) {
      case FLOWCONTROL_NONE:
        return "none";
      case FLOWCONTROL_XONXOFF_OUT:
        return "xon/xoff out";
      case FLOWCONTROL_XONXOFF_IN:
        return "xon/xoff in";
      case FLOWCONTROL_RTSCTS_IN:
        return "rts/cts in";
      case FLOWCONTROL_RTSCTS_OUT:
        return "rts/cts out";
      default:
        return "none";
    }
  }//flowToString

  /**
   * Populates the settings from an <tt>Proper</tt>
   * that reads from a properties file or contains a
   * set of properties.
   *
   * @param in the <tt>InputStream</tt> to read from.
   *
   private void loadFrom(InputStream in) throws IOException {
   Properties props = new Properties();
   props.load(in);
   setPortName(props.getProperty("portName"));
   setBaudRate(props.getProperty("baudRate"));
   setFlowControlIn(props.getProperty("flowControlIn"));
   setFlowControlOut(props.getProperty("flowControlOut"));
   setParity(props.getProperty("parity"));
   setDatabits(props.getProperty("databits"));
   setStopbits(props.getProperty("stopbits"));
   setEncoding(props.getProperty("encoding"));
   setEcho(new Boolean(props.getProperty("echo")).booleanValue());
   }//loadFrom

   */
}
