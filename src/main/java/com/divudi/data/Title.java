/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.data;

/**
 *
 * @author Dr. M H B Ariyaratne <buddhika.ari at gmail.com>
 */
public enum Title {

    Mr,
    Mrs,
    Miss,
    Ms,
    Master,
    Baby,
    Rev,
    RtRev,
    Hon,
    RtHon,
    Dr,
    DrMrs,
    DrMs,
    DrMiss,
    Prof,
    ProfMs,
    ProfMrs,
    ProfMiss,
    Other,
    Baby_Of;

    public String getLabel() {
        switch (this) {
            case Baby_Of:
                return "Baby of ";
            case DrMiss:
                return "Dr.(Miss) ";
            case DrMrs:
                return "Dr.(Mrs.) ";
            case DrMs:
                return "Dr.(Ms.) ";
            case Hon:
                return "Hon. ";
            case Dr:
                return "Dr. ";
            case Mr:
                return "Mr. ";
            case Miss:
                return "Miss. ";
            case Mrs:
                return "Mrs. ";
            case Ms:
                return "Ms. ";
            case Prof:
                return "Prof. ";
            case Rev:
                return "Rev. ";
            case RtHon:
                return "Rt. Hon. ";
            case RtRev:
                return "Rt. Rev. ";
            case ProfMs:
                return "Prof.(Ms)";
            case ProfMrs:
                return "Prof.(Mrs)";
            case ProfMiss:
                return "Prof.(Miss)";
            case Other:
                return "Other";
            default:
                return this.toString();

        }
    }
}