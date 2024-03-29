/*
 * MSc(Biomedical Informatics) Project
 *
 * Development and Implementation of a Web-based Combined Data Repository of
 Genealogical, Clinical, Laboratory and Genetic Data
 * and
 * a Set of Related Tools
 */
package com.divudi.bean.pharmacy;

import com.divudi.bean.common.SessionController;
import com.divudi.bean.common.UtilityController;
import com.divudi.entity.pharmacy.DoseUnit;
import com.divudi.entity.pharmacy.MeasurementUnit;
import com.divudi.facade.DoseUnitFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 * Informatics)
 */
@Named
@SessionScoped
public class DoseUnitController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @EJB
    private DoseUnitFacade ejbFacade;
    List<DoseUnit> selectedItems;
    private DoseUnit current;
    private List<DoseUnit> items = null;
    private List<MeasurementUnit> measurementUnits = null;
    String selectText = "";

    public void prepareAdd() {
        current = new DoseUnit();
    }

    public void setSelectedItems(List<DoseUnit> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String getSelectText() {
        return selectText;
    }

    private void recreateModel() {
        items = null;
    }

    public void saveSelected() {

        if (getCurrent().getId() != null && getCurrent().getId() > 0) {
            getFacade().edit(current);
            UtilityController.addSuccessMessage("Updated Successfully.");
        } else {
            current.setCreatedAt(new Date());
            current.setCreater(getSessionController().getLoggedUser());
            getFacade().create(current);
            UtilityController.addSuccessMessage("Saved Successfully");
        }
        recreateModel();
        getItems();
    }

    public void setSelectText(String selectText) {
        this.selectText = selectText;
    }

    public DoseUnitFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(DoseUnitFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public DoseUnitController() {
    }

    public DoseUnit getCurrent() {
        if (current == null) {
            current = new DoseUnit();
        }
        return current;
    }

    public void setCurrent(DoseUnit current) {
        this.current = current;
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

    private DoseUnitFacade getFacade() {
        return ejbFacade;
    }

    public List<DoseUnit> getItems() {
        if (items == null) {
            items = getFacade().findAll("name", true);
        }
        return items;
    }

    public List<MeasurementUnit> getMeasurementUnits() {
        if(measurementUnits==null){
            measurementUnits = new ArrayList<>();
            for(DoseUnit du: getItems()){
                System.out.println("du = " + du);
                measurementUnits.add(du);
            }
        }
        return measurementUnits;
    }
    
    


    /**
     *
     */
    @FacesConverter(forClass = DoseUnit.class)
    public static class DoseUnitControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DoseUnitController controller = (DoseUnitController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "doseUnitController");
            return controller.getEjbFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof DoseUnit) {
                DoseUnit o = (DoseUnit) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + DoseUnitController.class.getName());
            }
        }
    }
}
