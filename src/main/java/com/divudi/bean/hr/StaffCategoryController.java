/*
 * MSc(Biomedical Informatics) Project
 *
 * Development and Implementation of a Web-based Combined Data Repository of
 Genealogical, Clinical, Laboratory and Genetic Data
 * and
 * a Set of Related Tools
 */
package com.divudi.bean.hr;

import com.divudi.bean.common.SessionController;
import com.divudi.bean.common.UtilityController;
import com.divudi.entity.hr.StaffCategory;
import com.divudi.facade.StaffCategoryFacade;
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
public class StaffCategoryController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @EJB
    private StaffCategoryFacade ejbFacade;
    List<StaffCategory> selectedItems;
    private StaffCategory current;
    private List<StaffCategory> items = null;
    String selectText = "";

    public List<StaffCategory> getSelectedItems() {
        selectedItems = getFacade().findBySQL("select c from StaffCategory c where c.retired=false and upper(c.name) like '%" + getSelectText().toUpperCase() + "%' order by c.name");
        return selectedItems;
    }

    public List<StaffCategory> completeStaffCategory(String qry) {
        List<StaffCategory> a = null;
        if (qry != null) {
            a = getFacade().findBySQL("select c from StaffCategory c where c.retired=false and upper(c.name) like '%" + qry.toUpperCase() + "%' order by c.name");
        }
        if (a == null) {
            a = new ArrayList<StaffCategory>();
        }
        return a;
    }

    public void prepareAdd() {
        current = new StaffCategory();
    }

    public void setSelectedItems(List<StaffCategory> selectedItems) {
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

    public StaffCategoryFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(StaffCategoryFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public StaffCategoryController() {
    }

    public StaffCategory getCurrent() {
        if (current == null) {
            current = new StaffCategory();
        }
        return current;
    }

    public void setCurrent(StaffCategory current) {
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

    private StaffCategoryFacade getFacade() {
        return ejbFacade;
    }

    public List<StaffCategory> getItems() {
        if (items == null) {
            String j;
            j="select c "
                    + " from StaffCategory c "
                    + " where c.retired=false "
                    + " order by c.name";
            items = getFacade().findBySQL(j);
        }
        return items;
    }

    /**
     *
     */
    @FacesConverter(forClass = StaffCategory.class)
    public static class StaffCategoryConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            StaffCategoryController controller = (StaffCategoryController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "staffCategoryController");
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
            if (object instanceof StaffCategory) {
                StaffCategory o = (StaffCategory) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + StaffCategoryController.class.getName());
            }
        }
    }

    @FacesConverter("staffCategoryCon")
    public static class StaffCategoryControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            StaffCategoryController controller = (StaffCategoryController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "staffCategoryController");
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
            if (object instanceof StaffCategory) {
                StaffCategory o = (StaffCategory) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + StaffCategoryController.class.getName());
            }
        }
    }
}