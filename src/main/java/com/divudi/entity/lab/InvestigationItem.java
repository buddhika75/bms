/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.entity.lab;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Buddhika
 */
@Entity
public class InvestigationItem extends ReportItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "investigationItem", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<InvestigationItemValue> investigationItemValues;

    @OneToMany(mappedBy = "investigationItemOfLabelType", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<InvestigationItemValueFlag> investigationItemValueFlags;

    public List<InvestigationItemValue> getInvestigationItemValues() {
        return investigationItemValues;
    }

    public void setInvestigationItemValues(List<InvestigationItemValue> investigationItemValues) {
        this.investigationItemValues = investigationItemValues;
    }

//    public List<InvestigationItemValueFlag> getInvestigationItemValueFlags() {
//        System.out.println("22 getInvestigationItemValueFlags");
//        System.out.println("this name= " + this.name);
//        System.out.println("this id= " + this.id);
//        System.out.println("this iivs = " + this.investigationItemValues);
//        investigationItemValueFlags = new ArrayList<>();
//        System.out.println("getInvestigationItemValues() = " + getInvestigationItemValues());
//        if (getInvestigationItemValues() == null) {
//            return investigationItemValueFlags;
//        }
//        for(InvestigationItemValue f: getInvestigationItemValues()){
//            System.out.println("1 f = " + f);
//            System.out.println("f class = " + f.getClass());
//            if(f instanceof InvestigationItemValueFlag){
//                investigationItemValueFlags.add((InvestigationItemValueFlag) f);
//                
//            }
//        }
//        return investigationItemValueFlags;
//    }

    public List<InvestigationItemValueFlag> getInvestigationItemValueFlags() {
        System.out.println("getter investigationItemValueFlags = " + investigationItemValueFlags);
        if(investigationItemValueFlags!=null){
            for(InvestigationItemValueFlag f:investigationItemValueFlags){
                System.out.println("f = " + f);
            }
        }
        return investigationItemValueFlags;
    }

    public void setInvestigationItemValueFlags(List<InvestigationItemValueFlag> investigationItemValueFlags) {
        this.investigationItemValueFlags = investigationItemValueFlags;
    }
    
    

}
