/*
 * MSc(Biomedical Informatics) Project
 *
 * Development and Implementation of a Web-based Combined Data Repository of
 Genealogical, Clinical, Laboratory and Genetic Data
 * and
 * a Set of Related Tools
 */
package com.divudi.bean.common;

import com.divudi.data.Privileges;
import com.divudi.data.dataStructure.PrivilageNode;
import com.divudi.entity.WebUser;
import com.divudi.entity.WebUserPrivilege;
import com.divudi.facade.WebUserPrivilegeFacade;
import com.divudi.facade.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TemporalType;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

//import org.primefaces.examples.domain.Document;  
//import org.primefaces.model;
/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 * Informatics)
 */
@Named
@SessionScoped
public class UserPrivilageController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @EJB
    private WebUserPrivilegeFacade ejbFacade;
    private List<WebUserPrivilege> selectedItems;
    private WebUserPrivilege current;
    private WebUser currentWebUser;
    private List<WebUserPrivilege> items = null;

    private List<Privileges> privilegeList;
    private Privileges currentPrivilege;

    

    public UserPrivilageController() {
        
    }

    public void loadPrivileges() {
        if (currentWebUser == null) {
            JsfUtil.addErrorMessage("Select User");
            return;
        }
        items = fetchExistingWebUserPrivileges(currentWebUser);
    }

    public List<Privileges> completePrivilegeses(String qry) {
        List<Privileges> filteredPrivileges = new ArrayList<>();

        // Iterate over all Privileges enum values
        for (Privileges privilege : Privileges.values()) {
            // Check if the enum value name contains the query string (case-insensitive)
            if (privilege.name().toLowerCase().contains(qry.toLowerCase())) {
                filteredPrivileges.add(privilege);
            }
        }

        return filteredPrivileges;
    }

    private List<Privileges> loadPrivileges(WebUser wu) {
        String jpql = "select wup "
                + " from WebUserPrivilege wup "
                + " where wup.retired=:ret "
                + " and wup.webUser=:wu";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("wu", wu);
        List<Privileges> ps = new ArrayList<>();
        List<WebUserPrivilege> wups = getFacade().findBySQL(jpql, m);
        if (wups == null) {
            return null;
        }
        for (WebUserPrivilege wup : wups) {
            if (wup.getPrivilege() != null) {
                ps.add(wup.getPrivilege());
            }
        }
        return ps;
    }

    private List<WebUserPrivilege> fetchExistingWebUserPrivileges(WebUser wu) {
        String jpql = "select wup "
                + " from WebUserPrivilege wup "
                + " where wup.retired=:ret "
                + " and wup.webUser=:wu";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("wu", wu);
        List<WebUserPrivilege> wups = getFacade().findBySQL(jpql, m);
        if (wups == null) {
            return null;
        }

        return wups;
    }

    public void allAllPrivileges() {
        if (sessionController.getLoggedUser() == null) {
            UtilityController.addErrorMessage("Please select a user");
            return;
        }
        for (Privileges p : Privileges.values()) {
            addSinglePrivilege(p, sessionController.getLoggedUser());
        }
        getItems();
    }
    
    public void addSinglePrivilege() {
        addSinglePrivilege(currentPrivilege);
        currentPrivilege = null;
    }

    public void addSinglePrivilege(Privileges p) {
        addSinglePrivilege(p, currentWebUser);
    }

    public void addSinglePrivilege(Privileges p, WebUser u) {
        if (p == null || u == null) {
            return;
        }
        WebUserPrivilege wup;
        Map m = new HashMap();
        m.put("wup", p);
        String sql = "SELECT i FROM WebUserPrivilege i where i.webUser.id= " + u.getId() + " and i.privilege=:wup ";
        wup = getEjbFacade().findFirstBySQL(sql, m, TemporalType.DATE);

        if (wup == null) {
            wup = new WebUserPrivilege();
            wup.setRetired(false);
            wup.setPrivilege(p);
            wup.setWebUser(u);
            getFacade().create(wup);
        } else {
            wup.setRetired(false);
            wup.setPrivilege(p);
            wup.setWebUser(u);
            getFacade().edit(wup);
        }
        items.add(wup);

    }

    public void remove() {
        if (getCurrent() == null) {
            UtilityController.addErrorMessage("Select Privilage");
            return;
        }
        items.remove(getCurrent());
        getCurrent().setRetired(true);
        getFacade().edit(getCurrent());
    }

   

    public WebUserPrivilegeFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(WebUserPrivilegeFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public WebUserPrivilege getCurrent() {
        if (current == null) {
            current = new WebUserPrivilege();

        }
        return current;
    }

    public void setCurrent(WebUserPrivilege current) {
        this.current = current;
    }

   

    private WebUserPrivilegeFacade getFacade() {
        return ejbFacade;
    }
    

    public List<WebUserPrivilege> getItems() {
        return items;
    }

    
    public WebUser getCurrentWebUser() {
        return currentWebUser;
    }

    public void setCurrentWebUser(WebUser currentWebUser) {
        this.currentWebUser = currentWebUser;
    }

    public List<Privileges> getPrivilegeList() {
        return privilegeList;
    }

    public void setPrivilegeList(List<Privileges> privilegeList) {
        this.privilegeList = privilegeList;

    }

    public List<WebUserPrivilege> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<WebUserPrivilege> selectedItems) {
        this.selectedItems = selectedItems;
    }

   

    private void removeWebUserPrivilege(WebUserPrivilege webUserPrivilege) {
        if (webUserPrivilege == null) {
            return;
        }
        webUserPrivilege.setRetired(true);
        getFacade().edit(webUserPrivilege);
    }

    private void saveWebUserPrivilege(WebUserPrivilege webUserPrivilege) {
        if (webUserPrivilege == null) {
            return;
        }
        webUserPrivilege.setRetired(false);
        if (webUserPrivilege.getId() == null) {
            getFacade().create(webUserPrivilege);
        } else {
            getFacade().edit(webUserPrivilege);
        }
    }

    public Privileges getCurrentPrivilege() {
        return currentPrivilege;
    }

    public void setCurrentPrivilege(Privileges currentPrivilege) {
        this.currentPrivilege = currentPrivilege;
    }

    /**
     *
     */
    @FacesConverter(forClass = WebUserPrivilege.class)
    public static class WebUserPrivilegeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserPrivilageController controller = (UserPrivilageController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userPrivilegeController");
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
            if (object instanceof WebUserPrivilege) {
                WebUserPrivilege o = (WebUserPrivilege) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + UserPrivilageController.class.getName());
            }
        }
    }
}
