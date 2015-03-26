/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radicales.sm100.protocol;

/**
 *
 * @author JanZwiegers
 */
public class X4smRemoveSetupMessage extends X4smMessage {

    public X4smRemoveSetupMessage() {
        super(X4smMessage.MSG_TYPE_REMOVESETUP);
        encode();
    }

    private void encode() {
        String msg = "<RemoveSetup />";
        setData(msg);
    }

}
