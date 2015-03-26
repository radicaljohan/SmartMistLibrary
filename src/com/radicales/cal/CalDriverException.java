package com.radicales.cal;

/**
 * CAL DriverException
 * Communication Abstraction Layer Driver Exception Default Object
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
public class CalDriverException extends Exception {
    private static final long serialVersionUID = 2489655943904721322L;

    public CalDriverException( String Message ) {
        super(Message);
        //System.out.println("DriverException:" + Message);
    }
}
