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
package com.radicales.sm100.device;

import java.util.Date;
import java.util.List;

/**
 * Smart Mist 100 Events Interface
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
public interface Sm100Event {
    void eventStatus( String Message );
    void eventProgramList( String[] Names );
    void eventProgramConfig( Sm100Program Program );
    void eventProgramsUpdate( List<Sm100Program> Programs );
    void eventProgramStartTimesUpdate( Sm100Program Program, List<StartTime> StartTimesList );
    void eventZonesUpdate( List<Sm100Zone> Zones );
    void eventInformation( String Name, String Family, String Revision, int Channels );
    void eventDataTime( Date Time );
    void eventZoneStatusUpdate( List<Sm100Zone> Zones );
    void eventActiveProgram( boolean Active, Sm100Program Program, Sm100Zone Zone, String Status, int RunTime, int TimeToRun );
    void eventInputs( boolean[] Status );
    void eventUploadComplete();
}
