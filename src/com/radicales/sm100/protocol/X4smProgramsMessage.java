/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

import java.util.List;

/**
 *
 * @author JanZwiegers
 */
public class X4smProgramsMessage extends X4smMessage {

    private List<X4smAttribute> gAttributes;
    private String[] gNames;

    public X4smProgramsMessage( List<X4smAttribute> Attributes ) {
        super(MSG_TYPE_PROGRAMS);
        gAttributes = Attributes;
    }

    public void parse() throws X4smException  {

        for(X4smAttribute attr : gAttributes) {

            if(attr.Name.matches("Names")) {
                gNames = attr.Value.split(",");
            }

        }

    }

    public String[] getNames() {
        return gNames;
    }

}
