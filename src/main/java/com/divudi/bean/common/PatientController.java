package com.divudi.bean.common;

import com.divudi.bean.clinical.PatientEncounterController;
import com.divudi.bean.clinical.PracticeBookingController;
import com.divudi.bean.memberShip.MembershipSchemeController;
import com.divudi.data.Privileges;
import com.divudi.data.Sex;
import com.divudi.data.Title;
import com.divudi.data.dataStructure.YearMonthDay;
import com.divudi.ejb.BillNumberGenerator;
import com.divudi.ejb.CommonFunctions;
import com.divudi.entity.Institution;
import com.divudi.entity.Patient;
import com.divudi.entity.Person;
import com.divudi.entity.lab.PatientSample;
import com.divudi.entity.memberShip.MembershipScheme;
import com.divudi.facade.PatientFacade;
import com.divudi.facade.PersonFacade;
import com.divudi.facade.util.JsfUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 * Informatics)
 */
@Named
@SessionScoped
public class PatientController implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private PatientFacade ejbFacade;
    @Inject
    SessionController sessionController;
    @Inject
    PracticeBookingController practiceBookingController;
    @Inject
    private MembershipSchemeController membershipSchemeController;
    @Inject
    private BillController billController;
    @Inject
    ApplicationController applicationController;

    
    
    private String billNoForPaymentForMembership;
    private boolean billNumberForPaymentMatchesPatient;
    
    private Patient current;
    private List<Patient> items = null;

    @EJB
    private PersonFacade personFacade;
    private Date dob;

    @Inject
    PatientEncounterController PatientEncounterController;

    @EJB
    BillNumberGenerator billNumberBean;
    @EJB
    CommonFunctions commonFunctions;

    StreamedContent barcode;

    Title title;

    private String searchName;
    private String searchPhone;
    private String searchNic;
    private String searchPhn;
    private String searchPatientCode;
    private String searchPatientId;
    private String searchBillId;
    private String searchSampleId;
    private List<Patient> searchPatients;

    private String nameReq;
    private String addressReq;
    private String titleReq;
    private String yearsReq;
    private String monthsReq;
    private String daysReq;
    private String phoneReq;
    private String sexReq;
    private String membershipReq;
    private String usernameReq;
    private String passwordReq;
    private String requestResponse;

    private Long patientCount;

    String codeReq;

    public void calculatePatientCount() {
        String j = "select count(p) from Patient p where p.retired=false";
        patientCount = getFacade().findLongByJpql(j);
    }

    public void searchByBill() {
        String j;
        j = "select b.patient from Bill b where b.retired=false ";
        Map m = new HashMap();
        Long temId;
//        if(false){
//            Bill temP = new Bill();
//            temP.getPerson().getName();
//            temP.setRetired(true);
//            temP.getIdStr();
//            temP.getInsId();
//        }
        if (StringUtils.isNumeric(searchBillId)) {
            try {
                temId = Long.parseLong(searchBillId);
                j += " and b.id=:id ";
                m.put("id", temId);
            } catch (NumberFormatException e) {
                temId = 0l;
                j += " and b.id=:id ";
                m.put("id", temId);
            }
        } else {
            j += " and b.insId=:insid ";
            m.put("insid", searchBillId);
            temId = 0l;
        }
        j += " order by b.patient.person.name";
        searchPatients = getFacade().findBySQL(j, m);
    }

    public void searchBySample() {
        String j;
        j = "select ps.patientInvestigation.billItem.bill.patient from PatientSample ps where ps.retired=false ";
        Map m = new HashMap();
        Long temId;
        if (false) {
            PatientSample ps = new PatientSample();
            ps.getId();
            ps.getIdStr();
            ps.getPatientInvestigation().getBillItem().getBill().getPatient();
        }
        if (StringUtils.isNumeric(searchBillId)) {
            try {
                temId = Long.parseLong(searchSampleId);
                j += " and ps.id=:id ";
                m.put("id", temId);
            } catch (Exception e) {
                temId = 0l;
                j += " and ps.id=:id ";
                m.put("id", temId);
                searchPatients = new ArrayList<>();
            }
        }
        j += " order by ps.patientInvestigation.billItem.bill.patient.person.name";
        searchPatients = getFacade().findBySQL(j, m);
    }

    public void searchPatientByDetails() {

        String j;
        Map m = new HashMap();
        if (false) {
            Patient temP = new Patient();
            temP.getPerson().getName();
            temP.setRetired(true);
        }

        j = "select p from Patient p where p.retired=false and ";

        if (searchName != null && !searchName.trim().equals("")) {
            j += " lower(p.person.name) like :name ";
            m.put("name", "%" + searchName.toLowerCase() + "%");
        }

        if (searchPatientCode != null && !searchPatientCode.trim().equals("")) {
            j += " lower(p.code) like :name ";
            m.put("name", "%" + searchPatientCode.toLowerCase() + "%");
        }

        if (searchPhone != null && !searchPhone.trim().equals("")) {
            j += " p.person.phone =:phone";
            m.put("phone", searchPhone);
        }

        if (searchNic != null && !searchNic.trim().equals("")) {
            j += " p.person.nic =:nic";
            m.put("nic", searchNic);

        }

        if (searchPhn != null && !searchPhn.trim().equals("")) {
            j += " p.phn =:phn";
            m.put("phn", searchPhn);

        }

        j += " order by p.person.name";

        searchPatients = getFacade().findBySQL(j, m);

    }

    public void searchByPatientId() {
        String j;
        Map m = new HashMap();
        j = "select p from Patient p where p.retired=false and p.id=:id";
        Long ptId = 0l;
        try {
            ptId = Long.parseLong(searchPatientId);
        } catch (Exception e) {

        }
        m.put("id", ptId);
        searchPatients = getFacade().findBySQL(j, m);

    }

    public String searchPatient() {
        if (searchBillId != null && !searchBillId.trim().equals("")) {
            searchByBill();
        } else if (searchSampleId != null && !searchSampleId.trim().equals("")) {
            searchBySample();
        } else if (searchPatientId != null && !searchPatientId.trim().equals("")) {
            searchByPatientId();
        } else {
            searchPatientByDetails();
        }
        if (searchPatients == null) {
            JsfUtil.addErrorMessage("No Matches. Please use different criteria.");
            return "";
        }
        clearSearchDetails();
        return "";
    }

    public void clearSearchDetails() {
        searchName = null;
        searchPhone = null;
        searchNic = null;
        searchPatientCode = null;
        searchPatientId = null;
        searchBillId = null;
        searchSampleId = null;
    }

    public String toPatientFromSearchPatients() {
        if (current == null) {
            JsfUtil.addErrorMessage("No Patient Selected");
            return "";
        }
        patientSelected();
        return "/clinical/patient";
    }

    public String toAddToQueueFromSearchPatients() {
        if (current == null) {
            JsfUtil.addErrorMessage("No Patient Selected");
            return "";
        }
        patientSelected();
        return "/clinical/patient_add_to_queue";
    }

    public Patient addPatientByRequest() {
        Title temTitle = Title.Other;
        Sex temSex = Sex.Unknown;
        MembershipScheme temMembership;
        Patient pt = new Patient();
        Person p = new Person();

        pt.setCode(getCountPatientCode());

        if (sessionController.getLoggedUser() == null) {
            return null;
        }
        if (sessionController.getLoggedUser().getInstitution() == null) {
            return null;
        }

        Institution ins = sessionController.getLoggedUser().getInstitution();
        pt.setPhn(applicationController.createNewPersonalHealthNumber(ins));
        pt.setCreatedInstitution(ins);

        p.setName(nameReq);
        p.setAddress(addressReq);
        p.setPhone(phoneReq);


        getPersonFacade().create(p);
        getFacade().create(pt);

        YearMonthDay temYmd = new YearMonthDay();
        temYmd.setYear(yearsReq);
        temYmd.setMonth(monthsReq);
        temYmd.setDay(daysReq);

        p.setDob(getCommonFunctions().guessDob(temYmd));

        switch (sexReq.toLowerCase()) {
            case "male":
                temSex = Sex.Male;
                break;
            case "female":
                temSex = Sex.Female;
                break;
            case "other":
                temSex = Sex.Other;
                break;
            case "unknown":
                temSex = Sex.Unknown;
                break;
            default:
                temSex = Sex.Unknown;
        }

        switch (titleReq.toLowerCase()) {
            case "mr.":
                temTitle = Title.Mr;
                break;
            case "mrs.":
                temTitle = Title.Mrs;
                break;
            case "miss.":
                temTitle = Title.Miss;
                break;
            case "ms.":
                temTitle = Title.Ms;
                break;
            case "master":
                temTitle = Title.Master;
                break;
            case "baby":
                temTitle = Title.Baby;
                break;
            default:

        }

        temMembership = membershipSchemeController.findMembershipScheme(membershipReq, Boolean.FALSE);
        p.setMembershipScheme(temMembership);

        p.setTitle(temTitle);
        p.setSex(temSex);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DATE, 1);

        pt.setFromDate(c.getTime());

        c.add(Calendar.YEAR, 1);
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DATE, 31);

        pt.setToDate(c.getTime());

        pt.setPerson(p);

        getFacade().edit(pt);
        getPersonFacade().edit(p);

        return pt;
    }

    public Patient searchPatientByRequest(String code) {
        String j;
        Map m = new HashMap();
        j = "select p from Patient p where p.phn=:phn";
        m.put("phn", code);
        Patient p = getFacade().findFirstBySQL(j, m);
        if (p != null) {
            return p;
        }

        j = "select p from Patient p where p.code = :c or p.id = :lc";
        Long reqId = 0l;
        try {
            reqId = Long.parseLong(code);
        } catch (Exception e) {

        }
        m = new HashMap();
        m.put("c", code);
        m.put("lc", reqId);
        return getFacade().findFirstBySQL(j, m);
    }

    public void prepareSearchingPatientByRequest() {
        requestResponse = "#{";
        if (!getSessionController().loginForRequests(usernameReq, passwordReq)) {
            requestResponse += "Login=0|Message=Username or password error.}#";
            return;
        }
        if (codeReq == null || codeReq.trim().equals("")) {
            requestResponse += "Login=0|Message=No Code for Patient to search.}#";
            return;
        }
        Patient requestedPatient = searchPatientByRequest(codeReq);
        if (requestedPatient == null) {
            requestResponse += "Login=0";
            requestResponse += "|Message=No Patient was found with that code. Please recheck.";
        } else {
            requestResponse += "Login=1";
            requestResponse += "|Message=";

            /**
             * ^XA ^FO140,10^AC ^FDBUDDHIKA ARIYARATNE TEST^FS ^FO140,40
             * ^BY2,40,140 ^BC^FD003419^FS ^XZ
             */
            String printTemplate = "^XA\n"
                    + "^FO140,10\n"
                    + "^AC\n"
                    + "^FD" + requestedPatient.getPerson().getName() + "^FS\n"
                    + "^FO140,40\n"
                    + "^BY2,40,140\n"
                    + "^BC^FD" + requestedPatient.getPhn() + "^FS\n"
                    + "^XZ";
            requestResponse += printTemplate;

            billController.createBillForPatientCardPrinting(requestedPatient);

            requestedPatient.setCardIssues(true);
            requestedPatient.setCardIssuedDate(new Date());
            getFacade().edit(requestedPatient);
        }
        requestResponse += "}#";
    }

    public void preparePatientAddingByRequest() {
        requestResponse = "#{";

        if (!getSessionController().loginForRequests(usernameReq, passwordReq)) {
            requestResponse += "Login=0|Message=Username or password error.}#";
            return;
        }
        if (nameReq == null || nameReq.trim().equals("")) {
            requestResponse += "Login=0|Message=No Name for Patient.}#";
            return;
        }
        Patient requestedPatient = addPatientByRequest();

        requestResponse += "Login=1";
        requestResponse += "|Message=";

        /**
         * ^XA ^FO140,10^AC ^FDBUDDHIKA ARIYARATNE TEST^FS ^FO140,40 ^BY2,40,140
         * ^BC^FD003419^FS ^XZ
         */
        String printTemplate = "^XA\n"
                + "^F140,10\n"
                + "^AC\n"
                + "^FD" + requestedPatient.getPerson().getName() + "^FS\n"
                + "^FO140,40\n"
                + "^BY2,40,140\n"
                + "^BC^FD" + requestedPatient.getPhn() + "^FS\n"
                + "^XZ";

        requestResponse += printTemplate;

        requestResponse += "}#";

//        TODO: Remove if fee is taken
        billController.createBillForPatientCardPrinting(requestedPatient);

        requestedPatient.setCardIssues(true);
        requestedPatient.setCardIssuedDate(new Date());
        getFacade().edit(requestedPatient);
    }

    public void patientSelected() {
        getPatientEncounterController().fillCurrentPatientLists(current);
    }

    public void createPatientBarcode() {
        File barcodeFile = new File("ptbarcode");
        if (current != null && current.getCode() != null && !current.getCode().trim().equals("")) {
            try {
                BarcodeImageHandler.saveJPEG(BarcodeFactory.createCode128(getCurrent().getCode()), barcodeFile);
                barcode = new DefaultStreamedContent(new FileInputStream(barcodeFile), "image/jpeg");

            } catch (Exception ex) {
                //   //System.out.println("ex = " + ex.getMessage());
            }
        } else {
            //   //System.out.println("else = ");
            try {
                Barcode bc = BarcodeFactory.createCode128A("0000");
                bc.setBarHeight(5);
                bc.setBarWidth(3);
                bc.setDrawingText(true);
                BarcodeImageHandler.saveJPEG(bc, barcodeFile);
                //   //System.out.println("12");
                barcode = new DefaultStreamedContent(new FileInputStream(barcodeFile), "image/jpeg");
            } catch (Exception ex) {
                //   //System.out.println("ex = " + ex.getMessage());
            }
        }
    }

    public CommonFunctions getCommonFunctions() {
        return commonFunctions;
    }

    public void setCommonFunctions(CommonFunctions commonFunctions) {
        this.commonFunctions = commonFunctions;
    }

    private YearMonthDay yearMonthDay;

    public YearMonthDay getYearMonthDay() {
        if (yearMonthDay == null) {
            yearMonthDay = new YearMonthDay();

        }
        return yearMonthDay;
    }

    public void setYearMonthDay(YearMonthDay yearMonthDay) {
        this.yearMonthDay = yearMonthDay;
    }

    public void dateChangeListen() {
        getCurrent().getPerson().setDob(getCommonFunctions().guessDob(yearMonthDay));
    }

    public StreamedContent getPhoto(Patient p) {
        ////System.out.println("p is " + p);
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getRenderResponse()) {
            return new DefaultStreamedContent();
        } else if (p == null) {
            return new DefaultStreamedContent();
        } else {
            if (p.getId() != null && p.getBaImage() != null) {
                ////System.out.println("giving image");
                return new DefaultStreamedContent(new ByteArrayInputStream(p.getBaImage()), p.getFileType(), p.getFileName());
            } else {
                return new DefaultStreamedContent();
            }
        }

    }

    public StreamedContent getPhotoByByte(byte[] p) {
        ////System.out.println("p is " + p);
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getRenderResponse()) {
            return new DefaultStreamedContent();
        } else if (p == null) {
            return new DefaultStreamedContent();
        } else {
//   //System.out.println("giving image");
            return new DefaultStreamedContent(new ByteArrayInputStream(p), "image/png", "photo.");
        }
    }

    public StreamedContent getPhotoByByteNew(Patient p) {
        Patient pa = getFacade().find(p.getId());
        byte[] img = pa.getBaImage();
        if (img == null) {
            return new DefaultStreamedContent();
        } else {
//   //System.out.println("giving image");
            return new DefaultStreamedContent(new ByteArrayInputStream(img), "image/png");
        }
    }

    public Title[] getTitles() {
        return Title.values();
    }

    public Title[] getDoctorTitles() {
        Title[] titles = {
            Title.Dr,
            Title.DrMrs,
            Title.DrMs,
            Title.DrMiss,
            Title.Prof,
            Title.ProfMs,
            Title.ProfMrs,
            Title.ProfMiss,};
        return titles;
    }

    public Sex[] getSexs() {
        return Sex.values();
    }

    public String prepareAdd() {
        current = null;
        yearMonthDay = null;
        getCurrent();
        getYearMonthDay();
        return "/clinical/patient";
    }

    public void delete() {

        if (current != null) {
            current.setRetired(true);
            current.setRetiredAt(new Date());
            current.setRetirer(getSessionController().getLoggedUser());
            getFacade().edit(current);
            UtilityController.addSuccessMessage("Deleted Successfull");
        } else {
            UtilityController.addSuccessMessage("Nothing to Delete");
        }
        recreateModel();
        getItems();
        current = null;
        getCurrent();
    }

    private void recreateModel() {
        items = null;
    }

    public void createRandomPatient(String ptName) {
        Person person = new Person();
        Patient pt = new Patient();
        person.setName(ptName);
        pt.setPerson(person);
        getPersonFacade().create(person);
        getFacade().create(pt);
    }

    public List<Patient> completePatient(String query) {
        List<Patient> suggestions;
        String sql;
        HashMap hm = new HashMap();
        if (query == null) {
            suggestions = new ArrayList<>();
        } else {
            sql = "select p from Patient p where p.retired=false "
                    + " and upper(p.person.name) like :q "
                    + "  order by p.person.name";
            hm.put("q", "%" + query.toUpperCase() + "%");
            ////System.out.println(sql);
            suggestions = getFacade().findBySQL(sql, hm, 20);
        }
        return suggestions;
    }

    private List<Patient> patientList;

    /**
     * 
     * @param query
     * @return 
     */
    public List<Patient> completePatientByNameOrCode(String query) {
        if (query == null) {
            return null;
        }
        String sql;
        HashMap hm = new HashMap();
        sql = "select p from Patient p where p.retired=false "
                + " and (upper(p.person.name) like :q "
                + " or upper(p.code) like :q "
                + " or upper(p.person.nic) like :q "
                + " or upper(p.person.mobile) like :q "
                + " or upper(p.person.phone) like :q "
                + " or upper(p.person.address) like :q )"
                + "order by p.person.name";
        hm.put("q", "%" + query.trim().toUpperCase() + "%");
        patientList = getFacade().findBySQL(sql, hm, 30);
        return patientList;
    }

    public List<Patient> completePatientByNameOrCodeOrTitle(String query) {
        if (query == null) {
            return null;
        }
        String sql;
        HashMap hm = new HashMap();
        sql = "select p from Patient p where p.retired=false "
                + " and ( upper(p.person.name) like  :q "
                + " or upper(p.code) =:eq "
                + " or upper(p.phn) =:eq "
                + " or upper(p.person.nic) = :eq "
                + " or upper(p.person.mobile) = :eq "
                + " or upper(p.person.phone) = :eq "
                + " or upper(p.person.address) like :q ) ";

//        if (title != null) {
//            sql += " and p.person.title=:t ";
//            hm.put("t", title);
//        }

        sql += " order by p.person.name ";

        hm.put("q", "%" + query.trim().toUpperCase() + "%");
        hm.put("eq", query.toUpperCase());
        patientList = getFacade().findBySQL(sql, hm);
        return patientList;
    }

    public void saveAndUpdateQueue() {
        saveSelected();
        getPracticeBookingController().listBillSessions();
    }

    public void generateNewCode() {
        if (current == null) {
            JsfUtil.addErrorMessage("No patient");
            return;
        }
        current.setCode(getCountPatientCode());
    }

    public void generateNewPhn() {
        if (current == null) {
            JsfUtil.addErrorMessage("No patient");
            return;
        }
        if (sessionController.getLoggedUser() == null) {
            return;
        }
        if (sessionController.getLoggedUser().getInstitution() == null) {
            return;
        }
        Institution ins = sessionController.getLoggedUser().getInstitution();
        current.setPhn(applicationController.createNewPersonalHealthNumber(ins));
        current.setCreatedInstitution(ins);
    }

    public void createPhnsForAllPatients() {
        List<Patient> pts = getFacade().findAll();
        for (Patient p : pts) {
            if (p.getPhn() == null || p.getPhn().trim().equals("")) {
                if (sessionController.getLoggedUser() == null) {
                    return;
                }
                if (sessionController.getLoggedUser().getInstitution() == null) {
                    return;
                }
                Institution ins = sessionController.getLoggedUser().getInstitution();
                p.setPhn(applicationController.createNewPersonalHealthNumber(ins));
                p.setCreatedInstitution(ins);
                getFacade().edit(p);
            }
        }
    }

    public String getCountPatientCode() {
        String sql;
        String str = "";
        Long lng = null;
        if (applicationController.getLastCodeGenerated() == null) {
            sql = "select count(p) FROM Patient p";
            lng = getEjbFacade().countBySql(sql);
            lng++;

        } else {
            lng = applicationController.getLastCodeGenerated() + 1;
        }
        lng = codeExists(lng);
        applicationController.setLastCodeGenerated(lng);
        str += lng;
        return str;
    }

    public Long codeExists(Long code) {
        String sql;
        sql = "select p FROM Patient p where p.code=:code";
        Map m = new HashMap();
        m.put("code", code + "");
        Patient p = getFacade().findFirstBySQL(sql, m);
        if (p == null) {
            return code;
        } else {
            return codeExists(code + 1);
        }
    }

    public void saveSelected() {
        if (getCurrent() == null) {
            UtilityController.addErrorMessage("No Current. Error. NOT SAVED");
            return;
        }
        if (getCurrent().getPerson() == null) {
            UtilityController.addErrorMessage("No Person. Not Saved");
            return;
        }
        if (getCurrent().getPerson().getName().trim().equals("")) {
            UtilityController.addErrorMessage("Please enter a name");
            return;
        }
        if (getCurrent().getPerson().getId() == null) {

            getCurrent().getPerson().setCreatedAt(Calendar.getInstance().getTime());
            getCurrent().getPerson().setCreater(getSessionController().getLoggedUser());
            getPersonFacade().create(getCurrent().getPerson());

        } else {
            if (sessionController.hasPrivilege(Privileges.ClinicalPatientEdit)) {
                getCurrent().getPerson().setEditedAt(Calendar.getInstance().getTime());
                getCurrent().getPerson().setEditer(getSessionController().getLoggedUser());
                getPersonFacade().edit(getCurrent().getPerson());
            } else {
                JsfUtil.addErrorMessage("You have no Privilege to Edit Patients.");
                return;
            }
        }
        if (getCurrent().getId() == null) {

            getCurrent().setCreatedAt(new Date());
            getCurrent().setCreater(getSessionController().getLoggedUser());
            getFacade().create(current);
            UtilityController.addSuccessMessage("Saved as a new patient successfully.");

        } else {
            if (sessionController.hasPrivilege(Privileges.ClinicalPatientEdit)) {
                getCurrent().setEditedAt(Calendar.getInstance().getTime());
                getCurrent().setEditer(getSessionController().getLoggedUser());
                getFacade().edit(getCurrent());
                UtilityController.addSuccessMessage("Updated the patient details successfully.");
            } else {
                JsfUtil.addErrorMessage("You have no Privilege to Edit Patients.");
                return;
            }
        }
        getPersonFacade().edit(current.getPerson());
        getFacade().edit(current);

    }

    public PatientFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(PatientFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public PatientController() {
    }

    public Patient getCurrent() {
        if (current == null) {
            Person p = new Person();
            current = new Patient();
            current.setCode(getCountPatientCode());
            current.setPerson(p);
        }
        return current;
    }

    public void setCurrent(Patient current) {
        this.current = current;
        billNoForPaymentForMembership = "";
        getYearMonthDay();
        if (current == null) {
            yearMonthDay.setDay("0");
            yearMonthDay.setMonth("0");
            yearMonthDay.setYear("0");
        } else {
            yearMonthDay.setDay(current.getAgeDays() + "");
            yearMonthDay.setMonth(current.getAgeMonths() + "");
            yearMonthDay.setYear(current.getAgeYears() + "");
        }
        getPatientEncounterController().fillCurrentPatientLists(current);
    }

    private PatientFacade getFacade() {
        return ejbFacade;
    }

    public List<Patient> getItems() {
        if (items == null || items.isEmpty()) {
            fillAllPatients();
        }
        return items;
    }

    public void fillAllPatients() {
        String sql;
        sql = "select p from Patient p where p.retired = false order by p.person.name";
        items = getFacade().findBySQL(sql);
    }

    public List<Patient> getItemsByDob() {
        String sql;
        Map m = new HashMap();
        m.put("dob", dob);
        sql = "select p from Patient p where p.retired = false and p.person.dob=:dob order by p.person.name";
        return getFacade().findBySQL(sql, m);
    }

    public PersonFacade getPersonFacade() {
        return personFacade;
    }

    public void setPersonFacade(PersonFacade personFacade) {
        this.personFacade = personFacade;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public BillNumberGenerator getBillNumberBean() {
        return billNumberBean;
    }

    public void setBillNumberBean(BillNumberGenerator billNumberBean) {
        this.billNumberBean = billNumberBean;
    }

    public StreamedContent getBarcode() {
        return barcode;
    }

    public void setBarcode(StreamedContent barcode) {
        this.barcode = barcode;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public String getNameReq() {
        return nameReq;
    }

    public void setNameReq(String nameReq) {
        this.nameReq = nameReq;
    }

    public String getAddressReq() {
        return addressReq;
    }

    public void setAddressReq(String addressReq) {
        this.addressReq = addressReq;
    }

    public String getTitleReq() {
        return titleReq;
    }

    public void setTitleReq(String titleReq) {
        this.titleReq = titleReq;
    }

    public String getYearsReq() {
        return yearsReq;
    }

    public void setYearsReq(String yearsReq) {
        this.yearsReq = yearsReq;
    }

    public String getMonthsReq() {
        return monthsReq;
    }

    public void setMonthsReq(String monthsReq) {
        this.monthsReq = monthsReq;
    }

    public String getDaysReq() {
        return daysReq;
    }

    public void setDaysReq(String daysReq) {
        this.daysReq = daysReq;
    }

    public String getPhoneReq() {
        return phoneReq;
    }

    public void setPhoneReq(String phoneReq) {
        this.phoneReq = phoneReq;
    }

    public String getUsernameReq() {
        return usernameReq;
    }

    public void setUsernameReq(String usernameReq) {
        this.usernameReq = usernameReq;
    }

    public String getPasswordReq() {
        return passwordReq;
    }

    public void setPasswordReq(String passwordReq) {
        this.passwordReq = passwordReq;
    }

    public List<Patient> getPatientList() {
        return patientList;
    }

    public void setPatientList(List<Patient> patientList) {
        this.patientList = patientList;
    }

    public String getRequestResponse() {
        return requestResponse;
    }

    public void setRequestResponse(String requestResponse) {
        this.requestResponse = requestResponse;
    }

    public String getSexReq() {
        return sexReq;
    }

    public void setSexReq(String sexReq) {
        this.sexReq = sexReq;
    }

    public String getMembershipReq() {
        return membershipReq;
    }

    public void setMembershipReq(String membershipReq) {
        this.membershipReq = membershipReq;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String getSearchPhone() {
        return searchPhone;
    }

    public void setSearchPhone(String searchPhone) {
        this.searchPhone = searchPhone;
    }

    public String getSearchNic() {
        return searchNic;
    }

    public void setSearchNic(String searchNic) {
        this.searchNic = searchNic;
    }

    public String getSearchPatientCode() {
        return searchPatientCode;
    }

    public void setSearchPatientCode(String searchPatientCode) {
        this.searchPatientCode = searchPatientCode;
    }

    public String getSearchPatientillId() {
        return searchPatientId;
    }

    public void setSearchPatientillId(String searchPatientillId) {
        this.searchPatientId = searchPatientillId;
    }

    public String getSearchBillId() {
        return searchBillId;
    }

    public void setSearchBillId(String searchBillId) {
        this.searchBillId = searchBillId;
    }

    public String getSearchSampleId() {
        return searchSampleId;
    }

    public void setSearchSampleId(String searchSampleId) {
        this.searchSampleId = searchSampleId;
    }

    public List<Patient> getSearchPatients() {
        return searchPatients;
    }

    public void setSearchPatients(List<Patient> searchPatients) {
        this.searchPatients = searchPatients;
    }

    public MembershipSchemeController getMembershipSchemeController() {
        return membershipSchemeController;
    }

    public void setMembershipSchemeController(MembershipSchemeController membershipSchemeController) {
        this.membershipSchemeController = membershipSchemeController;
    }

    public Long getPatientCount() {
        return patientCount;
    }

    public void setPatientCount(Long patientCount) {
        this.patientCount = patientCount;
    }

    public String getSearchPatientId() {
        return searchPatientId;
    }

    public void setSearchPatientId(String searchPatientId) {
        this.searchPatientId = searchPatientId;
    }

    public BillController getBillController() {
        return billController;
    }

    public String getSearchPhn() {
        return searchPhn;
    }

    public void setSearchPhn(String searchPhn) {
        this.searchPhn = searchPhn;
    }

    public String getBillNoForPaymentForMembership() {
        return billNoForPaymentForMembership;
    }

    public void setBillNoForPaymentForMembership(String billNoForPaymentForMembership) {
        this.billNoForPaymentForMembership = billNoForPaymentForMembership;
    }

    public boolean isBillNumberForPaymentMatchesPatient() {
        if(billNoForPaymentForMembership==null || billNoForPaymentForMembership.trim().equals("")){
            billNumberForPaymentMatchesPatient = false;
            return billNumberForPaymentMatchesPatient;
        }
        String cardIssue = "Card issue";
        cardIssue = cardIssue.trim().toLowerCase();
        
        String j = "select count(bi) from BillItem bi "
                + " where bi.bill.retired<>:ret "
                + " and bi.bill.cancelled<>:ret "
                + " and bi.retired<>:ret "
                + " and bi.bill.patient=:pt "
                + " and (bi.bill.insId=:bn or bi.bill.deptId=:bn or bi.bill.id=:bid) "
                + " and lower(bi.item.name)=:name";
        Map m = new HashMap();
        m.put("pt", current);
        m.put("ret", false);
        m.put("name", cardIssue);
        m.put("bn", billNoForPaymentForMembership);

        try{
            Long bid = Long.parseLong(billNoForPaymentForMembership);
            m.put("bid", bid);
        }catch(Exception e){
            m.put("bid", 0);
        }
        
        Long count = getFacade().countBySql(j,m);
        if(count==null||count==0l){
            billNumberForPaymentMatchesPatient = false;
            return billNumberForPaymentMatchesPatient;
        }
        billNumberForPaymentMatchesPatient = true;
        return billNumberForPaymentMatchesPatient;
    }

    public void setBillNumberForPaymentMatchesPatient(boolean billNumberForPaymentMatchesPatient) {
        this.billNumberForPaymentMatchesPatient = billNumberForPaymentMatchesPatient;
    }
    
    
    

    /**
     *
     * Set all Patients to null
     *
     */
    /**
     *
     */
    /**
     *
     * Delete the current Patient
     *
     */
    /**
     *
     */
    @FacesConverter(forClass = Patient.class)
    public static class PatientControllerConverter implements Converter {

        /**
         *
         * @param facesContext
         * @param component
         * @param value
         * @return
         */
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PatientController controller = (PatientController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "patientController");
            ////System.out.println("value at converter getAsObject is " + value);
            return controller.getEjbFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            ////System.out.println(value);
            if (value == null || value.equals("null") || value.trim().equals("")) {
                key = 0l;
            } else {
                key = Long.valueOf(value);
                ////System.out.println(key);
                ////System.out.println(value);
            }
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        /**
         *
         * @param facesContext
         * @param component
         * @param object
         * @return
         */
        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Patient) {
                Patient o = (Patient) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + PatientController.class.getName());
            }
        }
    }

    public PracticeBookingController getPracticeBookingController() {
        return practiceBookingController;
    }

    public void setPracticeBookingController(PracticeBookingController practiceBookingController) {
        this.practiceBookingController = practiceBookingController;
    }

    public PatientEncounterController getPatientEncounterController() {
        return PatientEncounterController;
    }

    public void setPatientEncounterController(PatientEncounterController PatientEncounterController) {
        this.PatientEncounterController = PatientEncounterController;
    }

    public String getCodeReq() {
        return codeReq;
    }

    public void setCodeReq(String codeReq) {
        this.codeReq = codeReq;
    }

    @FacesConverter("patientConverter")
    public static class PatientConverter implements Converter {

        /**
         *
         * @param facesContext
         * @param component
         * @param value
         * @return
         */
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PatientController controller = (PatientController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "patientController");
            ////System.out.println("value at converter getAsObject is " + value);
            return controller.getEjbFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            ////System.out.println(value);
            if (value == null || value.equals("null") || value.trim().equals("")) {
                key = 0l;
            } else {
                key = Long.valueOf(value);
                ////System.out.println(key);
                ////System.out.println(value);
            }
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        /**
         *
         * @param facesContext
         * @param component
         * @param object
         * @return
         */
        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Patient) {
                Patient o = (Patient) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + PatientController.class.getName());
            }
        }
    }

}
