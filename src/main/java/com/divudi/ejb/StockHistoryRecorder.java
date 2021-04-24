/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.ejb;

import com.divudi.data.FeeType;
import com.divudi.data.HistoryType;
import com.divudi.entity.Department;
import com.divudi.entity.FeeChange;
import com.divudi.entity.Item;
import com.divudi.entity.ItemFee;
import com.divudi.entity.ServiceSession;
import com.divudi.entity.Staff;
import com.divudi.entity.pharmacy.Ampp;
import com.divudi.entity.pharmacy.StockHistory;
import com.divudi.facade.AmpFacade;
import com.divudi.facade.DepartmentFacade;
import com.divudi.facade.FeeChangeFacade;
import com.divudi.facade.ItemFacade;
import com.divudi.facade.ItemFeeFacade;
import com.divudi.facade.PharmaceuticalItemFacade;
import com.divudi.facade.StockFacade;
import com.divudi.facade.StockHistoryFacade;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TemporalType;

/**
 *
 * @author Buddhika
 */
@Stateless
public class StockHistoryRecorder {

    @EJB
    StockFacade StockFacade;
    @EJB
    DepartmentFacade departmentFacade;
    @EJB
    ItemFacade itemFacade;
    @EJB
    PharmaceuticalItemFacade piFacade;
    @EJB
    AmpFacade ampFacade;
    @EJB
    StockHistoryFacade stockHistoryFacade;
    @EJB
    FeeChangeFacade feeChangeFacade;
    @EJB
    ItemFeeFacade itemFeeFacade;

    @Inject
    CommonFunctions commonFunctions;

    @SuppressWarnings("unused")
//    @Schedule(minute = "1", second = "1", dayOfMonth = "*", month = "*", year = "*", hour = "1", persistent = false)
//    @Schedule(minute = "*", second = "10", dayOfMonth = "*", month = "*", year = "*", hour = "*", persistent = false)
    @Schedule(minute = "59", second = "59", hour = "23", dayOfMonth = "Last", info = "2nd Scheduled Timer", persistent = false)
//    @Schedule(second="*/1", minute="*",hour="*", persistent=false)
    public void myTimer() {
        Date startTime = new Date();
        ////System.out.println("Start writing stock history: " + startTime);
        for (Department d : fetchStockDepartment()) {
            if (!d.isRetired()) {
                for (Item amp : fetchStockItem(d)) {
                    if (!amp.isRetired()) {
                        StockHistory h = new StockHistory();
                        h.setFromDate(startTime);
                        h.setHistoryType(HistoryType.MonthlyRecord);
                        h.setDepartment(d);
                        h.setItem(amp);
                        h.setStockQty(getStockQty(amp, d));
                        h.setStockPurchaseValue(getStockPurchaseValue(amp, d));
                        h.setStockSaleValue(getStockRetailSaleValue(amp, d));
                        //SET DATE DATAS
                        h.setHxDate(Calendar.getInstance().get(Calendar.DATE));
                        h.setHxMonth(Calendar.getInstance().get(Calendar.MONTH));
                        h.setHxWeek(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
                        h.setHxYear(Calendar.getInstance().get(Calendar.YEAR));
                        h.setStockAt(startTime);
                        h.setCreatedAt(startTime);

                        getStockHistoryFacade().create(h);
                    }
                }
                ////System.out.println("hx finished for = " + d);
            }
        }
        ////System.out.println("End writing stock history: " + new Date());
//        ////System.out.println("TIme taken for Hx is " + (((new Date()) - startTime )/(1000*60*60)) + " minutes.");
    }

    @SuppressWarnings("unused")
    @Schedule(hour = "23", minute = "59", second = "59", dayOfMonth = "*", info = "Daily Mid Night", persistent = false)
    public void myTimerDaily() {
        Date startTime = new Date();
        for (FeeChange fc : fetchFeeChanges()) {
            if (fc.getFee().getStaff() != null) {
            }
            for (ItemFee f : fetchServiceSessionFees(fc.getFee().getFeeType(), fc.getFee().getName(), fc.getFee().getStaff())) {
                f.setFee(f.getFee() + fc.getFee().getFee());
                f.setFfee(f.getFfee() + fc.getFee().getFfee());
                getItemFeeFacade().edit(f);
            }
            fc.setDoneAt(new Date());
            fc.setDone(true);
            getFeeChangeFacade().edit(fc);
        }
        ////System.out.println("End writing stock history: " + new Date());
//        ////System.out.println("TIme taken for Hx is " + (((new Date()) - startTime )/(1000*60*60)) + " minutes.");
    }
    
    public List<Department> fetchStockDepartment() {
        String sql;
        Map m = new HashMap();
        sql = "select distinct(s.department) from Stock s order by s.department.name";
        return departmentFacade.findBySQL(sql, m);
    }

    public List<Item> fetchStockItem(Department department) {
        String sql;
        Map m = new HashMap();
        sql = "select distinct(s.itemBatch.item) from Stock s"
                + " where s.department=:dep "
                + "  order by s.itemBatch.item.name";
        m.put("dep", department);
        return itemFacade.findBySQL(sql, m);
    }

    public double getStockQty(Item item, Department department) {
        if (item instanceof Ampp) {
            item = ((Ampp) item).getAmp();
        }
        String sql;
        Map m = new HashMap();
        m.put("d", department);
        m.put("i", item);
        sql = "select sum(s.stock) from Stock s where s.department=:d and s.itemBatch.item=:i";
        return getStockFacade().findDoubleByJpql(sql, m);
    }

    public double getStockRetailSaleValue(Item item, Department department) {
        if (item instanceof Ampp) {
            item = ((Ampp) item).getAmp();
        }
        String sql;
        Map m = new HashMap();
        m.put("d", department);
        m.put("i", item);
        sql = "select sum(s.stock * s.itemBatch.retailsaleRate) from Stock s where s.department=:d and s.itemBatch.item=:i";
        return getStockFacade().findDoubleByJpql(sql, m);
    }

    public double getStockPurchaseValue(Item item, Department department) {
        if (item instanceof Ampp) {
            item = ((Ampp) item).getAmp();
        }
        String sql;
        Map m = new HashMap();
        m.put("d", department);
        m.put("i", item);
        sql = "select sum(s.stock * s.itemBatch.purcahseRate) from Stock s where s.department=:d and s.itemBatch.item=:i";
        return getStockFacade().findDoubleByJpql(sql, m);
    }

    public List<FeeChange> fetchFeeChanges() {
        String sql;
        Map m = new HashMap();
        sql = " select fc from FeeChange fc where "
                + " fc.retired=false "
                + " and fc.validFrom=:ed "
                + " and fc.done!=true ";
        m.put("ed", getCommonFunctions().getEndOfDay(new Date()));
        List<FeeChange> changes = getFeeChangeFacade().findBySQL(sql, m, TemporalType.DATE);
        return changes;
    }

    public List<ItemFee> fetchServiceSessionFees(FeeType ft, String s, Staff staff) {
        String sql;
        Map m = new HashMap();
        sql = "Select f from ItemFee f "
                + " where f.retired=false "
                + " and type(f.serviceSession)=:type "
                + " and f.serviceSession.originatingSession is null "
                + " and f.feeType=:ft "
                + " and f.serviceSession.staff=:staff "
                + " and f.name=:a ";

        if ((ft == FeeType.Service && s.equals("Scan Fee")) || (ft == FeeType.OwnInstitution && s.equals("Hospital Fee"))) {
            sql += " and (f.fee>0 or f.ffee>0) ";
        }

        sql += " order by f.id";
        m.put("type", ServiceSession.class);
        m.put("ft", ft);
        m.put("a", s);
        m.put("staff", staff);
        List<ItemFee> itemFees = getItemFeeFacade().findBySQL(sql, m);
        return itemFees;
    }

// Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public StockFacade getStockFacade() {
        return StockFacade;
    }

    public void setStockFacade(StockFacade StockFacade) {
        this.StockFacade = StockFacade;
    }

    public DepartmentFacade getDepartmentFacade() {
        return departmentFacade;
    }

    public void setDepartmentFacade(DepartmentFacade departmentFacade) {
        this.departmentFacade = departmentFacade;
    }

    public ItemFacade getItemFacade() {
        return itemFacade;
    }

    public void setItemFacade(ItemFacade itemFacade) {
        this.itemFacade = itemFacade;
    }

    public PharmaceuticalItemFacade getPiFacade() {
        return piFacade;
    }

    public void setPiFacade(PharmaceuticalItemFacade piFacade) {
        this.piFacade = piFacade;
    }

    public AmpFacade getAmpFacade() {
        return ampFacade;
    }

    public void setAmpFacade(AmpFacade ampFacade) {
        this.ampFacade = ampFacade;
    }

    public StockHistoryFacade getStockHistoryFacade() {
        return stockHistoryFacade;
    }

    public void setStockHistoryFacade(StockHistoryFacade stockHistoryFacade) {
        this.stockHistoryFacade = stockHistoryFacade;
    }

    public FeeChangeFacade getFeeChangeFacade() {
        return feeChangeFacade;
    }

    public void setFeeChangeFacade(FeeChangeFacade feeChangeFacade) {
        this.feeChangeFacade = feeChangeFacade;
    }

    public CommonFunctions getCommonFunctions() {
        return commonFunctions;
    }

    public void setCommonFunctions(CommonFunctions commonFunctions) {
        this.commonFunctions = commonFunctions;
    }

    public ItemFeeFacade getItemFeeFacade() {
        return itemFeeFacade;
    }

    public void setItemFeeFacade(ItemFeeFacade itemFeeFacade) {
        this.itemFeeFacade = itemFeeFacade;
    }

}
