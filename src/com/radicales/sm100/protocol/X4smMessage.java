/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author JanZwiegers
 */
public class X4smMessage {

    public static final int MSG_TYPE_NOTSET = 0;
    public static final int MSG_TYPE_UNKNOWN = -1;
    public static final int MSG_TYPE_ZONE = 1;
    public static final int MSG_TYPE_PROG = 2;
    public static final int MSG_TYPE_DATETIME = 3;
    public static final int MSG_TYPE_ERROR = 4;
    public static final int MSG_TYPE_EOT = 5;
    public static final int MSG_TYPE_FIRMWARE = 6;
    public static final int MSG_TYPE_SET_ZONE = 7;
    public static final int MSG_TYPE_GET_PROGRAM = 8;
    public static final int MSG_TYPE_GET_CONFIG = 9;
    public static final int MSG_TYPE_CONFIG = 10;
    public static final int MSG_TYPE_ACK = 11;
    public static final int MSG_TYPE_PING = 12;
    public static final int MSG_TYPE_SETDATETIME = 13;
    public static final int MSG_TYPE_GETDATETIME = 14;
    public static final int MSG_TYPE_GETPROGRAMS = 15;
    public static final int MSG_TYPE_PROGRAMS = 16;
    public static final int MSG_TYPE_PROGRAM = 17;
    public static final int MSG_TYPE_START_PROGRAM = 18;
    public static final int MSG_TYPE_STOP_PROGRAM = 19;
    public static final int MSG_TYPE_GETZONES = 20;
    public static final int MSG_TYPE_ZONES = 21;
    public static final int MSG_TYPE_GET_ZONES = 22;
    public static final int MSG_TYPE_GET_INFO = 23;
    public static final int MSG_TYPE_INFO = 24;
    public static final int MSG_TYPE_GET_STATUS = 25;
    public static final int MSG_TYPE_ACTIVEPROGRAM = 26;
    public static final int MSG_TYPE_INPUTS = 27;
    public static final int MSG_TYPE_REMOVESETUP = 28;
    public static final int MSG_TYPE_ADD_ZONE = 29;
    public static final int MSG_TYPE_ADD_PROGRAM = 30;
    public static final int MSG_TYPE_READY = 31;

    private String gData;
    private int gType;
    private List<X4smAttribute> gAttributes = new ArrayList<>();

    public X4smMessage() {
        gData = "<Empty />";
        gType = MSG_TYPE_NOTSET;
    }

    public X4smMessage( String Data ) {
       gData = Data;
        gType = MSG_TYPE_NOTSET;
    }

    public X4smMessage( int Type ) {
        gType = Type;
        gData = "<Empty />";
    }

    public void setData( String Data ) {
        gData = Data;
    }

    public String getData() {
        return gData;
    }

    public List getAttributes() {
        return gAttributes;
    }

    public X4smMessage parseData() throws X4smException {

        X4smMessage msg = null;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder;
        Document doc = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(gData));
            doc = dBuilder.parse(is);
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            System.out.println("XML Error");
            throw new X4smException("XML Error");
        }

        doc.getDocumentElement().normalize();
        Node node = doc.getFirstChild();
        String cmd = node.getNodeName();
        NamedNodeMap attr = node.getAttributes();
        gAttributes.clear();
        for(int i=0; i<attr.getLength(); i++) {
            Node n = attr.item(i);
            X4smAttribute a = new X4smAttribute(n.getNodeName(), n.getNodeValue());
            gAttributes.add(a);
        }

        gType = MSG_TYPE_UNKNOWN;

        // decode command
        if(cmd.matches("Ack")) {
            gType = MSG_TYPE_ACK;
            msg = this;
        }
        else if(cmd.matches("Ready")) {
            gType = MSG_TYPE_READY;
            msg = this;
        }
        else if(cmd.matches("Error")) {
            gType = MSG_TYPE_ERROR;
        }
        else if(cmd.matches("EOT")) {
            gType = MSG_TYPE_EOT;
        }
        else if(cmd.matches("Firmware")) {
            gType = MSG_TYPE_FIRMWARE;
        }
        else if(cmd.matches("DateTime")) {
            gType = MSG_TYPE_DATETIME;
            X4smDateTimeMessage m = new X4smDateTimeMessage(gAttributes);
            m.parse();
            msg = m;
        }
        else if(cmd.matches("Programs")) {
            gType = MSG_TYPE_PROGRAMS;
            X4smProgramsMessage m = new X4smProgramsMessage(gAttributes);
            m.parse();
            msg = m;
        }
        else if(cmd.matches("Config")) {
            gType = MSG_TYPE_CONFIG;
            X4smConfigMessage m = new X4smConfigMessage(gAttributes);
            m.parse();
            msg = m;
        }
        else if(cmd.matches("Program")) {
            gType = MSG_TYPE_PROGRAM;
            X4smProgramMessage m = new X4smProgramMessage(gAttributes);
            m.parse();
            msg = m;
        }
        else if(cmd.matches("Zones")) {
            gType = MSG_TYPE_ZONES;
            X4smZonesMessage m = new X4smZonesMessage(gAttributes);
            m.parse();
            msg = m;
        }
        else if(cmd.matches("Zone")) {
            gType = MSG_TYPE_ZONE;
            X4smZoneMessage m = new X4smZoneMessage(gAttributes);
            m.parse();
            msg = m;
        }
        else if(cmd.matches("DeviceInfo")) {
            gType = MSG_TYPE_INFO;
            X4smInfoMessage m = new X4smInfoMessage(gAttributes);
            m.parse();
            msg = m;
        }
        else if(cmd.matches("Inputs")) {
            gType = MSG_TYPE_INPUTS;
            X4smInputsMessage m = new X4smInputsMessage(gAttributes);
            m.parse();
            msg = m;
        }
        else if(cmd.matches("ActiveProgram")) {
            gType = MSG_TYPE_ACTIVEPROGRAM;
            X4smActiveProgramMessage m = new X4smActiveProgramMessage(gAttributes);
            m.parse();
            msg = m;
        }
        else {
            System.out.println("Unknown message: " + cmd);
        }

        return msg;
    }

    public void buildData( int DestSystem, int DestAddress, int SrcSystem, int SrcAddress, int Function, int Offset, byte[] Value ) {
        //gDestSystem = DestSystem;
    }

    public void fireEvent( List<X4smEvent> Listeners ) {

        switch(gType) {
            case MSG_TYPE_CONFIG:
                X4smConfigMessage msg = (X4smConfigMessage)this;
                for(X4smEvent ev : Listeners) {
                    ev.eventConfig(msg.getFilename(), msg.getZones(), msg.getPrograms());
                }
                break;
            case MSG_TYPE_ACK:
                for(X4smEvent ev : Listeners) {
                    ev.eventAck();
                }
                break;
            case MSG_TYPE_DATETIME:
                X4smDateTimeMessage msgdt = (X4smDateTimeMessage)this;
                for(X4smEvent ev : Listeners) {
                    ev.eventDateTime(msgdt.getDateTime());
                }
                break;
            case MSG_TYPE_PROGRAMS:
                X4smProgramsMessage msgprogs = (X4smProgramsMessage)this;
                for(X4smEvent ev : Listeners) {
                    ev.eventPrograms(msgprogs.getNames());
                }
                break;
            case MSG_TYPE_PROGRAM:
                X4smProgramMessage msgprog = (X4smProgramMessage)this;
                for(X4smEvent ev : Listeners) {
                    ev.eventProgram(msgprog.getName(), msgprog.getControlWord(), msgprog.getWaterBudget(), msgprog.getStartTimes(), msgprog.getSequences() );
                }
                break;
            case MSG_TYPE_ZONES:
                X4smZonesMessage msgzones = (X4smZonesMessage)this;
                for(X4smEvent ev : Listeners) {
                    if(msgzones.getMessageType() == X4smZonesMessage.ZONE_MSG_TYPE_NAMES) {
                        ev.eventZones(msgzones.getNames());
                    }
                    else {
                        System.out.println("Zone status update");
                        ev.eventZonesStatus(msgzones.getNames(), msgzones.getStatus());
                    }
                }
                break;
            case MSG_TYPE_ZONE:
                X4smZoneMessage msgzone = (X4smZoneMessage)this;
                for(X4smEvent ev : Listeners) {
                    ev.eventZone(msgzone.getName(), msgzone.getChannel(), msgzone.getControlWord(), msgzone.getInitValue(), msgzone.getOffDelay());
                }
                break;
            case MSG_TYPE_INFO:
                X4smInfoMessage msginfo = (X4smInfoMessage)this;
                for(X4smEvent ev : Listeners) {
                    ev.eventInformation(msginfo.getName(), msginfo.getFamily(), msginfo.getRevision(), msginfo.getChannels());
                }
                break;
            case MSG_TYPE_ACTIVEPROGRAM:
                X4smActiveProgramMessage msgactprog = (X4smActiveProgramMessage)this;
                for(X4smEvent ev : Listeners) {
                    ev.eventActiveProgram(msgactprog.isActive(), msgactprog.getProgramName(), msgactprog.getZoneName(), msgactprog.getStatus(), msgactprog.getRunTime(), msgactprog.getTimeToRun());
                }
                break;
            case MSG_TYPE_INPUTS:
                X4smInputsMessage msgin = (X4smInputsMessage)this;
                for(X4smEvent ev : Listeners) {
                    ev.eventInputs(msgin.getStatus());
                }
                break;

            case MSG_TYPE_READY:
                for(X4smEvent ev : Listeners) {
                    ev.eventReady();
                }
                break;
        }
    }


}
