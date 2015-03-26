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
package com.radicales.sm100.protocol;

import java.util.Date;

/**
 * XML For Smart Mist Events Interface
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
