/*
 * Author : Dr. M H B Ariyaratne, MO(Health Information), email : buddhika.ari@gmail.com
 * and open the template in the editor.
 */
package com.divudi.bean.common;

import com.divudi.facade.WebUserFacade;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Buddhika
 */
@Named
@SessionScoped
public class ThemeController implements Serializable {

    @EJB
    WebUserFacade facade;
    /**
     * Managed Properties
     */
    @Inject
    SessionController sessionController;

    /**
     * Creates a new instance of ThemeSwitcherBean
     */
    public ThemeController() {
    }
    private Map<String, String> themes;
    private String theme;

    public Map<String, String> getThemes() {
        return themes;
    }

    // Newly modified by Dr M H B Ariyaratne with assistance from ChatGPT from OpenAI
public String getTheme() {
    if (theme == null) {
        return "primefaces-nova-colored"; // default theme
    }
    switch (theme) {
        case "primefaces-arya-blue":
        case "primefaces-bootstrap4-blue-dark":
        case "primefaces-bootstrap4-blue-light":
        case "primefaces-bootstrap4-dark-common":
        case "primefaces-bootstrap4-light-common":
        case "primefaces-bootstrap4-purple-dark":
        case "primefaces-bootstrap4-purple-light":
        case "primefaces-luna-amber":
        case "primefaces-luna-blue":
        case "primefaces-luna-common":
        case "primefaces-luna-green":
        case "primefaces-luna-pink":
        case "primefaces-material-compact-deeppurple-dark":
        case "primefaces-material-compact-deeppurple-light":
        case "primefaces-material-compact-indigo-dark":
        case "primefaces-material-compact-indigo-light":
        case "primefaces-material-dark-common":
        case "primefaces-material-deeppurple-dark":
        case "primefaces-material-deeppurple-light":
        case "primefaces-material-indigo-dark":
        case "primefaces-material-indigo-light":
        case "primefaces-material-light-common":
        case "primefaces-mytheme":
        case "primefaces-nova-colored":
        case "primefaces-nova-common":
        case "primefaces-nova-dark":
        case "primefaces-nova-light":
        case "primefaces-saga-blue":
        case "primefaces-vela-blue":
            return theme;
        default:
            return "primefaces-nova-colored"; // default theme if it's an old theme or unrecognized theme
    }
}


    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void saveTheme() {
        if (getSessionController().getLoggedUser() != null) {
            getSessionController().getLoggedUser().setPrimeTheme(theme);
            getFacade().edit(getSessionController().getLoggedUser());
            UtilityController.addSuccessMessage("Theme updated");
        } else {
            getSessionController().setPrimeTheme(theme);
            UtilityController.addErrorMessage("Theme NOT updated. Please login before you change the theme permamtely");
        }
    }

    public WebUserFacade getFacade() {
        return facade;
    }

    public void setFacade(WebUserFacade facade) {
        this.facade = facade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    // Newly modified by Dr M H B Ariyaratne with assistance from ChatGPT from OpenAI
    @PostConstruct
    public void init() {
        themes = new TreeMap<String, String>();

        themes.put("Primefaces Arya Blue", "primeaces-arya-blue");
        themes.put("Primefaces Bootstrap4 Blue Dark", "primeaces-bootstrap4-blue-dark");
        themes.put("Primefaces Bootstrap4 Blue Light", "primeaces-bootstrap4-blue-light");
        themes.put("Primefaces Bootstrap4 Dark Common", "primeaces-bootstrap4-dark-common");
        themes.put("Primefaces Bootstrap4 Light Common", "primeaces-bootstrap4-light-common");
        themes.put("Primefaces Bootstrap4 Purple Dark", "primeaces-bootstrap4-purple-dark");
        themes.put("Primefaces Bootstrap4 Purple Light", "primeaces-bootstrap4-purple-light");
        themes.put("Primefaces Luna Amber", "primeaces-luna-amber");
        themes.put("Primefaces Luna Blue", "primeaces-luna-blue");
        themes.put("Primefaces Luna Common", "primeaces-luna-common");
        themes.put("Primefaces Luna Green", "primeaces-luna-green");
        themes.put("Primefaces Luna Pink", "primeaces-luna-pink");
        themes.put("Primefaces Material Compact Deep Purple Dark", "primeaces-material-compact-deeppurple-dark");
        themes.put("Primefaces Material Compact Deep Purple Light", "primeaces-material-compact-deeppurple-light");
        themes.put("Primefaces Material Compact Indigo Dark", "primeaces-material-compact-indigo-dark");
        themes.put("Primefaces Material Compact Indigo Light", "primeaces-material-compact-indigo-light");
        themes.put("Primefaces Material Dark Common", "primeaces-material-dark-common");
        themes.put("Primefaces Material Deep Purple Dark", "primefaces-material-deeppurple-dark");
        themes.put("Primefaces Material Deep Purple Light", "primefaces-material-deeppurple-light");
        themes.put("Primefaces Material Indigo Dark", "primefaces-material-indigo-dark");
        themes.put("Primefaces Material Indigo Light", "primefaces-material-indigo-light");
        themes.put("Primefaces Material Light Common", "primefaces-material-light-common");
        themes.put("Primefaces MyTheme", "primefaces-mytheme");
        themes.put("Primefaces Nova Colored", "primefaces-nova-colored");
        themes.put("Primefaces Nova Common", "primefaces-nova-common");
        themes.put("Primefaces Nova Dark", "primefaces-nova-dark");
        themes.put("Primefaces Nova Light", "primefaces-nova-light");
        themes.put("Primefaces Saga Blue", "primefaces-saga-blue");
        themes.put("Primefaces Vela Blue", "primefaces-vela-blue");
    }

}
