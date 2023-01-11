/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.bean.common;

import com.divudi.data.ApplicationInstitution;
import com.divudi.data.MessageType;
import com.divudi.data.hr.ReportKeyWord;
import com.divudi.ejb.CommonFunctions;
import com.divudi.entity.Bill;
import com.divudi.entity.Sms;
import com.divudi.facade.SmsFacade;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.TemporalType;

/**
 *
 * @author Dushan
 */
@Named(value = "smsController")
@SessionScoped
public class SmsController implements Serializable {

    @EJB
    SmsFacade smsFacade;

    @Inject
    SessionController sessionController;
    @Inject
    CommonFunctions commonFunctions;

    List<Sms> smses;
    List<SmsSummeryRow> smsSummeryRows;

    ReportKeyWord reportKeyWord;

    private Date from;
    private Date to;

    public void fillSuccess() {
        fillSuccess(null, true);;
    }

    public void fillLabSuccess() {
        List<MessageType> ts = new ArrayList<>();
        ts.add(MessageType.LabReport);
        ts.add(MessageType.LabReportAll);
        fillSuccess(ts, true);;
    }

    public void fillLabFail() {
        List<MessageType> ts = new ArrayList<>();
        ts.add(MessageType.LabReport);
        ts.add(MessageType.LabReportAll);
        fillSuccess(ts, false);;
    }

    public void fillChannelSuccess() {
        List<MessageType> ts = new ArrayList<>();
        ts.add(MessageType.ChannelBooking);
        ts.add(MessageType.ChannelCancellation);
        ts.add(MessageType.ChannelCustom);
        ts.add(MessageType.ChannelDoctorAraival);
        ts.add(MessageType.ChannelReminder);
        fillSuccess(ts, true);;
    }

    public void fillChannelFail() {
        List<MessageType> ts = new ArrayList<>();
        ts.add(MessageType.ChannelBooking);
        ts.add(MessageType.ChannelCancellation);
        ts.add(MessageType.ChannelCustom);
        ts.add(MessageType.ChannelDoctorAraival);
        ts.add(MessageType.ChannelReminder);
        fillSuccess(ts, false);;
    }

    public void fillSuccess(List<MessageType> ts, boolean success) {
        String j = "Select e "
                + " from Sms e "
                + " where e.sentSuccessfully=:suc "
                + " and e.retired=false "
                + " and e.retired=false "
                + " and e.createdAt between :fd and :td ";

        Map m = new HashMap();
        m.put("fd", from);
        m.put("td", to);
        m.put("suc", success);
        if (ts != null) {
            j += " and e.smsType in :ts ";
            m.put("ts", ts);
        }

        j += " order by e.createdAt";
        smses = getSmsFacade().findBySQL(j, m);
    }

    public void fillFailed() {
        fillSuccess(null, false);
    }

    public String toFailed() {
        smses = null;
        return "/sms/failed";
    }

    public String toSuccess() {
        smses = null;
        return "/sms/success";
    }
    
    public String toFailedLab() {
        smses = null;
        return "/sms/failed_lab";
    }

    public String toSuccessLab() {
        smses = null;
        return "/sms/success_lab";
    }
    
    public String toFailedChannel() {
        smses = null;
        return "/sms/failed_channel";
    }

    public String toSuccessChannel() {
        smses = null;
        return "/sms/success_channel";
    }

    /**
     * Creates a new instance of SmsController
     */
    public SmsController() {
    }

    public void createSmsTable() {
        long lng = getCommonFunctions().getDayCount(getReportKeyWord().getFromDate(), getReportKeyWord().getToDate());

        if (Math.abs(lng) > 2 && !getReportKeyWord().isAdditionalDetails()) {
            UtilityController.addErrorMessage("Date Range is too Long");
            return;
        }
        String sql;
        Map m = new HashMap();
        smsSummeryRows = new ArrayList<>();
        smses = new ArrayList<>();

        if (getReportKeyWord().isAdditionalDetails()) {
            sql = " select s.smsType, count(s) ";
        } else {
            sql = " select s ";
        }
        sql += " from Sms s where s.retired=false "
                + " and s.createdAt between :fd and :td ";

        if (getReportKeyWord().getSmsType() != null) {
            sql += " and s.smsType=:st ";
            m.put("st", getReportKeyWord().getSmsType());
        }

        if (getReportKeyWord().isAdditionalDetails()) {
            sql += " group by s.smsType ";
        } else {
            sql += " order by s.id ";
        }

        m.put("fd", getReportKeyWord().getFromDate());
        m.put("td", getReportKeyWord().getToDate());

        if (getReportKeyWord().isAdditionalDetails()) {
            List<Object[]> objects = getSmsFacade().findAggregates(sql, m, TemporalType.TIMESTAMP);
            long l = 0l;
            for (Object[] ob : objects) {
                SmsSummeryRow row = new SmsSummeryRow();
                MessageType smsType = (MessageType) ob[0];
                long count = (long) ob[1];
                row.setSmsType(smsType);
                row.setCount(count);
                l += count;
                smsSummeryRows.add(row);
            }
            SmsSummeryRow row = new SmsSummeryRow();
            row.setSmsType(null);
            row.setCount(l);
            smsSummeryRows.add(row);
        } else {
            smses = getSmsFacade().findBySQL(sql, m, TemporalType.TIMESTAMP);
        }

    }

    public List<SmsSummeryRow> getSmsSummeryRows() {
        return smsSummeryRows;
    }

    public void setSmsSummeryRows(List<SmsSummeryRow> smsSummeryRows) {
        this.smsSummeryRows = smsSummeryRows;
    }

    public CommonFunctions getCommonFunctions() {
        return commonFunctions;
    }

    public void setCommonFunctions(CommonFunctions commonFunctions) {
        this.commonFunctions = commonFunctions;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public class SmsSummeryRow {

        MessageType smsType;
        long count;

        public MessageType getSmsType() {
            return smsType;
        }

        public void setSmsType(MessageType smsType) {
            this.smsType = smsType;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }

    //---------Getters and Setters
    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public SmsFacade getSmsFacade() {
        return smsFacade;
    }

    public void setSmsFacade(SmsFacade smsFacade) {
        this.smsFacade = smsFacade;
    }

    public ReportKeyWord getReportKeyWord() {
        if (reportKeyWord == null) {
            reportKeyWord = new ReportKeyWord();
        }
        return reportKeyWord;
    }

    public void setReportKeyWord(ReportKeyWord reportKeyWord) {
        this.reportKeyWord = reportKeyWord;
    }

    public List<Sms> getSmses() {
        return smses;
    }

    public void setSmses(List<Sms> smses) {
        this.smses = smses;
    }

}
