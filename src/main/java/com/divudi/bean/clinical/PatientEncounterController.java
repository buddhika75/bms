/*
 * MSc(Biomedical Informatics) Project
 *
 * Development and Implementation of a Web-based Combined Data Repository of
 Genealogical, Clinical, Laboratory and Genetic Data
 * and
 * a Set of Related Tools
 */
package com.divudi.bean.clinical;

import com.divudi.bean.common.BillController;
import com.divudi.bean.common.CommonFunctionsController;
import com.divudi.bean.common.SessionController;
import com.divudi.bean.common.UtilityController;
import com.divudi.bean.common.WebUserController;
import com.divudi.bean.pharmacy.PharmacySaleController;
import com.divudi.data.BillType;
import com.divudi.data.Privileges;
import com.divudi.data.SymanticType;
import com.divudi.data.clinical.ItemUsageType;
import com.divudi.data.lab.InvestigationResultForGraph;
import com.divudi.entity.Bill;
import com.divudi.entity.Department;
import com.divudi.entity.Doctor;
import com.divudi.entity.Institution;
import com.divudi.entity.Item;
import com.divudi.entity.Patient;
import com.divudi.entity.PatientEncounter;
import com.divudi.entity.Person;
import com.divudi.entity.WebUserPrivilege;
import com.divudi.entity.clinical.ClinicalFindingItem;
import com.divudi.entity.clinical.ClinicalFindingValue;
import com.divudi.entity.clinical.ItemUsage;
import com.divudi.entity.lab.Investigation;
import com.divudi.entity.lab.InvestigationItem;
import com.divudi.entity.lab.PatientInvestigation;
import com.divudi.entity.lab.PatientReportItemValue;
import com.divudi.entity.pharmacy.Amp;
import com.divudi.entity.pharmacy.Vmp;
import com.divudi.facade.BillFacade;
import com.divudi.facade.ClinicalFindingItemFacade;
import com.divudi.facade.ItemUsageFacade;
import com.divudi.facade.PatientEncounterFacade;
import com.divudi.facade.PatientFacade;
import com.divudi.facade.PatientInvestigationFacade;
import com.divudi.facade.PatientReportItemValueFacade;
import com.divudi.facade.PersonFacade;
import com.divudi.facade.util.JsfUtil;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TemporalType;
import org.joda.time.DateTimeZone;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 * Informatics)
 */
@Named
@SessionScoped
public class PatientEncounterController implements Serializable {

    /**
     * EJBs
     */
    @EJB
    private PatientEncounterFacade ejbFacade;
    @EJB
    ClinicalFindingItemFacade clinicalFindingItemFacade;
    @EJB
    PersonFacade personFacade;
    @EJB
    PatientFacade patientFacade;
    @EJB
    BillFacade billFacade;
    @EJB
    PatientInvestigationFacade piFacade;
    @EJB
    ItemUsageFacade itemUsageFacade;
    @EJB
    private PatientReportItemValueFacade privFacade;
    /**
     * Controllers
     */
    @Inject
    private CommonFunctionsController commonFunctions;
    @Inject
    SessionController sessionController;
    @Inject
    PharmacySaleController pharmacySaleController;
    @Inject
    BillController billController;
    @Inject
    WebUserController webUserController;

    /**
     * Properties
     */
    List<String> completeStrings = null;
    private static final long serialVersionUID = 1L;
    //
    private List<PatientEncounter> selectedItems;
    private PatientEncounter current;
    private List<PatientEncounter> items = null;
    List<PatientEncounter> currentPatientEncounters;
    List<ItemUsage> currentPatientAllergies;
    List<Bill> currentPatientBills;
    List<Bill> currentChannelBills;
    List<PatientInvestigation> currentPatientInvestigations;
    String selectText = "";
    String[] selectTexts = null;

    ClinicalFindingItem diagnosis;
    String diagnosisComments;
    Investigation investigation;

    ClinicalFindingValue removingCfv;

    PatientEncounter encounterToDisplay;
    PatientEncounter startedEncounter;

    Date fromDate;
    Date toDate;
    Institution institution;
    Department department;
    Doctor doctor;

    private String chartNameSeries;
    private String chartDataSeries1;
    private String chartDataSeries2;
    private String chartName;
    private String values1Name;
    private String values2Name;

    private String chartString;

    private InvestigationItem graphInvestigationItem;

    public List<String> completeClinicalComments(String qry) {
        if (current == null || current.getComments() == null) {
            completeRx(qry);
            return completeStrings;
        }
        String c = current.getComments();
        int intHx = c.lastIndexOf(getSessionController().getUserPreference().getAbbreviationForHistory());
        int intEx = c.lastIndexOf(getSessionController().getUserPreference().getAbbreviationForExamination());
        int intIx = c.lastIndexOf(getSessionController().getUserPreference().getAbbreviationForInvestigations());
        int intRx = c.lastIndexOf(getSessionController().getUserPreference().getAbbreviationForTreatments());
        int intMx = c.lastIndexOf(getSessionController().getUserPreference().getAbbreviationForManagement());

        //   ////System.out.println("intHx = " + intHx);
        //   ////System.out.println("intEx = " + intEx);
        //   ////System.out.println("intIx = " + intIx);
        //   ////System.out.println("intRx = " + intRx);
        //   ////System.out.println("intMx = " + intMx);
        ClinicalField lastField = ClinicalField.History;
        int lastValue = intHx;

        if (intEx > lastValue) {
            lastField = ClinicalField.Examination;
            lastValue = intEx;
        }

        if (intIx > lastValue) {
            lastField = ClinicalField.Investigations;
            lastValue = intIx;
        }

        if (intRx > lastValue) {
            lastField = ClinicalField.Treatments;
            lastValue = intRx;
        }

        if (intMx > lastValue) {
            lastField = ClinicalField.Management;
            lastValue = intMx;
        }

        switch (lastField) {
            case History:
                completeHx(qry);
                break;
            case Examination:
                completeEx(qry);
                break;
            case Investigations:
                completeIx(qry);
                break;
            case Treatments:
                completeRx(qry);
                break;
            default:
                completeStrings = completeItem(qry);
        }

        return completeStrings;
    }

    public List<String> completeItem(String qry) {
        //   ////System.out.println("complete item");
        if (qry == null) {
            qry = "";
        }
        String sql;
        sql = "select c.name from Item c where c.retired=false and "
                + "upper(c.name) like :q "
                + "order by c.name";
        Map tmpMap = new HashMap();
        tmpMap.put("q", qry.toUpperCase() + "%");
        return getFacade().findString(sql, tmpMap);
    }

    public void completeHx(String qry) {
        //   ////System.out.println("complete hx");
        if (qry == null) {
            qry = "";
        }
        String sql;
        sql = "select c.name from Item c where c.retired=false and "
                + "type(c)= :cls and "
                + "c.symanticType=:st and "
                + "upper(c.name) like :q "
                + "order by c.name";
        Map tmpMap = new HashMap();
        tmpMap.put("cls", ClinicalFindingItem.class);
        tmpMap.put("st", SymanticType.Symptom);
        tmpMap.put("q", qry.toUpperCase() + "%");
        completeStrings = getFacade().findString(sql, tmpMap, TemporalType.TIMESTAMP);
    }

    public void completeEx(String qry) {

        //   ////System.out.println("complete ex");
        if (qry == null) {
            qry = "";
        }
        String sql;
        sql = "select c.name from Item c where c.retired=false and "
                + "type(c)= :cls and "
                + "c.symanticType=:st and "
                + "upper(c.name) like :q "
                + "order by c.name";
        Map tmpMap = new HashMap();
        tmpMap.put("cls", ClinicalFindingItem.class);
        tmpMap.put("st", SymanticType.Sign);
        tmpMap.put("q", qry.toUpperCase() + "%");
        completeStrings = getFacade().findString(sql, tmpMap, TemporalType.TIMESTAMP);
    }

    public void completeIx(String qry) {
        //   ////System.out.println("complete Ix");
        if (qry == null) {
            qry = "";
        }
        String sql;
        sql = "select c.name from Investigation c where c.retired=false and "
                + "upper(c.name) like :q "
                + "order by c.name";
        Map tmpMap = new HashMap();
        tmpMap.put("q", qry.toUpperCase() + "%");
        completeStrings = getFacade().findString(sql, tmpMap, TemporalType.TIMESTAMP);
    }

    public void completeRx(String qry) {
        //   ////System.out.println("complete rx");
        //   ////System.out.println("qry = " + qry);
        if (qry == null) {
            qry = "";
        }
        String sql;
        sql = "select c.name from Item c where c.retired=false and "
                + "(type(c)= :amp or type(c)= :vmp or "
                + "type(c)= :vtm or "
                + "(type(c)= :ce and c.symanticType=:st)) "
                + "and upper(c.name) like :q "
                + "order by c.name";
        //////System.out.println(sql);
        Map tmpMap = new HashMap();
        tmpMap.put("amp", Amp.class);
        tmpMap.put("vmp", Vmp.class);
        tmpMap.put("vtm", Vmp.class);
        tmpMap.put("ce", ClinicalFindingItem.class);
        tmpMap.put("st", SymanticType.Pharmacologic_Substance);
        tmpMap.put("q", qry.toUpperCase() + "%");
        completeStrings = getFacade().findString(sql, tmpMap);
    }

    public void listAllEncounters() {
        String jpql;
        Map m = new HashMap();
        jpql = "select pe from PatientEncounter pe where pe.retired=false and pe.createdAt between :fd and :td ";
        m.put("fd", fromDate);
        m.put("td", toDate);
        if (institution != null) {
            jpql = jpql + " and pe.department.institution=:ins ";
            m.put("ins", institution);

        } else if (department != null) {
            jpql = jpql + " and pe.department=:dep ";
            m.put("dep", department);
        }
        if (doctor != null) {
            jpql = jpql + " and pe.opdDoctor=:doc ";
            m.put("doc", doctor);
        }
        ////System.out.println("1. m = " + m);
        ////System.out.println("2. sql = " + jpql);
        items = getFacade().findBySQL(jpql, m, TemporalType.TIMESTAMP);
        ////System.out.println("3. items = " + items);
    }

    public void listPeriodEncounters() {
        String jpql;
        Map m = new HashMap();
        jpql = "select pe from PatientEncounter pe where pe.retired=false and pe.createdAt between :fd and :td ";
        m.put("fd", fromDate);
        m.put("td", toDate);
        if (institution != null) {
            jpql = jpql + " and pe.department.institution=:ins ";
            m.put("ins", institution);

        } else if (department != null) {
            jpql = jpql + " and pe.department=:dep ";
            m.put("dep", department);
        }
        if (doctor != null) {
            jpql = jpql + " and pe.opdDoctor=:doc ";
            m.put("doc", doctor);
        }
        //   ////System.out.println("m = " + m);
        //   ////System.out.println("sql = " + jpql);
        items = getFacade().findBySQL(jpql, m);

    }

    public void addDx() {
        if (diagnosis == null) {
            UtilityController.addErrorMessage("Please select a diagnosis");
            return;
        }
        if (current == null) {
            UtilityController.addErrorMessage("Please select a visit");
            return;
        }
        ClinicalFindingValue dx = new ClinicalFindingValue();
        dx.setItemValue(diagnosis);
        dx.setClinicalFindingItem(diagnosis);
        dx.setEncounter(current);
        dx.setPerson(current.getPatient().getPerson());
        dx.setStringValue(diagnosis.getName());
        dx.setLobValue(diagnosisComments);
        current.getClinicalFindingValues().add(dx);
        getFacade().edit(current);
        diagnosis = null;
//        diagnosis = new ClinicalFindingItem();
        diagnosisComments = "";
        UtilityController.addSuccessMessage("Diagnosis added");
        setCurrent(getFacade().find(current.getId()));
    }

    public List<PatientEncounter> getCurrentPatientEncounters() {
        return currentPatientEncounters;
    }

    public String createBpChart() {
        setChartNameSeries(getCurrentPatientEncountersDateStrings());
        setChartDataSeries1(getCurrentPatientEncountersSbpStrings());
        setChartDataSeries2(getCurrentPatientEncountersDbpStrings());
        setValues1Name("SBP");
        setValues2Name("DBP");
        setChartName("Blood Pressure Chart");

        String title1 = "Blood Pressure Chart";
        String title2 = "Name : " + current.getPatient().getPerson().getNameWithTitle();
        String title3 = "Age : " + current.getPatient().getAge();
        String title4 = "Date : " + CommonFunctionsController.formatDate();
        String[] titles
                = {title1, title2, title3, title4};

        setChartString(getDoubleLineChartString(titles));
        return "/chart";
    }

    public String createInvestigationChart() {
//        //System.out.println("createInvestigationChart");
        String s = "";
        int i = 0;
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
        String val = "";

//        //System.out.println("graphInvestigationItem = " + graphInvestigationItem);
//        //System.out.println("current.getPatient() = " + current.getPatient());
        List<PatientReportItemValue> privs
                = fillPatientReportItemValue(current.getPatient(), graphInvestigationItem);
//        //System.out.println("privs = " + privs);

        List<InvestigationResultForGraph> grs = new ArrayList<>();

        for (PatientReportItemValue v : privs) {
//            //System.out.println("v = " + v);
            boolean dateFound = false;
            Double dblVal = null;
            try {
                if (v.getDoubleValue() != null) {
                    dblVal = v.getDoubleValue();
                } else if (v.getStrValue() != null) {
                    dblVal = Double.parseDouble(v.getStrValue());
                }
                if (dblVal != null) {

                    i++;

                    dateFound = true;

                    s += "'" + format.format(v.getPatientReport().getPatientInvestigation().getSampledAt()) + "', ";

                    val += dblVal + ", ";

                }

            } catch (Exception e) {

            }

        }

        setChartNameSeries(s);
        setChartDataSeries1(val);
        setValues1Name(graphInvestigationItem.getName());
        setChartName(graphInvestigationItem.getName() + " Chart");
        String title1 = graphInvestigationItem.getName() + " Chart";
        String title2 = "Name : " + current.getPatient().getPerson().getNameWithTitle();
        String title3 = "Age : " + current.getPatient().getAge();
        String title4 = "Date : " + CommonFunctionsController.formatDate();
        String[] titles
                = {title1, title2, title3, title4};

        setChartString(getSingleLineChartString(titles,
                graphInvestigationItem.getName(),
                s,
                val));
        return "/chart";
    }

    public String createWtChart() {
        setChartNameSeries(getCurrentPatientEncountersDateStrings());
        setChartDataSeries1(getCurrentPatientEncountersWeightStrings());
        setValues1Name("Weight");
        setChartName("Weight Chart");
        String title1 =  "Weight Chart";
        String title2 = "Name : " + current.getPatient().getPerson().getNameWithTitle();
        String title3 = "Age : " + current.getPatient().getAge();
        String title4 = "Date : " + CommonFunctionsController.formatDate();
        String[] titles
                = {title1, title2, title3, title4};
        setChartString(getSingleLineChartString(
                titles, values1Name, chartNameSeries, chartDataSeries1)
        );
        return "/chart";
    }

    public String getDoubleLineChartString(String[] titles) {
        String s = "\n"
                + "		var MONTHS = [N1N1N1N1N1N1N1N1];\n"
                + "		var config = {\n"
                + "			type: 'line',\n"
                + "			data: {\n"
                + "				labels: [N1N1N1N1N1N1N1N1],\n"
                + "				datasets: [{\n"
                + "					label: 'My First dataset',\n"
                + "					backgroundColor: window.chartColors.red,\n"
                + "					borderColor: window.chartColors.red,\n"
                + "					data: [\n"
                + "						D1D1D1D1D1D1D1D1 \n"
                + "					],\n"
                + "					fill: false,\n"
                + "				}, {\n"
                + "					label: 'My Second dataset',\n"
                + "					fill: false,\n"
                + "					backgroundColor: window.chartColors.blue,\n"
                + "					borderColor: window.chartColors.blue,\n"
                + "					data: [\n"
                + "						D2D2D2D2D2D2D2D2\n"
                + "					],\n"
                + "				}]\n"
                + "			},\n"
                + "			options: {\n"
                + "				responsive: true,\n"
                + "				title: {\n"
                + "					display: true,\n"
                + "					text: 'Chart.js Line Chart'\n"
                + "				},\n"
                + "				tooltips: {\n"
                + "					mode: 'index',\n"
                + "					intersect: false,\n"
                + "				},\n"
                + "				hover: {\n"
                + "					mode: 'nearest',\n"
                + "					intersect: true\n"
                + "				},\n"
                + "				scales: {\n"
                + "					xAxes: [{\n"
                + "						display: true,\n"
                + "						scaleLabel: {\n"
                + "							display: true,\n"
                + "							labelString: 'Month'\n"
                + "						}\n"
                + "					}],\n"
                + "					yAxes: [{\n"
                + "						display: true,\n"
                + "						scaleLabel: {\n"
                + "							display: true,\n"
                + "							labelString: 'Value'\n"
                + "						}\n"
                + "					}]\n"
                + "				}\n"
                + "			}\n"
                + "		};\n"
                + "\n"
                + "		window.onload = function() {\n"
                + "			var ctx = document.getElementById('canvas').getContext('2d');\n"
                + "			window.myLine = new Chart(ctx, config);\n"
                + "		};\n"
                + "\n"
                + "		document.getElementById('randomizeData').addEventListener('click', function() {\n"
                + "			config.data.datasets.forEach(function(dataset) {\n"
                + "				dataset.data = dataset.data.map(function() {\n"
                + "					return randomScalingFactor();\n"
                + "				});\n"
                + "\n"
                + "			});\n"
                + "\n"
                + "			window.myLine.update();\n"
                + "		});\n"
                + "\n"
                + "		var colorNames = Object.keys(window.chartColors);\n"
                + "		document.getElementById('addDataset').addEventListener('click', function() {\n"
                + "			var colorName = colorNames[config.data.datasets.length % colorNames.length];\n"
                + "			var newColor = window.chartColors[colorName];\n"
                + "			var newDataset = {\n"
                + "				label: 'Dataset ' + config.data.datasets.length,\n"
                + "				backgroundColor: newColor,\n"
                + "				borderColor: newColor,\n"
                + "				data: [],\n"
                + "				fill: false\n"
                + "			};\n"
                + "\n"
                + "			for (var index = 0; index < config.data.labels.length; ++index) {\n"
                + "				newDataset.data.push(randomScalingFactor());\n"
                + "			}\n"
                + "\n"
                + "			config.data.datasets.push(newDataset);\n"
                + "			window.myLine.update();\n"
                + "		});\n"
                + "	";

        s = s.replace("D1D1D1D1D1D1D1D1", getChartDataSeries1());
        s = s.replace("D2D2D2D2D2D2D2D2", getChartDataSeries2());
        s = s.replace("N1N1N1N1N1N1N1N1", getChartNameSeries());
        s = s.replace("My First dataset", getValues1Name());
        s = s.replace("My Second dataset", getValues2Name());

        String title = "[";
        int c = 0;
        for (String t : titles) {
            if (c > 0) {
                title = title + ", '" + t + "' ";
            } else {
                title = title + " '" + t + "' ";
            }

            c++;
        }
        title += "]";

        s = s.replaceAll("text: 'Chart.js Line Chart'",
                "text: " + title);

        return s;
    }

    public String getSingleLineChartString() {
        String s = "\n"
                + "		var MONTHS = [N1N1N1N1N1N1N1N1];\n"
                + "		var config = {\n"
                + "			type: 'line',\n"
                + "			data: {\n"
                + "				labels: [N1N1N1N1N1N1N1N1],\n"
                + "				datasets: [{\n"
                + "					label: 'My First dataset',\n"
                + "					backgroundColor: window.chartColors.red,\n"
                + "					borderColor: window.chartColors.red,\n"
                + "					data: [\n"
                + "						D1D1D1D1D1D1D1D1 \n"
                + "					],\n"
                + "					fill: false,\n"
                + "				}]\n"
                + "			},\n"
                + "			options: {\n"
                + "				responsive: true,\n"
                + "				title: {\n"
                + "					display: true,\n"
                + "					text: 'Chart.js Line Chart'\n"
                + "				},\n"
                + "				tooltips: {\n"
                + "					mode: 'index',\n"
                + "					intersect: false,\n"
                + "				},\n"
                + "				hover: {\n"
                + "					mode: 'nearest',\n"
                + "					intersect: true\n"
                + "				},\n"
                + "				scales: {\n"
                + "					xAxes: [{\n"
                + "						display: true,\n"
                + "						scaleLabel: {\n"
                + "							display: true,\n"
                + "							labelString: 'Month'\n"
                + "						}\n"
                + "					}],\n"
                + "					yAxes: [{\n"
                + "						display: true,\n"
                + "						scaleLabel: {\n"
                + "							display: true,\n"
                + "							labelString: 'Value'\n"
                + "						}\n"
                + "					}]\n"
                + "				}\n"
                + "			}\n"
                + "		};\n"
                + "\n"
                + "		window.onload = function() {\n"
                + "			var ctx = document.getElementById('canvas').getContext('2d');\n"
                + "			window.myLine = new Chart(ctx, config);\n"
                + "		};\n"
                + "\n"
                + "		document.getElementById('randomizeData').addEventListener('click', function() {\n"
                + "			config.data.datasets.forEach(function(dataset) {\n"
                + "				dataset.data = dataset.data.map(function() {\n"
                + "					return randomScalingFactor();\n"
                + "				});\n"
                + "\n"
                + "			});\n"
                + "\n"
                + "			window.myLine.update();\n"
                + "		});\n"
                + "\n"
                + "		var colorNames = Object.keys(window.chartColors);\n"
                + "		document.getElementById('addDataset').addEventListener('click', function() {\n"
                + "			var colorName = colorNames[config.data.datasets.length % colorNames.length];\n"
                + "			var newColor = window.chartColors[colorName];\n"
                + "			var newDataset = {\n"
                + "				label: 'Dataset ' + config.data.datasets.length,\n"
                + "				backgroundColor: newColor,\n"
                + "				borderColor: newColor,\n"
                + "				data: [],\n"
                + "				fill: false\n"
                + "			};\n"
                + "\n"
                + "			for (var index = 0; index < config.data.labels.length; ++index) {\n"
                + "				newDataset.data.push(randomScalingFactor());\n"
                + "			}\n"
                + "\n"
                + "			config.data.datasets.push(newDataset);\n"
                + "			window.myLine.update();\n"
                + "		});\n"
                + "	";

        s = s.replace("D1D1D1D1D1D1D1D1", getChartDataSeries1());
        s = s.replace("N1N1N1N1N1N1N1N1", getChartNameSeries());
        s = s.replace("My First dataset", getValues1Name());
        s = s.replace("Chart.js Line Chart", getChartName());
        return s;
    }

    public String getSingleLineChartString(String title, String values1Name, String chartNameSeries, String chartDataSeries1) {
        String s = "\n"
                + "		var MONTHS = [N1N1N1N1N1N1N1N1];\n"
                + "		var config = {\n"
                + "			type: 'line',\n"
                + "			data: {\n"
                + "				labels: [N1N1N1N1N1N1N1N1],\n"
                + "				datasets: [{\n"
                + "					label: 'My First dataset',\n"
                + "					backgroundColor: window.chartColors.red,\n"
                + "					borderColor: window.chartColors.red,\n"
                + "					data: [\n"
                + "						D1D1D1D1D1D1D1D1 \n"
                + "					],\n"
                + "					fill: false,\n"
                + "				}]\n"
                + "			},\n"
                + "			options: {\n"
                + "				responsive: true,\n"
                + "				title: {\n"
                + "					display: true,\n"
                + "					text: 'Chart.js Line Chart'\n"
                + "				},\n"
                + "				tooltips: {\n"
                + "					mode: 'index',\n"
                + "					intersect: false,\n"
                + "				},\n"
                + "				hover: {\n"
                + "					mode: 'nearest',\n"
                + "					intersect: true\n"
                + "				},\n"
                + "				scales: {\n"
                + "					xAxes: [{\n"
                + "						display: true,\n"
                + "						scaleLabel: {\n"
                + "							display: true,\n"
                + "							labelString: 'Month'\n"
                + "						}\n"
                + "					}],\n"
                + "					yAxes: [{\n"
                + "						display: true,\n"
                + "						scaleLabel: {\n"
                + "							display: true,\n"
                + "							labelString: 'Value'\n"
                + "						}\n"
                + "					}]\n"
                + "				}\n"
                + "			}\n"
                + "		};\n"
                + "\n"
                + "		window.onload = function() {\n"
                + "			var ctx = document.getElementById('canvas').getContext('2d');\n"
                + "			window.myLine = new Chart(ctx, config);\n"
                + "		};\n"
                + "\n"
                + "		document.getElementById('randomizeData').addEventListener('click', function() {\n"
                + "			config.data.datasets.forEach(function(dataset) {\n"
                + "				dataset.data = dataset.data.map(function() {\n"
                + "					return randomScalingFactor();\n"
                + "				});\n"
                + "\n"
                + "			});\n"
                + "\n"
                + "			window.myLine.update();\n"
                + "		});\n"
                + "\n"
                + "		var colorNames = Object.keys(window.chartColors);\n"
                + "		document.getElementById('addDataset').addEventListener('click', function() {\n"
                + "			var colorName = colorNames[config.data.datasets.length % colorNames.length];\n"
                + "			var newColor = window.chartColors[colorName];\n"
                + "			var newDataset = {\n"
                + "				label: 'Dataset ' + config.data.datasets.length,\n"
                + "				backgroundColor: newColor,\n"
                + "				borderColor: newColor,\n"
                + "				data: [],\n"
                + "				fill: false\n"
                + "			};\n"
                + "\n"
                + "			for (var index = 0; index < config.data.labels.length; ++index) {\n"
                + "				newDataset.data.push(randomScalingFactor());\n"
                + "			}\n"
                + "\n"
                + "			config.data.datasets.push(newDataset);\n"
                + "			window.myLine.update();\n"
                + "		});\n"
                + "	";

        s = s.replace("D1D1D1D1D1D1D1D1", chartDataSeries1);
        s = s.replace("N1N1N1N1N1N1N1N1", chartNameSeries);
        s = s.replace("My First dataset", values1Name);
        s = s.replaceAll("text: 'Chart.js Line Chart'",
                "text: '"
                + title
                + "'");
        return s;
    }

    public String getSingleLineChartString(String[] titles, String values1Name, String chartNameSeries, String chartDataSeries1) {
        String s = "\n"
                + "		var MONTHS = [N1N1N1N1N1N1N1N1];\n"
                + "		var config = {\n"
                + "			type: 'line',\n"
                + "			data: {\n"
                + "				labels: [N1N1N1N1N1N1N1N1],\n"
                + "				datasets: [{\n"
                + "					label: 'My First dataset',\n"
                + "					backgroundColor: window.chartColors.red,\n"
                + "					borderColor: window.chartColors.red,\n"
                + "					data: [\n"
                + "						D1D1D1D1D1D1D1D1 \n"
                + "					],\n"
                + "					fill: false,\n"
                + "				}]\n"
                + "			},\n"
                + "			options: {\n"
                + "				responsive: true,\n"
                + "				title: {\n"
                + "					display: true,\n"
                + "					text: 'Chart.js Line Chart'\n"
                + "				},\n"
                + "				tooltips: {\n"
                + "					mode: 'index',\n"
                + "					intersect: false,\n"
                + "				},\n"
                + "				hover: {\n"
                + "					mode: 'nearest',\n"
                + "					intersect: true\n"
                + "				},\n"
                + "				scales: {\n"
                + "					xAxes: [{\n"
                + "						display: true,\n"
                + "						scaleLabel: {\n"
                + "							display: true,\n"
                + "							labelString: 'Month'\n"
                + "						}\n"
                + "					}],\n"
                + "					yAxes: [{\n"
                + "						display: true,\n"
                + "						scaleLabel: {\n"
                + "							display: true,\n"
                + "							labelString: 'Value'\n"
                + "						}\n"
                + "					}]\n"
                + "				}\n"
                + "			}\n"
                + "		};\n"
                + "\n"
                + "		window.onload = function() {\n"
                + "			var ctx = document.getElementById('canvas').getContext('2d');\n"
                + "			window.myLine = new Chart(ctx, config);\n"
                + "		};\n"
                + "\n"
                + "		document.getElementById('randomizeData').addEventListener('click', function() {\n"
                + "			config.data.datasets.forEach(function(dataset) {\n"
                + "				dataset.data = dataset.data.map(function() {\n"
                + "					return randomScalingFactor();\n"
                + "				});\n"
                + "\n"
                + "			});\n"
                + "\n"
                + "			window.myLine.update();\n"
                + "		});\n"
                + "\n"
                + "		var colorNames = Object.keys(window.chartColors);\n"
                + "		document.getElementById('addDataset').addEventListener('click', function() {\n"
                + "			var colorName = colorNames[config.data.datasets.length % colorNames.length];\n"
                + "			var newColor = window.chartColors[colorName];\n"
                + "			var newDataset = {\n"
                + "				label: 'Dataset ' + config.data.datasets.length,\n"
                + "				backgroundColor: newColor,\n"
                + "				borderColor: newColor,\n"
                + "				data: [],\n"
                + "				fill: false\n"
                + "			};\n"
                + "\n"
                + "			for (var index = 0; index < config.data.labels.length; ++index) {\n"
                + "				newDataset.data.push(randomScalingFactor());\n"
                + "			}\n"
                + "\n"
                + "			config.data.datasets.push(newDataset);\n"
                + "			window.myLine.update();\n"
                + "		});\n"
                + "	";

        String title = "[";
        int c = 0;
        for (String t : titles) {
            if (c > 0) {
                title = title + ", '" + t + "' ";
            } else {
                title = title + " '" + t + "' ";
            }

            c++;
        }
        title += "]";

        s = s.replace("D1D1D1D1D1D1D1D1", chartDataSeries1);
        s = s.replace("N1N1N1N1N1N1N1N1", chartNameSeries);
        s = s.replace("My First dataset", values1Name);
        s = s.replaceAll("text: 'Chart.js Line Chart'",
                "text: " + title);
        return s;
    }

    public String getCurrentPatientEncountersDateStrings() {
        String s = "";
        int i = 0;
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");

        List<PatientEncounter> re = new ArrayList<>();
        re.addAll(getCurrentPatientEncounters());
        Collections.reverse(re);

        for (PatientEncounter e : re) {
            i++;
            s += "'" + format.format(e.getCreatedAt()) + "'";
            if (i != getCurrentPatientEncounters().size()) {
                s += ", ";
            }
        }
        return s;
    }

    public String getCurrentPatientEncountersSbpStrings() {
        String val = "";
        int i = 0;

        List<PatientEncounter> re = new ArrayList<>();
        re.addAll(getCurrentPatientEncounters());
        Collections.reverse(re);

        for (PatientEncounter e : re) {
            i++;
            val += e.getSbp();
            if (i != getCurrentPatientEncounters().size()) {
                val += ", ";
            }
        }
        return val;
    }

    public String getCurrentPatientEncountersDbpStrings() {
        String s = "";
        int i = 0;

        List<PatientEncounter> re = new ArrayList<>();
        re.addAll(getCurrentPatientEncounters());
        Collections.reverse(re);

        for (PatientEncounter e : re) {
            i++;
            s += e.getDbp();
            if (i != getCurrentPatientEncounters().size()) {
                s += ", ";
            }
        }
        return s;
    }

    public String getCurrentPatientEncountersHeightStrings() {
        String s = "";
        int i = 0;

        List<PatientEncounter> re = new ArrayList<>();
        re.addAll(getCurrentPatientEncounters());
        Collections.reverse(re);

        for (PatientEncounter e : re) {
            i++;
            s += e.getHeight();
            if (i != getCurrentPatientEncounters().size()) {
                s += ", ";
            }
        }
        return s;
    }

    public String getCurrentPatientEncountersWeightStrings() {
        String s = "";
        int i = 0;

        List<PatientEncounter> re = new ArrayList<>();
        re.addAll(getCurrentPatientEncounters());
        Collections.reverse(re);

        for (PatientEncounter e : re) {
            i++;
            s += e.getWeight();
            if (i != getCurrentPatientEncounters().size()) {
                s += ", ";
            }
        }
        return s;
    }

    public List<ItemUsage> getCurrentPatientAllergies() {
        return currentPatientAllergies;
    }

    public List<PatientEncounter> fillCurrentPatientEncounters(PatientEncounter pe) {
        Map m = new HashMap();
        m.put("p", pe.getPatient());
        m.put("pe", pe);
        String sql;
        sql = "Select e from PatientEncounter e where e.patient=:p and e!=:pe order by e.id desc";
        return getFacade().findBySQL(sql, m);
    }

    public List<ItemUsage> fillCurrentPatientAllergies() {
        Map m = new HashMap();
        m.put("p", getCurrent().getPatient());
        m.put("t", ItemUsageType.Allergies);
        String sql;
        sql = "Select e "
                + " from ItemUsage e "
                + " where e.patient=:p "
                + " and e.type=:t "
                + " order by e.id desc";
        return itemUsageFacade.findBySQL(sql, m);
    }

    public void fillCurrentPatientLists(Patient patient) {
        currentPatientEncounters = fillPatientEncounters(patient);
        currentPatientBills = fillPatientBills(patient);
        currentChannelBills = fillPatientChannelBills(patient);
        currentPatientInvestigations = fillPatientInvestigations(patient);
        currentPatientAllergies = fillCurrentPatientAllergies();
    }

    public List<Bill> fillPatientBills(Patient patient) {
        Map m = new HashMap();
        m.put("p", patient);
        m.put("bt1", BillType.OpdBill);
        m.put("bt2", BillType.PharmacySale);
        String sql;
        Bill b = new Bill();
        b.getBillType();
        sql = "Select e from Bill e where e.patient=:p and (e.billType=:bt1 or e.billType=:bt2 ) order by e.id desc";
        return getBillFacade().findBySQL(sql, m);
    }

    public List<Bill> fillPatientChannelBills(Patient patient) {
        Map m = new HashMap();
        m.put("p", patient);
        BillType[] bts = {BillType.ChannelCash, BillType.ChannelAgent, BillType.ChannelStaff, BillType.ChannelOnCall};
        List<BillType> billTypes = Arrays.asList(bts);
        m.put("bts", billTypes);
        String sql;
        sql = "Select b from Bill b where b.patient=:p and b.billType in :bts order by b.id desc";
        return getBillFacade().findBySQL(sql, m);
    }

    public void fillPatientInvestigations() {
        currentPatientInvestigations = fillPatientInvestigations(current.getPatient());
    }

    public List<PatientInvestigation> fillPatientInvestigations(Patient patient) {
        Map m = new HashMap();
        m.put("p", patient);
        String sql;
        sql = "Select e from PatientInvestigation e where e.patient=:p order by e.id desc";
        return getPiFacade().findBySQL(sql, m);
    }

    public List<PatientReportItemValue> fillPatientReportItemValue(Patient patient, InvestigationItem ii) {
        Map m = new HashMap();
        m.put("p", patient);
        m.put("it", ii);
        String sql;
        sql = "Select v "
                + " from PatientReportItemValue v "
                + " where "
                + " v.patientReport.patientInvestigation.patient=:p "
                + " and v.investigationItem=:it "
                + " order by v.patientReport.patientInvestigation.id";
        return getPiFacade().findBySQL(sql, m);
    }

    public List<PatientEncounter> fillPatientEncounters(Patient patient) {
        //   ////System.out.println("fill current patient encounters");
        Map m = new HashMap();
        m.put("p", patient);
        String sql;
        sql = "Select e from PatientEncounter e where e.patient=:p order by e.id desc";
        return getFacade().findBySQL(sql, m);
    }

    public void removeCfv() {
        if (current == null) {
            UtilityController.addErrorMessage("No Patient Encounter");
            return;
        }
        if (removingCfv == null) {
            UtilityController.addErrorMessage("No Finding selected to remove");
            return;
        }
        current.getClinicalFindingValues().remove(removingCfv);
        current.getDiagnosis().remove(removingCfv);
        saveSelected();
        UtilityController.addSuccessMessage("Removed");
    }

    public ClinicalFindingItem getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(ClinicalFindingItem diagnosis) {
        this.diagnosis = diagnosis;
    }

    public Investigation getInvestigation() {
        return investigation;
    }

    public void setInvestigation(Investigation investigation) {
        this.investigation = investigation;
    }

    public void prepareAdd() {
        setCurrent(new PatientEncounter());
    }

    public String getSelectText() {
        return selectText;
    }

    private void recreateModel() {
        items = null;
    }

    public void saveSelected() {
        current.setDateTime(new Date());
        current.setDepartment(sessionController.getDepartment());
        if (getCurrent().getId() != null && getCurrent().getId() > 0) {
            if (sessionController.hasPrivilege(Privileges.ClinicalPatientEdit)) {
                getFacade().edit(current);
                UtilityController.addSuccessMessage("Updated Successfully.");
            } else {
                UtilityController.addSuccessMessage("You have no privilege to change patient details.");

            }
        } else {
            current.setCreatedAt(new Date());
            current.setCreater(getSessionController().getLoggedUser());
            getFacade().create(current);
            UtilityController.addSuccessMessage("Saved Successfully");
        }
        UtilityController.addSuccessMessage("Saved");
    }

    public void updateComments() {
        //   ////System.out.println("updating comments");
        //   ////System.out.println("current.getComments() = " + current.getComments());
        if (getCurrent().getId() != null && getCurrent().getId() > 0) {
            getFacade().edit(current);
        } else {
            current.setCreatedAt(new Date());
            current.setCreater(getSessionController().getLoggedUser());
            getFacade().create(current);
        }
    }

    public void updatePerson() {
        //   ////System.out.println("updating person");
        if (current == null) {
            //   ////System.out.println("current = " + current);
            return;
        }
        if (current.getPatient() == null) {
            //   ////System.out.println("current.getPatient()  = " + current.getPatient());
            return;
        }
        if (current.getPatient().getPerson() == null) {
            //   ////System.out.println("current.getPatient().getPerson() = " + current.getPatient().getPerson());
            return;
        }
        if (sessionController.hasPrivilege(Privileges.ClinicalPatientEdit)) {
            getPersonFacade().edit(current.getPatient().getPerson());
            getPatientFacade().edit(current.getPatient());
            JsfUtil.addSuccessMessage("Patient Details Updated.");
        } else {
            JsfUtil.addErrorMessage("You have no Privilege to Edit Patient Details");
            return;
        }

    }

    public String issueItems() {
        if (current == null) {
            return "";
        }
        getPharmacySaleController().setSearchedPatient(current.getPatient());
        getPharmacySaleController().setPatientSearchTab(1);
        getPharmacySaleController().setOpdEncounterComments(current.getComments());
        getPharmacySaleController().setFromOpdEncounter(true);
        getPharmacySaleController().setPatientTabId("tabSearchPt");
//        getPharmacySaleController().getBill().setPatientEncounter(current);
//        getPharmacySaleController().getBill().setPatient(current.getPatient());
        return "/clinical/clinical_pharmacy_sale";
    }

    public String issueServices() {
        if (current == null) {
            return "";
        }
        getBillController().prepareNewBill();
        getBillController().setSearchedPatient(current.getPatient());
        getBillController().setFromOpdEncounter(true);
        getBillController().setOpdEncounterComments(current.getComments());
        getBillController().setPatientSearchTab(1);
        getBillController().setPatientTabId("tabSearchPt");
        getBillController().setReferredBy(doctor);
        //        getPharmacySaleController().getBill().setPatientEncounter(current);
        //        getPharmacySaleController().getBill().setPatient(current.getPatient());
        return "/opd_bill";
    }

    public PatientEncounter getEncounterToDisplay() {
        return encounterToDisplay;
    }

    public void setEncounterToDisplay(PatientEncounter encounterToDisplay) {
        this.encounterToDisplay = encounterToDisplay;
    }

    public PatientEncounter getStartedEncounter() {
        return startedEncounter;
    }

    public void setStartedEncounter(PatientEncounter startedEncounter) {
        this.startedEncounter = startedEncounter;
    }

    public String prepareToDisplayPastVisit() {
        if (current == null) {
            JsfUtil.addErrorMessage("No visit");
            return "";
        }
        if (encounterToDisplay == null) {
            JsfUtil.addErrorMessage("Select Visit");
            return "";
        }
        setCurrent(encounterToDisplay);
        return "";
    }

    public void backToStartingEncounter() {
        if (startedEncounter == null) {
            JsfUtil.addErrorMessage("No visit");
            return;
        }
        setCurrent(startedEncounter);
    }

    public void setSelectText(String selectText) {
        this.selectText = selectText;
    }

    public PatientEncounterFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(PatientEncounterFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public PatientEncounterController() {
    }

    public PatientEncounter getCurrent() {
        if (current == null) {
            current = new PatientEncounter();
            Patient pt = new Patient();
            Person p = new Person();
            pt.setPerson(p);
            current.setPatient(pt);
        }
        return current;
    }

    public void setCurrent(PatientEncounter current) {
        if (this.current == current) {
            return;
        }
        this.current = current;
        if (this != null) {
            fillCurrentPatientLists(current.getPatient());
        }
    }

    public void delete() {

        if (current != null) {
            current.setRetired(true);
            current.setRetiredAt(new Date());
            current.setRetirer(getSessionController().getLoggedUser());
            getFacade().edit(current);
            UtilityController.addSuccessMessage("Deleted Successfully");
        } else {
            UtilityController.addSuccessMessage("Nothing to Delete");
        }
        recreateModel();
        getItems();
        current = null;
        getCurrent();
    }

    private PatientEncounterFacade getFacade() {
        return ejbFacade;
    }

    public List<PatientEncounter> getItems() {
        return items;
    }

    public ItemUsageFacade getItemUsageFacade() {
        return itemUsageFacade;
    }

    public WebUserController getWebUserController() {
        return webUserController;
    }

    public String getDiagnosisComments() {
        return diagnosisComments;
    }

    public void setDiagnosisComments(String diagnosisComments) {
        this.diagnosisComments = diagnosisComments;
    }

    public ClinicalFindingValue getRemovingCfv() {
        return removingCfv;
    }

    public void setRemovingCfv(ClinicalFindingValue removingCfv) {
        this.removingCfv = removingCfv;
    }

    public PharmacySaleController getPharmacySaleController() {
        return pharmacySaleController;
    }

    public Date getFromDate() {
        if (fromDate == null) {
            fromDate = new Date();
//            fromDate = getCommonFunctions().getStartOfDay(fromDate);
        }
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        if (toDate == null) {
            toDate = new Date();
//            toDate = getCommonFunctions().getEndOfDay(toDate);
        }
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public ClinicalFindingItemFacade getClinicalFindingItemFacade() {
        return clinicalFindingItemFacade;
    }

    public BillController getBillController() {
        return billController;
    }

    public PersonFacade getPersonFacade() {
        return personFacade;
    }

    public PatientFacade getPatientFacade() {
        return patientFacade;
    }

    public CommonFunctionsController getCommonFunctions() {
        return commonFunctions;
    }

    public BillFacade getBillFacade() {
        return billFacade;
    }

    public PatientInvestigationFacade getPiFacade() {
        return piFacade;
    }

    public List<Bill> getCurrentPatientBills() {
        return currentPatientBills;
    }

    public List<PatientInvestigation> getCurrentPatientInvestigations() {
        return currentPatientInvestigations;
    }

    public List<String> getCompleteStrings() {
        return completeStrings;
    }

    public void setCompleteStrings(List<String> completeStrings) {
        this.completeStrings = completeStrings;
    }

    public List<Bill> getCurrentChannelBills() {
        return currentChannelBills;
    }

    public void setCurrentChannelBills(List<Bill> currentChannelBills) {
        this.currentChannelBills = currentChannelBills;
    }

    public List<PatientEncounter> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<PatientEncounter> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String getChartNameSeries() {
        return chartNameSeries;
    }

    public void setChartNameSeries(String chartNameSeries) {
        this.chartNameSeries = chartNameSeries;
    }

    public String getChartDataSeries1() {
        return chartDataSeries1;
    }

    public void setChartDataSeries1(String chartDataSeries1) {
        this.chartDataSeries1 = chartDataSeries1;
    }

    public String getChartDataSeries2() {
        return chartDataSeries2;
    }

    public void setChartDataSeries2(String chartDataSeries2) {
        this.chartDataSeries2 = chartDataSeries2;
    }

    public String getChartName() {
        return chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    public String getValues1Name() {
        return values1Name;
    }

    public void setValues1Name(String values1Name) {
        this.values1Name = values1Name;
    }

    public String getValues2Name() {
        return values2Name;
    }

    public void setValues2Name(String values2Name) {
        this.values2Name = values2Name;
    }

    public String getChartString() {
        return chartString;
    }

    public void setChartString(String chartString) {
        this.chartString = chartString;
    }

    public PatientReportItemValueFacade getPrivFacade() {
        return privFacade;
    }

    public InvestigationItem getGraphInvestigationItem() {
        return graphInvestigationItem;
    }

    public void setGraphInvestigationItem(InvestigationItem graphInvestigationItem) {
        this.graphInvestigationItem = graphInvestigationItem;
    }

}

enum ClinicalField {

    History,
    Examination,
    Investigations,
    Treatments,
    Management,
}
