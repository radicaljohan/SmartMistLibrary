package com.radicales.cal;

/**
 * CAL DriverEvent
 * Communication Abstraction Layer Driver Event Interface.
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
public interface CalDriverEvent {
    public void DriverEventRead( byte[] Buffer, int Length );
    public void DriverEventWrite( byte[] Buffer, int Lenght );
    public void DriverEventRun();
    public void DriverEventData();
    public void DriverEventNotify( int Id, int Value );
}
