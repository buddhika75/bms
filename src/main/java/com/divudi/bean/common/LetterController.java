package com.divudi.bean.common;

import com.divudi.data.DocumentType;
import com.divudi.data.PojoFunction;
import com.divudi.data.Title;
import com.divudi.data.table.String1Value1;
import com.divudi.entity.Doctor;
import com.divudi.entity.Document;
import com.divudi.entity.Patient;
import com.divudi.entity.PatientEncounter;
import com.divudi.entity.clinical.ClinicalFindingValue;
import com.divudi.facade.DocumentFacade;
import com.divudi.facade.PatientFacade;
import com.divudi.facade.util.JsfUtil;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.TemporalType;

/**
 *
 * @author Dushan
 */
@Named(value = "letterController")
@SessionScoped
public class LetterController implements Serializable {

    @EJB
    PatientFacade patientFacade;
    @EJB
    DocumentFacade documentFacade;

    @Inject
    SessionController sessionController;
    @Inject
    CommonFunctionsController commonFunctionsController;

    Date fromDate;
    Date toDate;
    Patient patient;
    PatientEncounter patientEncounter;
    Doctor doctor;
    boolean bool1;
    DocumentType documentType;

    String hx;
    String ex;
    String lx;
    String rx;

    Document document;

    List<Document> documents;

    public void clearAll() {
        patient = new Patient();
        patientEncounter = new PatientEncounter();
        doctor = new Doctor();
        bool1 = false;
        documentType = null;
        document = new Document();
        documents = new ArrayList<>();
    }

    public void fetchMedicalCertificate() {
        document = new Document();
        document = fetchDocument(DocumentType.MedicalCertificate);
    }
    
    public void newMedicalCertificate() {
        document = new Document();
        document = newDocument(DocumentType.MedicalCertificate);
    }

    public void fetchInvoice() {
        document = new Document();
        document = fetchDocument(DocumentType.Invoice);
    }

    public void fetchDiagnosisCard() {
        document = new Document();
        document = fetchDocument(DocumentType.DiagnosisCard);
    }

    public void fetchRequestLetter() {
        document = new Document();
        document = fetchDocument(DocumentType.RequestLetter);
    }

    public void newRequestLetter() {
        document = new Document();
        document = newDocument(DocumentType.RequestLetter);
    }
    
    public String viewPatientLetter() {
        patient = getPatientEncounter().getPatient();
        fetchAllLetters();
        return "clinical_letter_search";
    }

    public void fetchAllLetters() {
        if (patient == null) {
            JsfUtil.addErrorMessage("Please Enter Patient Name");
            return;
        }
        documents = fetchDocuments(documentType, null, patient, bool1, fromDate, toDate);
    }

    public String viewDocument(Document d) {
        document = d;
        return "clinical_letter_all";
    }
    
    public String editDocument(Document d) {
        document = d;
        return "clinical_letter_request_letter";
    }

    private Document newDocument(DocumentType dt) {
        Document d = new Document();

        d = new Document();
        d.setVisit(getPatientEncounter());
        d.setPatient(getPatientEncounter().getPatient());
        d.setDate(getPatientEncounter().getCreatedAt());
        d.setFromDate(new Date());
        d.setToDate(new Date());
        d.setDocumentType(dt);

        if (dt == DocumentType.MedicalCertificate || dt == DocumentType.DiagnosisCard || dt == DocumentType.Invoice) {
            if (getPatientEncounter().getDiagnosis() != null) {
                for (ClinicalFindingValue dx : getPatientEncounter().getDiagnosis()) {
                    if (d.getDiagnosis() == null) {
                        d.setDiagnosis("");
                    }
                    d.setDiagnosis(d.getDiagnosis() + dx.getStringValue());
                }
            }
        }

        if (dt == DocumentType.DiagnosisCard) {
            d.setHpc(hx);
            d.setExaminationFindings(ex);
            d.setTreatmentsGiven(rx);
        }

        if (dt == DocumentType.Invoice) {
            d.setTreatmentsGiven(rx);
        }

        if (dt == DocumentType.RequestLetter) {
            String s = "This " + getPatientEncounter().getPatient().getPerson().getAge()
                    + " old " + getPatientEncounter().getPatient().getPerson().getNameWithTitle()
                    + " Presented with ......... For days / weeks duration. "
                    + " O/E Found to have ......... Please see and do the "
                    + " needful/ arrange Ultrasound scanning of "
                    + " Abdomen/Pelvis/KUB on this patient.";
            d.setDiagnosis(s);
            d.setHeader(getLetterHeader());
        }
        return d;

    }

    private Document fetchDocument(DocumentType dt) {
        Document d = new Document();

        String sql;
        Map m = new HashMap();

        sql = " select d from Document d where "
                + " d.retired=false "
                + " and d.visit=:pe "
                + " and d.documentType=:dt ";
        m.put("pe", getPatientEncounter());
        m.put("dt", dt);

        d = getDocumentFacade().findFirstBySQL(sql, m);
        if (d != null && d.getId() != null) {
            return d;
        } else {
            d = new Document();
            d.setVisit(getPatientEncounter());
            d.setPatient(getPatientEncounter().getPatient());
            d.setDate(getPatientEncounter().getCreatedAt());
            d.setFromDate(new Date());
            d.setToDate(new Date());
            d.setDocumentType(dt);

            if (dt == DocumentType.MedicalCertificate || dt == DocumentType.DiagnosisCard || dt == DocumentType.Invoice) {
                if (getPatientEncounter().getDiagnosis() != null) {
                    for (ClinicalFindingValue dx : getPatientEncounter().getDiagnosis()) {
                        if (d.getDiagnosis() == null) {
                            d.setDiagnosis("");
                        }
                        d.setDiagnosis(d.getDiagnosis() + dx.getStringValue());
                    }
                }
            }

            if (dt == DocumentType.DiagnosisCard) {
                d.setHpc(hx);
                d.setExaminationFindings(ex);
                d.setTreatmentsGiven(rx);
            }

            if (dt == DocumentType.Invoice) {
                d.setTreatmentsGiven(rx);
            }

            if (dt == DocumentType.RequestLetter) {
                String s = "This " + getPatientEncounter().getPatient().getPerson().getAge()
                        + " old " + getPatientEncounter().getPatient().getPerson().getNameWithTitle()
                        + " Presented with ......... For days / weeks duration. "
                        + " O/E Found to have ......... Please see and do the "
                        + " needful/ arrange Ultrasound scanning of "
                        + " Abdomen/Pelvis/KUB on this patient.";
                d.setDiagnosis(s);
                d.setHeader(getLetterHeader());
            }
            return d;
        }
    }

    private List<Document> fetchDocuments(DocumentType dt, PatientEncounter pe, Patient p, boolean bool, Date fd, Date td) {
        List<Document> documents = new ArrayList<>();

        String sql;
        Map m = new HashMap();

        sql = " select d from Document d where "
                + " d.retired=false ";

        if (dt != null) {
            sql += " and d.documentType=:dt ";
            m.put("dt", dt);
        }

        if (pe != null) {
            sql += " and d.visit=:pe ";
            m.put("pe", getPatientEncounter());
        }
        if (p != null) {
            sql += " and d.patient=:p ";
            m.put("p", p);
        }

        if (bool) {
            if (fd != null && td != null) {
                sql += " and d.visit.createdAt between :fd and :td ";
                m.put("fd", fd);
                m.put("td", td);
            }
        }

        documents = getDocumentFacade().findBySQL(sql, m, TemporalType.TIMESTAMP);

        return documents;
    }

    public void saveDocument() {
        if (getDocument().getId() == null) {
            getDocument().setCreatedAt(new Date());
            getDocument().setCreater(getSessionController().getLoggedUser());
            getDocumentFacade().create(getDocument());
            JsfUtil.addSuccessMessage("Saved");
        } else {
            getDocument().setEditer(getSessionController().getLoggedUser());
            getDocument().setEditedAt(new Date());
            getDocumentFacade().edit(getDocument());
            JsfUtil.addSuccessMessage("Updated");
        }
    }

    public void listnerDateChange() {
        int days = 0;
        try {
            days = Integer.valueOf(getDocument().getNoOfDays());
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Please Enter Correct Date.");
            return;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDocument().getFromDate());
        cal.set(Calendar.HOUR, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.add(Calendar.DATE, days);
        cal.add(Calendar.SECOND, -1);
        getDocument().setToDate(cal.getTime());
    }

    public LetterController() {
    }

    public String getValueInWords(double d) {
        String s = PojoFunction.doubleToText(d).toUpperCase();
        return s;
    }

    public void listnerHeader() {
        if (getDocument() != null && getDocument().getId() == null) {
            getDocument().setHeader(getLetterHeader());
        }
    }

    public String getLetterHeader() {
        if (getDocument() == null) {
            return "Dear Sir / Madam,";
        }
        if (getDocument().getDoctor() != null) {
            if (getDocument().getDoctor().getPerson().getTitle() != null) {
                if (getDocument().getDoctor().getPerson().getTitle() == Title.Dr
                        || getDocument().getDoctor().getPerson().getTitle() == Title.Prof) {
                    return "Dear Sir,";

                } else if (getDocument().getDoctor().getPerson().getTitle() == Title.DrMiss
                        || getDocument().getDoctor().getPerson().getTitle() == Title.DrMs
                        || getDocument().getDoctor().getPerson().getTitle() == Title.DrMrs
                        || getDocument().getDoctor().getPerson().getTitle() == Title.ProfMiss
                        || getDocument().getDoctor().getPerson().getTitle() == Title.ProfMs
                        || getDocument().getDoctor().getPerson().getTitle() == Title.ProfMrs) {
                    return "Dear Madam,";
                } else {
                    return "Dear Sir / Madam,";
                }
            } else {
                return "Dear Sir / Madam,";
            }
        } else {
            return "Dear Sir / Madam,";
        }
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public PatientEncounter getPatientEncounter() {
        return patientEncounter;
    }

    public void setPatientEncounter(PatientEncounter patientEncounter) {
        this.patientEncounter = patientEncounter;
        if (patientEncounter != null) {
            setPatient(patientEncounter.getPatient());
            String c = getPatientEncounter().getComments();
            int[] arr = new int[4];
            List<String1Value1> list = new ArrayList<>();
            arr[0] = c.lastIndexOf(getSessionController().getUserPreference().getAbbreviationForHistory());
            arr[1] = c.lastIndexOf(getSessionController().getUserPreference().getAbbreviationForExamination());
            arr[2] = c.lastIndexOf(getSessionController().getUserPreference().getAbbreviationForInvestigations());
            arr[3] = c.lastIndexOf(getSessionController().getUserPreference().getAbbreviationForTreatments());
            System.out.println("intHx = " + arr[0]);
            System.out.println("intEx = " + arr[1]);
            System.out.println("intIx = " + arr[2]);
            String s;
            if (arr[0] > -1) {
                String1Value1 sv = new String1Value1();
                sv.setString("Hx");
                sv.setValue(arr[0]);
                list.add(sv);
            }
            if (arr[1] > -1) {
                String1Value1 sv = new String1Value1();
                sv.setString("Ex");
                sv.setValue(arr[1]);
                if (list.isEmpty()) {
                    list.add(sv);
                } else {
                    List<String1Value1> temp = new ArrayList<>();
                    boolean b = false;
                    for (String1Value1 ss : list) {
                        if (!b) {
                            if (arr[1] < ss.getValue()) {
                                temp.add(sv);
                                b = true;
                                temp.add(ss);
                            } else {
                                temp.add(ss);
                            }
                        } else {
                            temp.add(ss);
                        }
                    }
                    if (!b) {
                        temp.add(sv);
                    }
                    list = temp;
                }
            }
            for (String1Value1 ss : list) {
            }
//            System.out.println("2.list.size() = " + list.size());
            if (arr[2] > -1) {
                String1Value1 sv = new String1Value1();
                sv.setString("Ix");
                sv.setValue(arr[2]);
                if (list.isEmpty()) {
                    list.add(sv);
                } else {
                    List<String1Value1> temp = new ArrayList<>();
                    boolean b = false;
                    for (String1Value1 ss : list) {
//                        System.out.println("ss.getString() = " + ss.getString());
//                        System.out.println("ss.getValue() = " + ss.getValue());
                        if (!b) {
                            if (arr[2] < ss.getValue()) {
                                temp.add(sv);
                                b = true;
                                temp.add(ss);
                            } else {
                                temp.add(ss);
                            }
                        } else {
                            temp.add(ss);
                        }
                    }
                    if (!b) {
                        temp.add(sv);
                    }
//                    System.out.println("2.temp.size() = " + temp.size());
                    list = temp;
                }
            }
//            System.out.println("3.list.size() = " + list.size());
            if (arr[3] > -1) {
                String1Value1 sv = new String1Value1();
                sv.setString("Rx");
                sv.setValue(arr[3]);
                if (list.isEmpty()) {
                    list.add(sv);
                } else {
                    List<String1Value1> temp = new ArrayList<>();
                    boolean b = false;
                    for (String1Value1 ss : list) {
//                        System.out.println("ss.getString() = " + ss.getString());
//                        System.out.println("ss.getValue() = " + ss.getValue());
                        if (!b) {
                            if (arr[3] < ss.getValue()) {
                                temp.add(sv);
                                b = true;
                                temp.add(ss);
                            } else {
                                temp.add(ss);
                            }
                        } else {
                            temp.add(ss);
                        }
                    }
                    if (!b) {
                        temp.add(sv);
                    }
//                    System.out.println("3.temp.size() = " + temp.size());
                    list = temp;
                }
            }
//            System.out.println("4.list.size() = " + list.size());
            int i = 1;
            for (String1Value1 ss : list) {
                //System.out.println("ss.getString() = " + ss.getString());
                //System.out.println("ss.getString() = " + ss.getValue());
                if (i < list.size()) {
                    if (ss.getString().toUpperCase().equals("HX")) {
                        hx = c.substring((int) ss.getValue(), (int) list.get(i).getValue());
                        hx = hx.substring(2);
                    }
                    if (ss.getString().toUpperCase().equals("EX")) {
                        ex = c.substring((int) ss.getValue(), (int) list.get(i).getValue());
                        ex = ex.substring(2);
                    }
                    if (ss.getString().toUpperCase().equals("IX")) {
                        lx = c.substring((int) ss.getValue(), (int) list.get(i).getValue());
                        lx = lx.substring(2);
                    }
                    if (ss.getString().toUpperCase().equals("RX")) {
                        rx = c.substring((int) ss.getValue(), (int) list.get(i).getValue());
                        rx = rx.substring(2);
                    }
                } else {
                    if (ss.getString().toUpperCase().equals("HX")) {
                        hx = c.substring((int) ss.getValue());
                        hx = hx.substring(2);
                    }
                    if (ss.getString().toUpperCase().equals("EX")) {
                        ex = c.substring((int) ss.getValue());
                        ex = ex.substring(2);
                    }
                    if (ss.getString().toUpperCase().equals("IX")) {
                        lx = c.substring((int) ss.getValue());
                        lx = lx.substring(2);
                    }
                    if (ss.getString().toUpperCase().equals("RX")) {
                        rx = c.substring((int) ss.getValue());
                        rx = rx.substring(2);
                    }
                }

                i++;
            }
//            setDate(patientEncounter.getCreatedAt());
        }
    }

    public PatientFacade getPatientFacade() {
        return patientFacade;
    }

    public void setPatientFacade(PatientFacade patientFacade) {
        this.patientFacade = patientFacade;
    }

    public DocumentFacade getDocumentFacade() {
        return documentFacade;
    }

    public void setDocumentFacade(DocumentFacade documentFacade) {
        this.documentFacade = documentFacade;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public Date getFromDate() {
        if (fromDate == null) {
            fromDate = commonFunctionsController.getStartOfDay(new Date());
        }
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        if (toDate == null) {
            toDate = commonFunctionsController.getEndOfDay(new Date());
        }
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public boolean isBool1() {
        return bool1;
    }

    public void setBool1(boolean bool1) {
        this.bool1 = bool1;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public CommonFunctionsController getCommonFunctionsController() {
        return commonFunctionsController;
    }

    public void setCommonFunctionsController(CommonFunctionsController commonFunctionsController) {
        this.commonFunctionsController = commonFunctionsController;
    }

    public String getHx() {
        return hx;
    }

    public void setHx(String hx) {
        this.hx = hx;
    }

    public String getEx() {
        return ex;
    }

    public void setEx(String ex) {
        this.ex = ex;
    }

    public String getLx() {
        return lx;
    }

    public void setLx(String lx) {
        this.lx = lx;
    }

    public String getRx() {
        return rx;
    }

    public void setRx(String rx) {
        this.rx = rx;
    }
}
