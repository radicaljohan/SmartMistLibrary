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
public class X4smProgramMessage extends X4smMessage {

    private List<X4smAttribute> gAttributes;
    private String gName;
    private long gControlWord;
    private int[] gWaterBudget = new int[12];
    private X4smStartTime[] gStartTimes;
    private X4smSequence[] gSequences;

    /*
     * <Program
     * Name="Program F"
     * ControlWord="0x8000"
     * StartTimes="08:00"
     * WaterBudget="080,090,090,090,100,100,100,100,090,090,080,080" />
     *
     */
    public X4smProgramMessage( List<X4smAttribute> Attributes ) {
        super(MSG_TYPE_PROGRAM);
        gAttributes = Attributes;
        gStartTimes = new X4smStartTime[0];
        gSequences = new X4smSequence[0];
    }

    public void parse() throws X4smException  {

        for(X4smAttribute attr : gAttributes) {

            if(attr.Name.matches("Name")) {
                gName = attr.Value;
            }
            else if(attr.Name.matches("ControlWord")) {
                gControlWord = Long.decode(attr.Value);
            }
            else if(attr.Name.matches("StartTimes")) {
                String[] st = attr.Value.split(",");
                gStartTimes = new X4smStartTime[st.length];
                for(int i=0; i<st.length; i++) {
                    gStartTimes[i] = new X4smStartTime(0,0);
                    gStartTimes[i].parseTime(st[i]);
                }
            }
            else if(attr.Name.matches("WaterBudget")) {
                String[] ss = attr.Value.split(",");

                for(int i=0; (i<ss.length) && (i<12); i++) {
                    gWaterBudget[i] = Integer.parseInt(ss[i]);
                }
            }
            else if(attr.Name.matches("Sequence")) {
                String[] ss = attr.Value.split(",");
                gSequences = new X4smSequence[ss.length];
                for(int i=0; i<ss.length; i++) {
                    gSequences[i] = new X4smSequence(0,0);
                    gSequences[i].parseSequence(ss[i]);
                }
            }
        }
    }

    public String getName() {
        return gName;
    }

    public long getControlWord() {
        return gControlWord;
    }

    public int[] getWaterBudget() {
        return gWaterBudget;
    }

    public X4smStartTime[] getStartTimes() {
        return gStartTimes;
    }

    public X4smSequence[] getSequences() {
        return gSequences;
    }

}
