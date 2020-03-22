/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.data.lab;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chrishantha
 */
public class RequestInformationRecord {

    private String fieldDeliminator;
    private String repeatDeliminator;
    private String componentDeliminator;
    private String escapeDeliminator;
    private String recordString;
    private List<String> ids;

    public RequestInformationRecord() {
    }

    public RequestInformationRecord(String fieldDeliminator, String repeatDeliminator, String componentDeliminator, String escapeDeliminator, String recordString) {
        this.fieldDeliminator = fieldDeliminator;
        this.repeatDeliminator = repeatDeliminator;
        this.componentDeliminator = componentDeliminator;
        this.escapeDeliminator = escapeDeliminator;
        this.recordString = recordString;
    }

    private void messageToIds() {
        String message;
        String messageFields[];
        String sampleIdAndPtNoField;
        String sampleAndPtIdNumbers[];
        String sampleIdNumbers[];
        message = recordString;
        messageFields = message.split("\\" + fieldDeliminator, -1);
        int i = 0;
        for (String mf : messageFields) {
            i++;
        }
        sampleIdAndPtNoField = messageFields[2];
        sampleAndPtIdNumbers = sampleIdAndPtNoField.split("\\" + repeatDeliminator, -1);
        sampleIdNumbers = new String[sampleAndPtIdNumbers.length];
        i = 0;
        for (String sampleAndPtId : sampleAndPtIdNumbers) {
            String[] ptSid = sampleAndPtId.split("\\" + componentDeliminator, -1);
            sampleIdNumbers[i] = ptSid[1];

        }
        for (String c : sampleIdNumbers) {
            try {
//                Long id = Long.parseLong(c);
                ids.add(c);
            } catch (NumberFormatException e) {
            }
        }
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
        return recordString;
    }

    public void setRecordString(String recordString) {
        this.recordString = recordString;
    }

    public List<String> getIds() {
        if (ids == null) {
            ids = new ArrayList<>();
        }
        messageToIds();
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

}
