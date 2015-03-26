/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

import java.util.Date;

/**
 *
 * @author JanZwiegers
 */
public interface X4smEvent {
    void eventInformation( String Name, String Family, String Revision, int Channels );
    void eventZone( int Channel, int Status );
    void eventFirmware( String Version );
    void eventConfig( String FileName, int Zones, int Programs );
    void eventAck();
    void eventDateTime( Date Time );
    void eventPrograms( String[] Names );
    void eventProgram( String Name, long ControlWord, int[] WaterBudget, X4smStartTime[] StartTimes, X4smSequence[] Sequences );
    void eventZones( String[] Names );
    void eventZonesStatus( String[] Names, boolean[] Status );
    void eventZone( String Name, int Channel, long ControlWord, int InitValue, int OffDelay );
    void eventActiveProgram( boolean Active, String Name, String Zone, String Status, int RunTime, int TimeToRun );
    void eventInputs( boolean[] Status );
    void eventReady();
}
