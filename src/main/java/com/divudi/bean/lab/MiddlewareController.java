/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.bean.lab;

import com.divudi.bean.common.CommonController;
import com.divudi.ejb.CommonFunctions;
import com.divudi.entity.lab.MiddlewareMessage;
import com.divudi.facade.MiddlewareMessageFacade;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

/**
 *
 * @author chrishantha
 */
@Named(value = "middlewareController")
@SessionScoped
public class MiddlewareController implements Serializable {

    
    @EJB
    MiddlewareMessageFacade mwmFacade;
    private Date fromDate;
    private Date toDate;
    /**
     * Creates a new instance of MiddlewareController
     */
    public MiddlewareController() {
    }
    
    public void fillMessage(){
        String j = "select m "
                + " from MiddlewareMessage m "
                + " where m.receivedAt between :fd and :td "
                + " order by m.id desc";
        Map m = new HashMap();
        m.put("fd", fromDate);
        m.put("td", toDate);
        items = getMwmFacade().findBySQL(j, m);
    }
    
    private List<MiddlewareMessage> items ;
    private MiddlewareMessage selected;

    
    
    public MiddlewareMessageFacade getMwmFacade() {
        return mwmFacade;
    }

    public List<MiddlewareMessage> getItems() {
        return items;
    }

    public void setItems(List<MiddlewareMessage> items) {
        this.items = items;
    }

    public MiddlewareMessage getSelected() {
        return selected;
    }

    public void setSelected(MiddlewareMessage selected) {
        this.selected = selected;
    }

    public Date getFromDate() {
        if(fromDate==null){
            fromDate = CommonFunctions.getStartOfMonth();
        }
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        if(toDate==null){
            toDate = CommonFunctions.getEndOfDay();
        }
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
    
    
    
    
}
