/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.data.lab;

import java.util.List;

/**
 *
 * @author chrishantha
 */
public class PatientInformationRecord {

    private String fieldDeliminator;
    private String repeatDeliminator;
    private String componentDeliminator;
    private String escapeDeliminator;
    private String recordString;
    private String patientName;
    private String patientSex;
    private Long patientId;
    private  List<String>  testToOrder;
    private int frameNo;    
    
   

    private int fieldCount = 36;



    public PatientInformationRecord() {
    }

    public PatientInformationRecord(String fieldDeliminator, 
            String repeatDeliminator, 
            String componentDeliminator, 
            String escapeDeliminator,
            Long patientId, 
            int frameNo,
            String patientName,
            String patientSex) {
        this.fieldDeliminator = fieldDeliminator;
        this.repeatDeliminator = repeatDeliminator;
        this.componentDeliminator = componentDeliminator;
        this.escapeDeliminator = escapeDeliminator;
        this.patientId = patientId;
        this.frameNo = frameNo;
        this.patientName = patientName;
        this.patientSex = patientSex;
    }
   
    
    
    
    private void createRecordString() {
        String ts = "";
        for (int i = 0; i < fieldCount; i++) {
            String fs = "";
            switch (i) {
                case 0:
                    fs = "P";
                    ts  = ts + fs + fieldDeliminator ;
                    break;
                case 1:
                    fs = frameNo + "";
                    ts  = ts + fs + fieldDeliminator ;
                    break;
                case 2:
                    fs = patientId + "";
                    ts  = ts + fs + fieldDeliminator ;
                    break;
                case 5:
                    fs = patientName;
                    ts = ts + fs + fieldDeliminator;
                    break;
                case 8:
                    if(patientSex.toLowerCase().contains("m")){
                        fs = "M";
                    }else{
                        fs = "F";
                    }
                    ts = ts + fs + fieldDeliminator;
                    break;
                case 35:
                    fs = Character.toString ((char)13);
                    ts  = ts + fs ;
                    break;
                default:
                    fs="";
                    ts  = ts + fs + fieldDeliminator ;
                    break;
            }
            
        }
        recordString = ts;
    }

   
    public int getFieldCount() {
        return fieldCount;
    }

    public void setFieldCount(int fieldCount) {
        this.fieldCount = fieldCount;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public List<String> getTestToOrder() {
        return testToOrder;
    }

    public void setTestToOrder( List<String>  testToOrder) {
        this.testToOrder = testToOrder;
    }

    public String getFieldDeliminator() {
        return fieldDeliminator;
    }

    public void setFieldDeliminator(String fieldDeliminator) {
        this.fieldDeliminator = fieldDeliminator;
    }

    public String getRepeatDeliminator() {
        return repeatDeliminator;
    }

    public void setRepeatDeliminator(String repeatDeliminator) {
        this.repeatDeliminator = repeatDeliminator;
    }

    public String getComponentDeliminator() {
        return componentDeliminator;
    }

    public void setComponentDeliminator(String componentDeliminator) {
        this.componentDeliminator = componentDeliminator;
    }

    public String getEscapeDeliminator() {
        return escapeDeliminator;
    }

    public void setEscapeDeliminator(String escapeDeliminator) {
        this.escapeDeliminator = escapeDeliminator;
    }

    public String getRecordString() {
        createRecordString();
        return recordString;
    }

    public void setRecordString(String recordString) {
        this.recordString = recordString;
    }

    public int getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(int frameNo) {
        this.frameNo = frameNo;
    }

    public String getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(String patientSex) {
        this.patientSex = patientSex;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    

}
