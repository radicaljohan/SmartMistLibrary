/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

import java.util.List;

/**
 *
 * @author JanZwiegers
 */
public class X4smInputsMessage extends X4smMessage {

    private final List<X4smAttribute> gAttributes;
    private boolean[] gStatus;

    public X4smInputsMessage( List<X4smAttribute> Attributes ) {
        super(MSG_TYPE_INPUTS);
        gAttributes = Attributes;

    }

    public void parse() throws X4smException  {

        for(X4smAttribute attr : gAttributes) {

            if(attr.Name.matches("Status")) {
                String[] sa = attr.Value.split(",");
                gStatus = new boolean[sa.length];
                for(int i=0; i<sa.length; i++) {
                    gStatus[i] = sa[i].matches("On");
                }
            }
        }
    }

    public boolean[] getStatus() {
        return gStatus;
    }


}
