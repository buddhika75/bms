/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.data.lab;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author chrishantha
 */
public class TestOrderRecord {

    private String fieldDeliminator;
    private String repeatDeliminator;
    private String componentDeliminator;
    private String escapeDeliminator;
    private String recordString;

    private int fieldCount = 32;

    private String sampleId;
    private List<String> testToOrder;
    private Date sampledAt;
    private int frameNo;

    public TestOrderRecord() {
    }

    public TestOrderRecord(String fieldDeliminator, String repeatDeliminator, String componentDeliminator, String escapeDeliminator, String sampleId, int frameNo) {
        this.fieldDeliminator = fieldDeliminator;
        this.repeatDeliminator = repeatDeliminator;
        this.componentDeliminator = componentDeliminator;
        this.escapeDeliminator = escapeDeliminator;
        this.sampleId = sampleId;
        this.frameNo = frameNo;
    }

    private void createRecordString() {
        String ts = "";
        for (int i = 0; i < fieldCount; i++) {
            String fs;
            switch (i) {
                case 0:
                    fs = "O";
                    ts = ts + fs + fieldDeliminator;
                    break;
                case 1:
                    fs = frameNo + "";
                    ts = ts + fs + fieldDeliminator;
                    break;
                case 2:
                    fs = sampleId + "";
                    ts = ts + fs + componentDeliminator + "01" + fieldDeliminator;
                    break;
                case 4:
                    fs = createUniversalTextIdFromTestsToOrder();
                    ts = ts + fs + fieldDeliminator;
                    break;
                case 5:
                    fs = "R";
                    ts = ts + fs + fieldDeliminator;
                    break;
                case 7:
                    fs = createSampleDateTime();
                    ts = ts + fs + fieldDeliminator;
                    break;
                case 11:
                    fs = "A";
                    ts = ts + fs + fieldDeliminator;
                    break;
                case 15:
                    fs = "SERUM";
                    ts = ts + fs + fieldDeliminator;
                    break;
                case 25:
                    fs = "O";
                    ts = ts + fs + fieldDeliminator;
                    break;
                case 31:
                    fs = Character.toString((char) 13);
                    ts = ts + fs;
                    break;
                default:
                    fs = "";
                    ts = ts + fs + fieldDeliminator;
                    break;
            }

        }
        recordString = ts;
    }

    private String createSampleDateTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");//dd/MM/yyyy
        return sdfDate.format(sampledAt);
    }

    private String createUniversalTextIdFromTestsToOrder() {
        String ut = "";
        for (String tut : testToOrder) {
            ut = componentDeliminator + componentDeliminator + componentDeliminator + tut + repeatDeliminator;
        }
        ut = ut.substring(0, ut.length() - 1);
        return ut;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public void setFieldCount(int fieldCount) {
        this.fieldCount = fieldCount;
    }

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public List<String> getTestToOrder() {
        return testToOrder;
    }

    public void setTestToOrder(List<String> testToOrder) {
        this.testToOrder = testToOrder;
    }

    public Date getSampledAt() {
        return sampledAt;
    }

    public void setSampledAt(Date sampledAt) {
        this.sampledAt = sampledAt;
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

}
