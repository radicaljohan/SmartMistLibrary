/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.device;

import java.util.Date;
import java.util.List;

/**
 *
 * @author JanZwiegers
 */
public interface Sm100Event {
    void eventStatus( String Message );
    void eventProgramList( String[] Names );
    void eventProgramConfig( Sm100Program Program );
    void eventProgramUpdate( List<Sm100Program> Programs );
    void eventZoneUpdate( List<Sm100Zone> Zones );
    void eventInformation( String Name, String Family, String Revision, int Channels );
    void eventDataTime( Date Time );
    void eventZoneStatusUpdate( List<Sm100Zone> Zones );
    void eventActiveProgram( boolean Active, Sm100Program Program, Sm100Zone Zone, String Status, int RunTime, int TimeToRun );
    void eventInputs( boolean[] Status );
    void eventUploadComplete();
}
