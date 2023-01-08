/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.bean.clinical;

import com.divudi.bean.common.SessionController;
import com.divudi.data.clinical.ItemUsageType;
import com.divudi.entity.Item;
import com.divudi.entity.clinical.ItemUsage;
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
    /**
     * Properties
     */
    Item item;
    ItemUsage current;
    List<ItemUsage> items;

    /**
     * Methods
     */
    public void fillFavouriteMedicines() {
        fillFavouriteItems(ItemUsageType.FavoutireMedicine);
    }

    public void fillFavouriteDisgnosis() {
        fillFavouriteItems(ItemUsageType.FavouriteDiagnosis);
    }

    /**
     *
     * @param type
     */
    public void fillFavouriteItems(ItemUsageType type) {
        String j;
        Map m = new HashMap();
        j = "select i "
                + " from ItemUsage i "
                + " where i.retired=false "
                + " and i.forItem=:item "
                + " and i.forWebUser=:wu ";

        if (type != null) {
            m.put("t", type);
            j += " and i.type=:t ";
        }

        j += " order by i.orderNo";

        m.put("item", item);
        m.put("wu", sessionController.getLoggedUser());
        items = favouriteItemFacade.findBySQL(j, m);
    }

    public void prepareAddingFavouriteItem() {
        if (item == null) {
            JsfUtil.addErrorMessage("No Item Selected");
            return;
        }
        current = new ItemUsage();
        current.setForItem(item);
        switch (item.getMedicineType()) {
            case Amp:
                break;
            case Ampp:
                break;
            case Atm:

                break;
            case Vmp:
                break;
            case Vmpp:
                break;
            case Vtm:
                break;
            case Medicine:
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

    public String toViewFavouriteMedicines() {
        listMyFavouriteMedicines();
        return "/clinical/clinical_favourite_item";
    }

    public void listMyFavouriteMedicines() {

    }
    
    public void removeFavourite(){
        if(current==null){
            JsfUtil.addErrorMessage("Nothing current");
            return;
        }
        current.setRetired(true);
        favouriteItemFacade.edit(current);
        ItemUsageType tt = current.getType();
        current = null;
        fillFavouriteItems(tt);
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
                current.setFromDays(current.getFavouriteFrom()*30.4167);
                current.setToDays(current.getFavouriteTo()*30.4167);
                current.setFromKg(null);
                current.setToKg(null);
                break;
            case years:
                current.setFromDays(current.getFavouriteFrom()*365);
                current.setToDays(current.getFavouriteTo()*365);
                current.setFromKg(null);
                current.setToKg(null);
                break;
            default:
                JsfUtil.addErrorMessage("Favourite Type NOT current.");
                return;
        }

        current.setType(ItemUsageType.FavoutireMedicine);
        current.setForItem(item);
        current.setItem(item);
        current.setForWebUser(sessionController.getLoggedUser());
        current.setOrderNo(getItems().size()+1.0);
        favouriteItemFacade.create(current);
        current = null;
        fillFavouriteItems(ItemUsageType.FavoutireMedicine);
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
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<ItemUsage> getItems() {
        if(items==null){
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

}
