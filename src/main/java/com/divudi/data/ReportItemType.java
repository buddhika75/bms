/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.data;

/**
 *
 * @author Buddhika
 */
public enum ReportItemType {
    PatientName,
    PatientAge,
    PatientSex,
    InvestigationName,
    SampledTime,
    BilledDate,
    BilledTime,
    SampledDate,
    BillNo,
    BillItemNo,
    AutherizedSignature,
    DataEntrySignature,
    ReportedDate,
    ReportedTime,
    ReferringDoctor,
    ReferringInstitution,
    Speciman,
    CollectingCenter,
    NameInFull,
    Surname,
    NameWithInitials,
    Birthday,
    NicNo,
    Nationality,
    Religion,
    BloodGroup,
    Area,
    Phone,
    Mobile,
    Fax,
    CivilStatus,
    Category,
    Item,
    Person,
    Institution,
    DateTime,
    Barcode,
    DataEntered,
    PatientAgeOnBillDate,
    Phn,
    SampledAt,
    Ward,
    AutherizedName,
    AutherizedCode,
    AutherizedPosition,
    AutherizedQualification,
    DataEntryUserName,
    DataEntryUserDetails,
    ApprovedSignature,
    ApprovedUserName,
    ApprovedUserDetails,
    AutherizedUserName,
    AutherizedUserDetails,
    BarcodeBillId,
    BarcodeBillItemId,
    BarcodePatientId,
    BarcodePatientInvestigationId,
    BarcodePatientReportId,
    BHT,
    Comments,
    Css,;

    public String getLabel() {
        switch (this) {
            case AutherizedSignature:
                return "AutherizedSignature";
        }
        return null;
    }
}
