/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.data;

/**
 *
 * @author Dushan
 */
public enum DocumentType {
    MedicalCertificate,
    DiagnosisCard,
    RequestLetter,
    Invoice,;
    
    public String getLabel() {
        switch (this) {
            case MedicalCertificate:
                return "Medical Certificate";
            case DiagnosisCard:
                return "Diagnosis Card";
            case RequestLetter:
                return "Request Letter";
            case Invoice:
                return "Invoice";
            default:
                return this.toString();
        }
    }
}
