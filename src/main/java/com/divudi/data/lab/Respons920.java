/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.data.lab;

import com.divudi.data.InvestigationItemType;
import com.divudi.entity.Patient;
import com.divudi.entity.lab.InvestigationItem;
import com.divudi.entity.lab.PatientSample;
import com.divudi.entity.lab.PatientSampleComponant;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * @author Dr. M H B Ariyaratne <buddhika.ari@gmail.com>
 */
public class Respons920 {

    private String inputStringBytesSpaceSeperatedR920;
    private String inputStringBytesPlusSeperated;
    private String inputStringCharactors;
    private List<Byte> bytesR920;
    private List<TestOrderRecord> testOrderRecords = new ArrayList<>();

    private String patientInformationRecord = null;
    private String testOrderRecord = null;
    private String resultRecord = null;
    private String commentRecord = null;
    private String requestInformationRecord = null;
    private String manufacturerInformationRecord = null;

    private String fieldDeliminator;
    private String repeatDeliminator;
    private String componentDeliminator;
    private String escapeDeliminator;

    private int fieldCount;
    private Map<Integer, String> requestFields;
    private Map<Integer, String> responseFields;

    private String responseString;

    private Boolean limsHasSamplesToSend;
    private Boolean limsFoundPatientInvestigationToEnterResults;

    private MessageType limsMessageType;
    private MessageSubtype limsMessageSubtype;
    private String limsPatientId;
    private String limsSampleId;
    private String limsSampleType;
    private Priority limsPriority;
    private List<String> limsTests;
    private PatientSample limsPatientSample;
    private List<PatientSampleComponant> limsPatientSampleComponants;

    private Patient patient;

    private Boolean toDeleteSampleRequest;

    private MessageType analyzerMessageType;
    private MessageSubtype analyzerMessageSubtype;
    private String analyzerPatientId;
    private String analyzerSampleId;
    private SampleTypeForDimension analyzerSampleType;
    private DimensionPriority analyzerPriority;
    private List<DimensionTestResult> analyzerTestResults;

    private String instrumentId;
    private Byte firstPollValue;
    private Byte requestValue;
    private String requestAcceptanceStatus;

    public void analyzeTheReceivedMessage() {
        textToByteArraySeperatedBySpaceR920();
        String astmPacket = byteArrayToStringsR920();
        String[] astmRecords = splitAstmPacketToAstmRecordsR920(astmPacket);
        int i = 0;
        for (String s : astmRecords) {
            i++;
        }
        boolean messageHeaderRecordExists = checkHeaderRecord(astmRecords[0]);
        if (!messageHeaderRecordExists) {
            responseString = "Error. Header Record Does NOT exists.";
            return;
        }
        boolean messageTerminatorRecordExists = checkTerminatorRecord(astmRecords[astmRecords.length - 2]);
        if (!messageTerminatorRecordExists) {
            responseString = "Error. Footer Record Does NOT exists.";
            return;
        }

        requestInformationRecord = extractRecordFromastmRecord(astmRecords, AstmRecordType.Request_Information_Record);

        if (requestInformationRecord != null) {
            processRequestInformationRecord(requestInformationRecord);
        }

    }

    private boolean checkHeaderRecord(String hr) {
        boolean he = false;
        String firstLetter = hr.substring(0, 1);
        if (hr.length() < 3) {
            return false;
        }
        String firstTwoLetters = hr.substring(0, 2);
        if (firstTwoLetters.toUpperCase().equals("1H")) {
            he = true;
            fieldDeliminator = hr.substring(2, 3);
            repeatDeliminator = hr.substring(3, 4);
            componentDeliminator = hr.substring(4, 5);
            escapeDeliminator = hr.substring(5, 6);
        } else if (firstLetter.toUpperCase().equals("H")) {
            he = true;
            fieldDeliminator = hr.substring(1, 2);
            repeatDeliminator = hr.substring(2, 3);
            componentDeliminator = hr.substring(3, 4);
            escapeDeliminator = hr.substring(4, 5);
        } else {
        }
        return he;
    }

    private boolean checkTerminatorRecord(String tr) {
        boolean he = false;
        String firstLetter = tr.substring(0, 1);
        if (firstLetter.toUpperCase().equals("L")) {
            he = true;
        }
        return he;
    }

    private String extractRecordFromastmRecord(String[] astmRecords, AstmRecordType t) {
        String temRecord = null;
        for (String ts : astmRecords) {
            if (ts != null && ts.length() > 1) {
                String firstLetter = ts.substring(0, 1);
                if (firstLetter.toUpperCase().equals("Q") && t == AstmRecordType.Request_Information_Record) {
                    temRecord = ts;
                } else if (firstLetter.toUpperCase().equals("R") && t == AstmRecordType.Result_Record) {

                } else if (firstLetter.toUpperCase().equals("O") && t == AstmRecordType.Test_Order_Record) {

                }
            }
        }
        return temRecord;
    }

    private String[] splitAstmPacketToAstmRecordsR920(String astmPacket) {
        return astmPacket.split("\\" + (Character.toString((char) 13)), -1);
    }

    private void processRequestInformationRecord(String prir) {
        RequestInformationRecord r = new RequestInformationRecord(fieldDeliminator, repeatDeliminator, componentDeliminator, escapeDeliminator, prir);
        int i = 0;
        for (String id : r.getIds()) {
            i++;
            TestOrderRecord tor = new TestOrderRecord(fieldDeliminator, repeatDeliminator, componentDeliminator, escapeDeliminator, id, i);
            testOrderRecords.add(tor);
        }
    }

    private void createTestOrderRequestForRequestInformationRecord() {
        String torss = "";
        for (TestOrderRecord temTor : testOrderRecords) {
            PatientInformationRecord pir
                    = new PatientInformationRecord(
                            fieldDeliminator,
                            repeatDeliminator,
                            componentDeliminator,
                            escapeDeliminator,
                            patient.getId(),
                            1,
                            patient.getPerson().getName(),
                            patient.getPerson().getSex().toString());
            torss += pir.getRecordString() + temTor.getRecordString();
        }
        String outputExceptChecksumAndEnd = createHeaderRecord() + torss + createTerminatorRecord() + Character.toString((char) 3);

        String checkSum = calculateChecksum(outputExceptChecksumAndEnd);
        responseString = outputExceptChecksumAndEnd + checkSum + createEnd();
        byte[] temByteArray = stringToByteArray(responseString);
        responseString = "";
        for (byte b : temByteArray) {
            responseString += b + "+";
        }
        responseString = responseString.substring(0, responseString.length() - 1);
    }

    public String createHeaderRecord() {
        String hr = "";
        hr = Character.toString((char) 2) + "1H" + fieldDeliminator + repeatDeliminator + componentDeliminator + escapeDeliminator
                + fieldDeliminator
                + fieldDeliminator
                + fieldDeliminator
                + fieldDeliminator
                + fieldDeliminator
                + fieldDeliminator
                + fieldDeliminator
                + fieldDeliminator
                + fieldDeliminator
                + fieldDeliminator
                + fieldDeliminator
                + fieldDeliminator
                + Character.toString((char) 13);
        String hs = "";
        for (int i = 0; i < 14; i++) {
            switch (i) {
                case 0:
                    hs += "1H";
                    break;
                case 1:
                    hs += fieldDeliminator + repeatDeliminator + componentDeliminator + escapeDeliminator+fieldDeliminator;
                    break;
                case 11:
                    hs += "P" + fieldDeliminator;
                    break;
                case 13:
                    String pattern = "yyyyMMddhhmmss";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    String date = simpleDateFormat.format(new Date());
                    hs += date + fieldDeliminator;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 12:
                case 14:
                    hs += fieldDeliminator;
                    break;

            }
        }
//        return hr;
        return hs;
    }

    public String createTerminatorRecord() {
        String tm = "";
        tm = "L" + fieldDeliminator + "1" + fieldDeliminator + "N" + Character.toString((char) 13);
        return tm;
    }

    public String createEnd() {
        String e = "";
//        e += Character.toString((char) 23);
        e += Character.toString((char) 13);
        e += Character.toString((char) 10);
        return e;
    }

    private void classifyMessage() {
        if (requestFields.size() < 2) {
            analyzerMessageType = MessageType.EmptyMessage;
            return;
        }
        String mt = requestFields.get(0).toUpperCase();
        if (mt == null) {
            analyzerMessageType = MessageType.EmptyMessage;
        } else if (mt.equalsIgnoreCase("P")) {
            analyzerMessageType = MessageType.Poll;
        } else if (mt.equalsIgnoreCase("D")) {
            analyzerMessageType = MessageType.SampleRequest;
        } else if (mt.equalsIgnoreCase("N")) {
            analyzerMessageType = MessageType.NoRequest;
        } else if (mt.equalsIgnoreCase("M")) {
            if (requestFields.size() == 3) {
                analyzerMessageType = MessageType.ResultAcceptance;
            } else if (requestFields.size() == 6) {
                analyzerMessageType = MessageType.RequestAcceptance;
            }
        } else if (mt.equalsIgnoreCase("I")) {
            if (requestFields.size() == 4) {
                analyzerMessageType = MessageType.EnhancedQueryMessage;
            } else if (requestFields.size() == 2) {
                analyzerMessageType = MessageType.QueryMessage;
            }
        } else if (mt.equalsIgnoreCase("R")) {
            analyzerMessageType = MessageType.ResultMessage;
        } else if (mt.equalsIgnoreCase("C")) {
            analyzerMessageType = MessageType.CaliberationResultMessage;
        } else {
            analyzerMessageType = MessageType.EmptyMessage;
        }
    }

    private void determineValues() {
        if (analyzerMessageType == MessageType.Poll) {
            instrumentId = requestFields.get(1);
            firstPollValue = getByte(requestFields.get(2));
            requestValue = getByte(requestFields.get(3));
        } else if (analyzerMessageType == MessageType.ResultAcceptance) {
            requestAcceptanceStatus = requestFields.get(1);
            //TODO: Reason for Rejection, Get Cup Positions
        } else if (analyzerMessageType == MessageType.RequestAcceptance) {
            requestAcceptanceStatus = requestFields.get(1);
            //TODO: Reason for Rejection, Get Cup Positions
        } else if (analyzerMessageType == MessageType.QueryMessage) {
            analyzerSampleId = requestFields.get(1);
        } else if (analyzerMessageType == MessageType.ResultMessage) {
            analyzerPatientId = requestFields.get(2);
            analyzerSampleId = requestFields.get(3);
            int resultsCount = Integer.parseInt(requestFields.get(10));
            analyzerTestResults = new ArrayList<>();
            for (int i = 0; i < resultsCount; i++) {
                int count = 11 + i * 4;
                DimensionTestResult tr = new DimensionTestResult();
                tr.setTestName(requestFields.get(count));
                tr.setTestResult(requestFields.get(count + 1));
                tr.setTestUnit(requestFields.get(count + 2));
                tr.setErrorCode(requestFields.get(count + 3));
                analyzerTestResults.add(tr);
            }
        }
    }

    private void determineMessageSubtype() {

        if (analyzerMessageType == MessageType.Poll) {
            if (firstPollValue == 1) {
                analyzerMessageSubtype = MessageSubtype.FirstPoll;
            } else {
                if (requestValue == 1) {
                    analyzerMessageSubtype = MessageSubtype.ConversationalPollReady;
                } else {
                    analyzerMessageSubtype = MessageSubtype.ConversationalPollBusy;
                }
            }
            return;
        } else if (analyzerMessageType == MessageType.RequestAcceptance) {
            if (requestAcceptanceStatus.equals("A")) {
                analyzerMessageSubtype = MessageSubtype.RequestAcceptanceSuccess;
            } else {
                analyzerMessageSubtype = MessageSubtype.RequestAcceptanceFailed;
            }
        }
    }

    private void createResponseFieldsForPollMessage() {
        responseFields = new HashMap<>();
        if (analyzerMessageSubtype == MessageSubtype.FirstPoll) {
            createNoSampleRequestMessage();
        } else if (analyzerMessageSubtype == MessageSubtype.ConversationalPollBusy) {
            createNoSampleRequestMessage();
            return;
        } else if (analyzerMessageSubtype == MessageSubtype.ConversationalPollReady) {
            if (limsHasSamplesToSend) {
//                toDeleteSampleRequest = false;
                createSampleRequestMessage();
            } else {
                createNoSampleRequestMessage();
            }
            return;
        }
    }

    private void createResponseFieldsForQueryMessage() {
        responseFields = new HashMap<>();
        if (limsHasSamplesToSend) {
            createSampleRequestMessage();
        } else {
            createNoSampleRequestMessage();
        }
        return;
    }

    private void createNoSampleRequestMessage() {
        limsMessageType = MessageType.SampleRequest;
        limsMessageSubtype = MessageSubtype.SampleRequestsNo;
        responseFields.put(0, "N");
    }

    private void createSampleRequestMessage(List<String> temSss) {
        if (temSss == null || temSss.isEmpty()) {
            return;
        }

        responseFields.put(4, limsPatientId);
        responseFields.put(5, limsSampleId);
        responseFields.put(6, analyzerSampleType.getFiledValue());
        responseFields.put(7, "");
        responseFields.put(8, analyzerPriority.getValue() + "");
        responseFields.put(9, "1");
        responseFields.put(10, "**");
        responseFields.put(11, "1");
        responseFields.put(12, limsTests.size() + "");
        int temTestCount = 13;
        for (String t : limsTests) {
            responseFields.put(temTestCount, t);
            temTestCount++;
        }

    }

    private void createSampleRequestMessage() {
        if (limsTests == null || limsTests.isEmpty()) {
            createNoSampleRequestMessage();
            return;
        }
        limsMessageType = MessageType.SampleRequest;
        limsMessageSubtype = MessageSubtype.SampleRequestYes;
        responseFields.put(0, "D");
        responseFields.put(1, "0");
        responseFields.put(2, "0");
        if (toDeleteSampleRequest) {
            responseFields.put(3, "D");
        } else {
            responseFields.put(3, "A");
        }
        responseFields.put(4, limsPatientId);
        responseFields.put(5, limsSampleId);
        responseFields.put(6, analyzerSampleType.getFiledValue());
        responseFields.put(7, "");
        responseFields.put(8, analyzerPriority.getValue() + "");
        responseFields.put(9, "1");
        responseFields.put(10, "**");
        responseFields.put(11, "1");
        responseFields.put(12, limsTests.size() + "");
        int temTestCount = 13;
        for (String t : limsTests) {
            responseFields.put(temTestCount, t);
            temTestCount++;
        }

    }

    private void convertSampleStringToSampleType() {
        if (limsSampleType == null) {
            analyzerSampleType = SampleTypeForDimension.One;
        } else if (limsSampleType.toLowerCase().contains("blood")) {
            analyzerSampleType = SampleTypeForDimension.W;
        } else if (limsSampleType.toLowerCase().contains("serum")) {
            analyzerSampleType = SampleTypeForDimension.One;
        } else if (limsSampleType.toLowerCase().contains("plasma")) {
            analyzerSampleType = SampleTypeForDimension.Two;
        } else if (limsSampleType.toLowerCase().contains("urine")) {
            analyzerSampleType = SampleTypeForDimension.Three;
        } else if (limsSampleType.toLowerCase().contains("csf")) {
            analyzerSampleType = SampleTypeForDimension.Four;
        }
    }

    public void createResponseString() {
        String temRs = "";
        if (getResponseFields().isEmpty()) {
            responseString = "";
            return;
        }
        for (int i = 0; i < responseFields.size(); i++) {
            temRs += responseFields.get(i) + (char) 28;
        }
        String checkSum = calculateChecksum(temRs);
        temRs = (char) 2 + temRs + checkSum + (char) 3;
        byte[] temRes = temRs.getBytes(StandardCharsets.US_ASCII);
        temRs = "";
        for (Byte b : temRes) {
            temRs += b + "+";
        }
        responseString = temRs;
    }

    public String calculateChecksum(String input, boolean replaceFieldSeperator) {
        String ip = input;
        String fs = (char) 28 + "";
        ip = ip.replaceAll("<FS>", fs);
        return calculateChecksum(ip);
    }

    public String calculateChecksum(String input) {
        byte[] temBytes = stringToByteArray(input);
        return calculateChecksum(temBytes);
    }

    public String calculateChecksum(byte[] bytes) {
        long checksum = 0;
        for (int i = 0; i < bytes.length; i++) {
            checksum += (bytes[i] & 0xffffffffL);
        }
        int integerChecksum = (int) checksum;
        String hexChecksum = Integer.toHexString(integerChecksum).toUpperCase();
        return hexChecksum.substring(Math.max(hexChecksum.length() - 2, 0));
    }

    private byte[] stringToByteArray(String s) {
        return s.getBytes();
    }

    public boolean isCorrectReport() {
        boolean flag = true;

        return true;
    }

    private void textToByteArraySeperatedByPlus() {
        bytesR920 = new ArrayList<>();
        String strInput = inputStringBytesPlusSeperated;
        String[] strByte = strInput.split(Pattern.quote("+"));
        for (String s : strByte) {
            try {
                Byte b = Byte.parseByte(s);
                bytesR920.add(b);
            } catch (Exception e) {
//                System.out.println("e = " + e);
                bytesR920.add(null);
            }
        }
    }

    private void textToByteArraySeperatedBySpaceR920() {
        bytesR920 = new ArrayList<>();
        String strInput = inputStringBytesSpaceSeperatedR920;
        String[] strByte = strInput.split("\\s+");
        for (String s : strByte) {
            try {
                Byte b = Byte.parseByte(s);
                bytesR920.add(b);
            } catch (Exception e) {
                bytesR920.add(null);
            }
        }
    }

    private Byte getByte(String input) {
        try {
            Byte b = Byte.parseByte(input);
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getInteger(String input) {
        try {
            Integer b = Integer.parseInt(input);
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    private Double getDouble(String input) {
        try {
            Double b = Double.parseDouble(input);
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    private void textToByteArrayByCharactors() {
        bytesR920 = new ArrayList<>();
        String strInput = inputStringCharactors;
        char[] strByte = strInput.toCharArray();
        for (char s : strByte) {
            try {
                Byte b = (byte) s;
                bytesR920.add(b);
            } catch (Exception e) {
//                System.out.println("e = " + e);
                bytesR920.add(null);
            }
        }
    }

    private List<Byte> trimMessage(List<Byte> temBytes) {
        List<Byte> trimmedBytes = new ArrayList<>();
        for (Byte b : temBytes) {
            if (b != 2) {
                if (b == 3) {
                    return trimmedBytes;
                }
                trimmedBytes.add(b);
            }
        }
        return trimmedBytes;
    }

    private String byteArrayToStringsR920() {
        List<Byte> temBytes = trimMessage(bytesR920);
        requestFields = new HashMap<>();
        String temStr = "";
        for (byte b : temBytes) {
            char c = (char) b;
            temStr += c;
        }
        return temStr;
    }

    public String addDecimalSeperator(String val) {
        String formatString = "#,###";
        Double dbl = Double.parseDouble(val);
        DecimalFormat formatter = new DecimalFormat(formatString);
        return formatter.format(dbl);
    }

    private String round(double value, int places) {
        String returnVal = "";
        if (places == 0) {
            returnVal = ((long) value) + "";
        } else if (places < 0) {
            long tn = (long) value;
            long pow = (long) Math.pow(10, Math.abs(places));
            tn = (tn / pow) * pow;
            returnVal = tn + "";
        } else {
            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            returnVal = bd.doubleValue() + "";
        }
        return returnVal;
    }

    public void prepareResponseForResultMessages() {
        responseFields = null;
        if (limsFoundPatientInvestigationToEnterResults = true) {
            createResultAcceptanceMessageFields();
        } else {
            createResultRejectMessageFields();
        }
    }

    public void prepareResponseForCaliberationResultMessages() {
        createResultAcceptanceMessageFields();
    }

    private void createResultAcceptanceMessageFields() {
        responseFields = new HashMap<>();
        getResponseFields().put(0, "M");
        getResponseFields().put(1, "A");
        getResponseFields().put(2, "");
    }

    private void createResultRejectMessageFields() {
        responseFields = new HashMap<>();
        getResponseFields().put(0, "M");
        getResponseFields().put(1, "R");
        getResponseFields().put(2, "1");
    }

    public void prepareResponseForPollMessages() {
        PatientSample temPs = this.limsPatientSample;
        if (temPs == null) {
            setLimsHasSamplesToSend(false);
        } else {
            setLimsHasSamplesToSend(true);
            setLimsSampleId(analyzerSampleId);
            setLimsPatientId(temPs.getPatient().getPhn());
            List<String> temSss = getTestsFromPatientSample();
            this.setLimsTests(temSss);
        }
        createResponseFieldsForPollMessage();
//        createResponseString();
    }

    public void prepareResponseForQueryMessages() {
        PatientSample temPs = this.limsPatientSample;
        if (temPs == null) {
            setLimsHasSamplesToSend(false);
        } else {
            setLimsHasSamplesToSend(true);
            setLimsSampleId(analyzerSampleId);
            String temName = temPs.getPatient().getPerson().getName() + "                              ";
            temName = temName.substring(0, 25);
            setLimsPatientId(temName);
            List<String> temSss = getTestsFromPatientSample();
            this.setLimsTests(temSss);
        }
        createResponseFieldsForQueryMessage();
//        createResponseString();
    }

    public List<String> getTestsFromPatientSample(List<PatientSampleComponant> tpsc, PatientSample temPs) {
        Set<String> temsss = new HashSet<>();
        List<String> temss = new ArrayList<>();
        if (tpsc == null) {
            return temss;
        }
        for (PatientSampleComponant c : tpsc) {
            for (InvestigationItem tii : c.getPatientInvestigation().getInvestigation().getReportItems()) {
                if (tii.getIxItemType() == InvestigationItemType.Value) {
                    if (tii.getSample() != null) {
                        this.setLimsSampleType(tii.getSample().getName());
                    }
                    if (tii.getItem().getPriority() != null) {
                        this.setLimsPriority(tii.getItem().getPriority());
                    } else {
                        this.setLimsPriority(Priority.Routeine);
                    }
                    if (tii.getItem().isHasMoreThanOneComponant()) {
                        if (tii.getTest() != null && !tii.getTest().getName().trim().equals("")) {
                            if (tii.getSampleComponent().equals(temPs.getInvestigationComponant())) {
                                temsss.add(tii.getTest().getCode());
                            }
                        }
                    } else {
                        if (tii.getTest() != null && !tii.getTest().getName().trim().equals("")) {
                            temsss.add(tii.getTest().getCode());
                        }
                    }
                }
            }

        }
        temss = new ArrayList<>(temsss);
        return temss;
    }

    public List<String> getTestsFromPatientSample() {
        Set<String> temsss = new HashSet<>();
        List<String> temss = new ArrayList<>();
        if (limsPatientSample == null) {
            return temss;
        }
        for (PatientSampleComponant c : limsPatientSampleComponants) {
            for (InvestigationItem tii : c.getPatientInvestigation().getInvestigation().getReportItems()) {
                if (tii.getIxItemType() == InvestigationItemType.Value) {
                    if (tii.getSample() != null) {
                        this.setLimsSampleType(tii.getSample().getName());
                    }
                    if (tii.getItem().getPriority() != null) {
                        this.setLimsPriority(tii.getItem().getPriority());
                    } else {
                        this.setLimsPriority(Priority.Routeine);
                    }
                    if (tii.getItem().isHasMoreThanOneComponant()) {
                        if (tii.getTest() != null && !tii.getTest().getName().trim().equals("")) {
                            if (tii.getSampleComponent().equals(limsPatientSample.getInvestigationComponant())) {
                                temsss.add(tii.getTest().getCode());
                            }
                        }
                    } else {
                        if (tii.getTest() != null && !tii.getTest().getName().trim().equals("")) {
                            temsss.add(tii.getTest().getCode());
                        }
                    }
                }
            }

        }
        temss = new ArrayList<>(temsss);
        return temss;
    }

    public String getInputStringBytesSpaceSeperatedR920() {
        return inputStringBytesSpaceSeperatedR920;
    }

    public void setInputStringBytesSpaceSeperatedR920(String inputStringBytesSpaceSeperatedR920) {
        this.inputStringBytesSpaceSeperatedR920 = inputStringBytesSpaceSeperatedR920;
    }

    public List<Byte> getBytesR920() {
        return bytesR920;
    }

    public void setBytesR920(List<Byte> bytesR920) {
        this.bytesR920 = bytesR920;
    }

    public String getInputStringBytesPlusSeperated() {
        return inputStringBytesPlusSeperated;
    }

    public void setInputStringBytesPlusSeperated(String inputStringBytesPlusSeperated) {
        this.inputStringBytesPlusSeperated = inputStringBytesPlusSeperated;
        textToByteArraySeperatedByPlus();
    }

    public String getInputStringCharactors() {
        return inputStringCharactors;
    }

    public void setInputStringCharactors(String inputStringCharactors) {
        this.inputStringCharactors = inputStringCharactors;
        textToByteArrayByCharactors();
    }

    public MessageType getAnalyzerMessageType() {
        return analyzerMessageType;
    }

    public void setAnalyzerMessageType(MessageType analyzerMessageType) {
        this.analyzerMessageType = analyzerMessageType;
    }

    public MessageSubtype getAnalyzerMessageSubtype() {
        return analyzerMessageSubtype;
    }

    public void setAnalyzerMessageSubtype(MessageSubtype analyzerMessageSubtype) {
        this.analyzerMessageSubtype = analyzerMessageSubtype;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public void setFieldCount(int fieldCount) {
        this.fieldCount = fieldCount;
    }

    public Map<Integer, String> getRequestFields() {
        return requestFields;
    }

    public void setRequestFields(Map<Integer, String> requestFields) {
        this.requestFields = requestFields;
    }

    public Map<Integer, String> getResponseFields() {
        if (responseFields == null) {
            responseFields = new HashMap<>();
        }
        return responseFields;
    }

    public void setResponseFields(Map<Integer, String> responseFields) {
        this.responseFields = responseFields;
    }

    public Boolean getLimsHasSamplesToSend() {
        return limsHasSamplesToSend;
    }

    public void setLimsHasSamplesToSend(Boolean limsHasSamplesToSend) {
        this.limsHasSamplesToSend = limsHasSamplesToSend;
    }

    public Boolean getToDeleteSampleRequest() {
        return toDeleteSampleRequest;
    }

    public void setToDeleteSampleRequest(Boolean toDeleteSampleRequest) {
        this.toDeleteSampleRequest = toDeleteSampleRequest;
    }

    public String getLimsPatientId() {
        return limsPatientId;
    }

    public void setLimsPatientId(String limsPatientId) {
        this.limsPatientId = limsPatientId;
    }

    public String getAnalyzerPatientId() {
        return analyzerPatientId;
    }

    public void setAnalyzerPatientId(String analyzerPatientId) {
        this.analyzerPatientId = analyzerPatientId;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public Byte getFirstPollValue() {
        return firstPollValue;
    }

    public void setFirstPollValue(Byte firstPollValue) {
        this.firstPollValue = firstPollValue;
    }

    public Byte getRequestValue() {
        return requestValue;
    }

    public void setRequestValue(Byte requestValue) {
        this.requestValue = requestValue;
    }

    public String getLimsSampleId() {
        return limsSampleId;
    }

    public void setLimsSampleId(String limsSampleId) {
        this.limsSampleId = limsSampleId;
    }

    public String getAnalyzerSampleId() {
        return analyzerSampleId;
    }

    public void setAnalyzerSampleId(String analyzerSampleId) {
        this.analyzerSampleId = analyzerSampleId;
    }

    public String getLimsSampleType() {
        return limsSampleType;
    }

    public void setLimsSampleType(String limsSampleType) {
        this.limsSampleType = limsSampleType;
        convertSampleStringToSampleType();
    }

    public SampleTypeForDimension getAnalyzerSampleType() {
        return analyzerSampleType;
    }

    public void setAnalyzerSampleType(SampleTypeForDimension analyzerSampleType) {
        this.analyzerSampleType = analyzerSampleType;
    }

    public DimensionPriority getAnalyzerPriority() {
        return analyzerPriority;
    }

    public void setAnalyzerPriority(DimensionPriority analyzerPriority) {
        this.analyzerPriority = analyzerPriority;
    }

    public Priority getLimsPriority() {
        return limsPriority;
    }

    public void setLimsPriority(Priority limsPriority) {
        this.limsPriority = limsPriority;
        if (limsPriority == null) {
            limsPriority = Priority.Routeine;
        }
        switch (limsPriority) {
            case Asap:
                analyzerPriority = DimensionPriority.Two;
                break;
            case Stat:
                analyzerPriority = DimensionPriority.One;
                break;
            case Routeine:
                analyzerPriority = DimensionPriority.Zero;
                break;
            default:
                analyzerPriority = DimensionPriority.Zero;
        }
    }

    public List<String> getLimsTests() {
        return limsTests;
    }

    public void setLimsTests(List<String> limsTests) {
        this.limsTests = limsTests;
    }

    public String getResponseString() {
        createTestOrderRequestForRequestInformationRecord();
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    public String getRequestAcceptanceStatus() {
        return requestAcceptanceStatus;
    }

    public void setRequestAcceptanceStatus(String requestAcceptanceStatus) {
        this.requestAcceptanceStatus = requestAcceptanceStatus;
    }

    public MessageType getLimsMessageType() {
        return limsMessageType;
    }

    public void setLimsMessageType(MessageType limsMessageType) {
        this.limsMessageType = limsMessageType;
    }

    public MessageSubtype getLimsMessageSubtype() {
        return limsMessageSubtype;
    }

    public void setLimsMessageSubtype(MessageSubtype limsMessageSubtype) {
        this.limsMessageSubtype = limsMessageSubtype;
    }

    public PatientSample getLimsPatientSample() {
        return limsPatientSample;
    }

    public void setLimsPatientSample(PatientSample limsPatientSample) {
        this.limsPatientSample = limsPatientSample;
    }

    public List<PatientSampleComponant> getLimsPatientSampleComponants() {
        return limsPatientSampleComponants;
    }

    public void setLimsPatientSampleComponants(List<PatientSampleComponant> limsPatientSampleComponants) {
        this.limsPatientSampleComponants = limsPatientSampleComponants;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Boolean getLimsFoundPatientInvestigationToEnterResults() {
        return limsFoundPatientInvestigationToEnterResults;
    }

    public void setLimsFoundPatientInvestigationToEnterResults(Boolean limsFoundPatientInvestigationToEnterResults) {
        this.limsFoundPatientInvestigationToEnterResults = limsFoundPatientInvestigationToEnterResults;
    }

    public List<DimensionTestResult> getAnalyzerTestResults() {
        return analyzerTestResults;
    }

    public void setAnalyzerTestResults(List<DimensionTestResult> analyzerTestResults) {
        this.analyzerTestResults = analyzerTestResults;
    }

    public String getPatientInformationRecord() {
        return patientInformationRecord;
    }

    public void setPatientInformationRecord(String patientInformationRecord) {
        this.patientInformationRecord = patientInformationRecord;
    }

    public String getTestOrderRecord() {
        return testOrderRecord;
    }

    public void setTestOrderRecord(String testOrderRecord) {
        this.testOrderRecord = testOrderRecord;
    }

    public String getResultRecord() {
        return resultRecord;
    }

    public void setResultRecord(String resultRecord) {
        this.resultRecord = resultRecord;
    }

    public String getCommentRecord() {
        return commentRecord;
    }

    public void setCommentRecord(String commentRecord) {
        this.commentRecord = commentRecord;
    }

    public String getRequestInformationRecord() {
        return requestInformationRecord;
    }

    public void setRequestInformationRecord(String requestInformationRecord) {
        this.requestInformationRecord = requestInformationRecord;
    }

    public String getManufacturerInformationRecord() {
        return manufacturerInformationRecord;
    }

    public void setManufacturerInformationRecord(String manufacturerInformationRecord) {
        this.manufacturerInformationRecord = manufacturerInformationRecord;
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

    public List<TestOrderRecord> getTestOrderRecords() {
        return testOrderRecords;
    }

    public void setTestOrderRecords(List<TestOrderRecord> testOrderRecords) {
        this.testOrderRecords = testOrderRecords;
    }

}
