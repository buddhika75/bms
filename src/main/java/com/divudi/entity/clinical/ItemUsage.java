/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.entity.clinical;

import com.divudi.data.Sex;
import com.divudi.data.clinical.FavouriteType;
import com.divudi.data.clinical.ItemUsageType;
import com.divudi.entity.Category;
import com.divudi.entity.Department;
import com.divudi.entity.Institution;
import com.divudi.entity.Item;
import com.divudi.entity.Patient;
import com.divudi.entity.PatientEncounter;
import com.divudi.entity.WebUser;
import com.divudi.entity.pharmacy.DoseUnit;
import com.divudi.entity.pharmacy.DurationUnit;
import com.divudi.entity.pharmacy.FrequencyUnit;
import com.divudi.entity.pharmacy.MeasurementUnit;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 *
 * @author buddhika
 */
@Entity
public class ItemUsage implements Serializable {

    @ManyToOne
    private ItemUsage parent;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Institution forInstitution;
    @ManyToOne
    private Department forDepartment;
    @ManyToOne
    private WebUser forWebUser;
    @ManyToOne
    private Item forItem;

    @ManyToOne
    Patient patient;
    @ManyToOne
    PatientEncounter patientEncounter;

    @Enumerated(EnumType.STRING)
    private ItemUsageType type;
    

    
    
    @ManyToOne
    private Item item;
    @ManyToOne
    private Category category;
    
    private Double favouriteFrom;
    private Double favouriteTo;
    @Enumerated
    private FavouriteType favouriteType;
    
    private Double fromDays;
    private Double toDays;
    private Double fromKg;
    private Double toKg;
    
    
    Integer intValue1;
    @ManyToOne
    private DoseUnit doseUnit;
    private Double dose;
    Integer intValue2;
    @ManyToOne
    private FrequencyUnit frequencyUnit;
    @Enumerated(EnumType.STRING)
    private Sex sex;
    private Long ageInMonthsFrom;
    private Long ageInMonthsTo;
    private Double orderNo;
    @ManyToOne
    private DurationUnit durationUnit;
    private Double duration;

    @ManyToOne
    private MeasurementUnit issueUnit;
    private Double issue;
    
    
    
    
    
    @OneToMany(mappedBy = "parent")
    private List<ItemUsage> children;

    //Created Properties
    @ManyToOne
    private WebUser creater;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createdAt;
    //Retairing properties
    private boolean retired;
    @ManyToOne
    private WebUser retirer;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date retiredAt;
    private String retireComments;
    //Editer Properties
    @ManyToOne
    private WebUser editer;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date editedAt;
    
    

    @Transient
    boolean needDosageForm;

    @Transient
    private boolean doseUnitFixed;

    public boolean isNeedDosageForm() {
        needDosageForm = false;
        if (this.getItem() == null) {
            return needDosageForm;
        }
        if (this.getItem().getMedicineType() == null) {
            return needDosageForm;
        }
        switch (this.getItem().getMedicineType()) {
            case Vtm:
            case Atm:
                needDosageForm = true;
                break;
            default:
                needDosageForm = false;
        }
        return needDosageForm;
    }

    public boolean isDoseUnitFixed() {
        doseUnitFixed = false;
        if (this.getItem() == null) {
            return doseUnitFixed;
        }
        if (this.getItem().getMedicineType() == null) {
            return doseUnitFixed;
        }
        switch (this.getItem().getMedicineType()) {
            case Vtm:
            case Atm:
                doseUnitFixed = false;
                break;
            default:
                doseUnitFixed = true;
        }
        return doseUnitFixed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public PatientEncounter getPatientEncounter() {
        return patientEncounter;
    }

    public void setPatientEncounter(PatientEncounter patientEncounter) {
        this.patientEncounter = patientEncounter;
    }

    public Integer getIntValue1() {
        return intValue1;
    }

    public void setIntValue1(Integer intValue1) {
        this.intValue1 = intValue1;
    }

    public Integer getIntValue2() {
        return intValue2;
    }

    public void setIntValue2(Integer intValue2) {
        this.intValue2 = intValue2;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItemUsage)) {
            return false;
        }
        ItemUsage other = (ItemUsage) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.divudi.entity.clinical.FavouriteItem[ id=" + id + " ]";
    }

    public Institution getForInstitution() {
        return forInstitution;
    }

    public void setForInstitution(Institution forInstitution) {
        this.forInstitution = forInstitution;
    }

    public Department getForDepartment() {
        return forDepartment;
    }

    public void setForDepartment(Department forDepartment) {
        this.forDepartment = forDepartment;
    }

    public WebUser getForWebUser() {
        return forWebUser;
    }

    public void setForWebUser(WebUser forWebUser) {
        this.forWebUser = forWebUser;
    }

    public ItemUsageType getType() {
        return type;
    }

    public void setType(ItemUsageType type) {
        this.type = type;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Double getFavouriteFrom() {
        return favouriteFrom;
    }

    public void setFavouriteFrom(Double favouriteFrom) {
        this.favouriteFrom = favouriteFrom;
    }

    public DoseUnit getDoseUnit() {
        return doseUnit;
    }

    public void setDoseUnit(DoseUnit dosageUnit) {
        this.doseUnit = dosageUnit;
    }

    public Double getDose() {
        return dose;
    }

    public void setDose(Double dose) {
        this.dose = dose;
    }

    public FrequencyUnit getFrequencyUnit() {
        return frequencyUnit;
    }

    public void setFrequencyUnit(FrequencyUnit frequencyUnit) {
        this.frequencyUnit = frequencyUnit;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Long getAgeInMonthsFrom() {
        return ageInMonthsFrom;
    }

    public void setAgeInMonthsFrom(Long ageInMonthsFrom) {
        this.ageInMonthsFrom = ageInMonthsFrom;
    }

    public Long getAgeInMonthsTo() {
        return ageInMonthsTo;
    }

    public void setAgeInMonthsTo(Long ageInMonthsTo) {
        this.ageInMonthsTo = ageInMonthsTo;
    }

    public Double getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Double orderNo) {
        this.orderNo = orderNo;
    }

    public List<ItemUsage> getChildren() {
        return children;
    }

    public void setChildren(List<ItemUsage> children) {
        this.children = children;
    }

    public WebUser getCreater() {
        return creater;
    }

    public void setCreater(WebUser creater) {
        this.creater = creater;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRetired() {
        return retired;
    }

    public void setRetired(boolean retired) {
        this.retired = retired;
    }

    public WebUser getRetirer() {
        return retirer;
    }

    public void setRetirer(WebUser retirer) {
        this.retirer = retirer;
    }

    public Date getRetiredAt() {
        return retiredAt;
    }

    public void setRetiredAt(Date retiredAt) {
        this.retiredAt = retiredAt;
    }

    public String getRetireComments() {
        return retireComments;
    }

    public void setRetireComments(String retireComments) {
        this.retireComments = retireComments;
    }

    public WebUser getEditer() {
        return editer;
    }

    public void setEditer(WebUser editer) {
        this.editer = editer;
    }

    public Date getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(Date editedAt) {
        this.editedAt = editedAt;
    }

    public ItemUsage getParent() {
        return parent;
    }

    public void setParent(ItemUsage parent) {
        this.parent = parent;
    }

    public Item getForItem() {
        return forItem;
    }

    public void setForItem(Item forItem) {
        this.forItem = forItem;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public DurationUnit getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(DurationUnit durationUnit) {
        this.durationUnit = durationUnit;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public MeasurementUnit getIssueUnit() {
        return issueUnit;
    }

    public void setIssueUnit(MeasurementUnit issueUnit) {
        this.issueUnit = issueUnit;
    }

    public Double getIssue() {
        return issue;
    }

    public void setIssue(Double issue) {
        this.issue = issue;
    }

    public FavouriteType getFavouriteType() {
        return favouriteType;
    }

    public void setFavouriteType(FavouriteType favouriteType) {
        this.favouriteType = favouriteType;
    }

    public Double getFromDays() {
        return fromDays;
    }

    public void setFromDays(Double fromDays) {
        this.fromDays = fromDays;
    }

    public Double getToDays() {
        return toDays;
    }

    public void setToDays(Double toDays) {
        this.toDays = toDays;
    }

    public Double getFromKg() {
        return fromKg;
    }

    public void setFromKg(Double fromKg) {
        this.fromKg = fromKg;
    }

    public Double getToKg() {
        return toKg;
    }

    public void setToKg(Double toKg) {
        this.toKg = toKg;
    }

    public Double getFavouriteTo() {
        return favouriteTo;
    }

    public void setFavouriteTo(Double favouriteTo) {
        this.favouriteTo = favouriteTo;
    }

}
