/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.bean.clinical;

import com.divudi.bean.common.SessionController;
import com.divudi.bean.pharmacy.DoseUnitController;
import com.divudi.bean.pharmacy.VmpController;
import com.divudi.data.clinical.ItemUsageType;
import com.divudi.entity.Item;
import com.divudi.entity.clinical.ItemUsage;
import com.divudi.entity.pharmacy.MeasurementUnit;
import com.divudi.facade.ItemUsageFacade;
import com.divudi.facade.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author buddhika
 */
@Named
@SessionScoped
public class FavouriteController implements Serializable {

    /**
     * EJBs
     */
    @EJB
    ItemUsageFacade favouriteItemFacade;
    /**
     * Controllers
     */
    @Inject
    SessionController sessionController;
    @Inject
    DoseUnitController doseUnitController;
    @Inject
    VmpController vmpController;
    /**
     * Properties
     */
    Item item;
    ItemUsage current;
    List<ItemUsage> items;
    private List<MeasurementUnit> availableDoseUnits;
    private List<Item> availableItems;

    /**
     * Methods
     */
    public void fillFavouriteMedicines() {
        fillFavouriteItems(item, ItemUsageType.FavouriteMedicine);
    }

    public void fillFavouriteDisgnosis() {
        fillFavouriteItems(item, ItemUsageType.FavouriteDiagnosis);
    }

    public String toAddFavDig() {
        item = null;
        items = null;
        current = null;
        return "/clinical/clinical_favourite_diagnosis";
    }

    public String toAddFavItem() {
        item = null;
        items = null;
        current = null;
        return "/clinical/clinical_favourite_item";
    }

    public void fillFavouriteItems(Item forItem, ItemUsageType type) {
        items = listFavouriteItems(forItem, type);
    }

    public List<ItemUsage> listFavouriteItems(Item forItem, ItemUsageType type) {
        return listFavouriteItems(forItem, type, null);
    }

    public List<ItemUsage> listFavouriteItems(Item forItem, ItemUsageType type, Double weight) {
        System.out.println("listFavouriteItems");
        return listFavouriteItems(forItem, type, weight, null);
    }
    
    public List<ItemUsage> listFavouriteItems( ItemUsageType type, Double weight, Long ageInDays) {
        return listFavouriteItems(null, type, weight, ageInDays);
    }
    
    public List<ItemUsage> listFavouriteItems(Item forItem, ItemUsageType type, Double weight, Long ageInDays) {
        System.out.println("listFavouriteItems");
        String j;
        Map m = new HashMap();
        j = "select i "
                + " from ItemUsage i "
                + " where i.retired=false "
                + " and i.forWebUser=:wu ";

        if (type != null) {
            m.put("t", type);
            j += " and i.type=:t ";
        }
        
        if(forItem!=null){
            m.put("fi", forItem);
            j += " and i.forItem=:fi ";
        }
        if (weight != null) {
            j += " and ( i.fromKg < :wt and i.toKg > :wt ) ";
            m.put("wt", weight);
        }
        if (ageInDays != null) {
            j += " and ( i.fromDays < :ad and i.toDays > :ad ) ";
            m.put("ad", (double)ageInDays);
        }
        j += " order by i.orderNo";

        m.put("wu", sessionController.getLoggedUser());
        System.out.println("m = " + m);
        System.out.println("j = " + j);
        List<ItemUsage> its = favouriteItemFacade.findBySQL(j, m);
        if(its==null){
            System.out.println("its is empty");
            its = new ArrayList<>();
        }
        System.out.println("its = " + its.size());
        return its;
    }

    public void prepareAddingFavouriteItem() {
        if (item == null) {
            JsfUtil.addErrorMessage("No Item Selected");
            return;
        }
        System.out.println("item = " + item);
        current = new ItemUsage();
        current.setForItem(item);
        current.setItem(item);
        current.setDoseUnit(item.getIssueUnit());
        current.setType(ItemUsageType.FavouriteMedicine);

        availableDoseUnits = new ArrayList<>();
        availableItems = new ArrayList<>();
        System.out.println("item.getMedicineType() = " + item.getMedicineType());
        switch (item.getMedicineType()) {
            case Vmp:
                availableDoseUnits.add(item.getBaseUnit());
                availableDoseUnits.add(item.getIssueUnit());
                availableItems.add(item);
                availableItems.addAll(vmpController.ampsOfVmp(item));
                break;
            case Amp:
                availableDoseUnits.add(item.getVmp().getBaseUnit());
                availableDoseUnits.add(item.getVmp().getIssueUnit());
                availableItems.add(item);
                break;
            case Vtm:
                availableDoseUnits = doseUnitController.getMeasurementUnits();
                availableItems.addAll(vmpController.ampsAndVmpsContainingVtm(item));
                availableItems.add(item);
                break;
            case Atm:
                availableDoseUnits = doseUnitController.getMeasurementUnits();
                availableItems.addAll(vmpController.ampsAndVmpsContainingVtm(item));
                availableItems.add(item);
                break;
            case Ampp:
                break;
            case Vmpp:
                break;
            case Medicine:
                System.out.println("subtype");
                JsfUtil.addErrorMessage("Selected needs a subtype");
                break;
            case AnalyzerTest:
            case Appointment:
            case Investigation:
            case Other:
            case SampleComponent:
            case Service:
                JsfUtil.addErrorMessage("Selected is NOT a medicine");
                break;
            default:
        }
    }

    public void prepareAddingFavouriteDiagnosis() {
        if (item == null) {
            JsfUtil.addErrorMessage("No Diagnosis Selected");
            return;
        }
        current = new ItemUsage();
        current.setForItem(item);
        current.setType(ItemUsageType.FavouriteDiagnosis);
    }

    public String toViewFavouriteMedicines() {
        listMyFavouriteMedicines();
        return "/clinical/clinical_favourite_item";
    }

    public void listMyFavouriteMedicines() {

    }

    public void removeFavourite() {
        if (current == null) {
            JsfUtil.addErrorMessage("Nothing current");
            return;
        }
        current.setRetired(true);
        favouriteItemFacade.edit(current);
        ItemUsageType tt = current.getType();
        current = null;
        fillFavouriteItems(item, tt);
        JsfUtil.addSuccessMessage("Removed");
    }

    public void addToFavouriteMedicine() {
        if (item == null) {
            JsfUtil.addErrorMessage("Please select an item");
            return;
        }
        if (current == null) {
            JsfUtil.addErrorMessage("Favourite Item is not create by getter. Please contact vendor.");
            return;
        }
        if (current.getFavouriteType() == null) {
            JsfUtil.addErrorMessage("Favourite Type NOT current.");
            return;
        }
        if (current.getFavouriteFrom() == null) {
            JsfUtil.addErrorMessage("From NOT current.");
            return;
        }
        if (current.getFavouriteTo() == null) {
            JsfUtil.addErrorMessage("To NOT current.");
            return;
        }
        if (current.getFavouriteFrom().equals(current.getFavouriteTo())) {
            JsfUtil.addErrorMessage("From is equal not To. So not added.");
            return;
        }
        switch (current.getFavouriteType()) {
            case kg:
                current.setFromDays(null);
                current.setToDays(null);
                current.setFromKg(current.getFavouriteFrom());
                current.setToKg(current.getFavouriteTo());
                break;
            case days:
                current.setFromDays(current.getFavouriteFrom());
                current.setToDays(current.getFavouriteTo());
                current.setFromKg(null);
                current.setToKg(null);
                break;
            case months:
                current.setFromDays(current.getFavouriteFrom() * 30.4167);
                current.setToDays(current.getFavouriteTo() * 30.4167);
                current.setFromKg(null);
                current.setToKg(null);
                break;
            case years:
                current.setFromDays(current.getFavouriteFrom() * 365);
                current.setToDays(current.getFavouriteTo() * 365);
                current.setFromKg(null);
                current.setToKg(null);
                break;
            default:
                JsfUtil.addErrorMessage("Favourite Type NOT current.");
                return;
        }

        current.setType(ItemUsageType.FavouriteMedicine);
        current.setForItem(item);
        current.setForWebUser(sessionController.getLoggedUser());
        current.setOrderNo(getItems().size() + 1.0);
        favouriteItemFacade.create(current);
        current = null;
        fillFavouriteItems(item, ItemUsageType.FavouriteMedicine);
        JsfUtil.addSuccessMessage("Saved");

    }

    public void addToFavouriteDiagnosis() {
        if (item == null) {
            JsfUtil.addErrorMessage("Please select a Diagnosis");
            return;
        }
        if (current == null) {
            JsfUtil.addErrorMessage("Favourite Item is not create by getter. Please contact vendor.");
            return;
        }
        if (current.getFavouriteType() == null) {
            JsfUtil.addErrorMessage("Favourite Type NOT current.");
            return;
        }
        if (current.getFavouriteFrom() == null) {
            JsfUtil.addErrorMessage("From NOT current.");
            return;
        }
        if (current.getFavouriteTo() == null) {
            JsfUtil.addErrorMessage("To NOT current.");
            return;
        }
        if (current.getFavouriteFrom().equals(current.getFavouriteTo())) {
            JsfUtil.addErrorMessage("From is equal not To. So not added.");
            return;
        }
        switch (current.getFavouriteType()) {
            case kg:
                current.setFromDays(null);
                current.setToDays(null);
                current.setFromKg(current.getFavouriteFrom());
                current.setToKg(current.getFavouriteTo());
                break;
            case days:
                current.setFromDays(current.getFavouriteFrom());
                current.setToDays(current.getFavouriteTo());
                current.setFromKg(null);
                current.setToKg(null);
                break;
            case months:
                current.setFromDays(current.getFavouriteFrom() * 30.4167);
                current.setToDays(current.getFavouriteTo() * 30.4167);
                current.setFromKg(null);
                current.setToKg(null);
                break;
            case years:
                current.setFromDays(current.getFavouriteFrom() * 365);
                current.setToDays(current.getFavouriteTo() * 365);
                current.setFromKg(null);
                current.setToKg(null);
                break;
            default:
                JsfUtil.addErrorMessage("Favourite Type NOT current.");
                return;
        }

        current.setType(ItemUsageType.FavouriteDiagnosis);
        current.setForItem(item);
        current.setForWebUser(sessionController.getLoggedUser());
        current.setOrderNo(getItems().size() + 1.0);
        favouriteItemFacade.create(current);
        current = null;
        fillFavouriteItems(item, ItemUsageType.FavouriteDiagnosis);
        JsfUtil.addSuccessMessage("Saved");

    }

    public void updateSelected() {
        updateSelected(current);
    }

    public void updateSelected(ItemUsage updatingItem) {
        if (updatingItem != null) {
            favouriteItemFacade.edit(updatingItem);
            JsfUtil.addSuccessMessage("Updated");
        }
    }

    /**
     * Creates a new instance of FavouriteController
     */
    public FavouriteController() {
    }

    /**
     * Getters And Setters
     */
    
    
    
    /**
     * @return 
     */
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<ItemUsage> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public void setItems(List<ItemUsage> items) {
        this.items = items;
    }

    public ItemUsage getCurrent() {
        if (current == null) {
            current = new ItemUsage();
            current.setItem(item);
        }
        return current;
    }

    public void setCurrent(ItemUsage current) {
        this.current = current;
    }

    public List<MeasurementUnit> getAvailableDoseUnits() {
        return availableDoseUnits;
    }

    public void setAvailableDoseUnits(List<MeasurementUnit> availableDoseUnits) {
        this.availableDoseUnits = availableDoseUnits;
    }

    public List<Item> getAvailableItems() {
        return availableItems;
    }

    public void setAvailableItems(List<Item> availableItems) {
        this.availableItems = availableItems;
    }

}
