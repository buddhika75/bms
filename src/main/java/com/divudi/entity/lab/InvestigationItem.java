/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.entity.lab;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Buddhika
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@XmlRootElement
public class InvestigationItem extends ReportItem implements Serializable {
   
    private static final long serialVersionUID = 1L;
    
    
    @OneToMany(mappedBy = "investigationItem", cascade= CascadeType.ALL, fetch= FetchType.LAZY)
    List<InvestigationItemValue> investigationItemValues;

    @XmlTransient
    public List<InvestigationItemValue> getInvestigationItemValues() {
        return investigationItemValues;
    }

    public void setInvestigationItemValues(List<InvestigationItemValue> investigationItemValues) {
        this.investigationItemValues = investigationItemValues;
    }
    
    
    
    
    
}