/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.data;

/**
 *
 * @author www.divudi.com
 */
public enum Privileges {
    //Main Menu Privileges
    TheaterIssueBHT,
    Opd,
    Inward,
    Lab,
    Pharmacy,
    Payment,
    Hr,
    Reports,
    User,
    Admin,
    Channelling,
    Clinical,
    Store,
    Search,
    CashTransaction,
    //Submenu Privileges
    OpdBilling,
    OpdCollectingCentreBilling,
    OpdPreBilling,
    OpdBillSearch,
    OpdBillItemSearch,
    OpdReprint,
    OpdCancel,
    OpdReturn,
    OpdReactivate,
    OpdBillSearchEdit,
    InwardAdmissions,
    InwardAdmissionsAdmission,
    InwardAdmissionsEditAdmission,
    InwardAdmissionsInwardAppoinment,
    InwardRoom,
    InwardRoomRoomOccupency,
    InwardRoomRoomChange,
    InwardRoomGurdianRoomChange,
    InwardRoomDischarge,
    InwardServicesAndItems,
    InwardServicesAndItemsAddServices,
    InwardServicesAndItemsAddOutSideCharges,
    InwardServicesAndItemsAddProfessionalFee,
    InwardServicesAndItemsAddTimedServices,
    InwardBilling,
    InwardBillingInterimBill,
    InwardBillingInterimBillSearch,
    InwardSearch,
    InwardSearchServiceBill,
    InwardSearchProfessionalBill,
    InwardSearchFinalBill,
    InwardReport,
    InwardFinalBillReportEdit,
    InwardAdministration,
    InwardAdditionalPrivilages,
    InwardBillSearch,
    InwardBillItemSearch,
    InwardBillReprint,
    InwardCancel,
    InwardReturn,
    InwardReactivate,
    InwardCheck,
    InwardUnCheck,
    ShowInwardFee,
    
    LabBilling,
    LabBillCancelSpecial,
    LabBillRefundSpecial,
    LabCasheirBillSearch,
    LabCashier,
    LabBillSearchCashier,
    LabBillSearch,
    LabBillItemSearch,
    LabBillCancelling,
    CollectingCentreCancelling,
    LabBillReturning,
    LabBillReprint,
    LabBillRefunding,
    LabBillReactivating,
    LabSampleCollecting,
    LabSampleReceiving,
    LabReportFormatEditing,
    LabDataentry,
    LabAutherizing,
    LabDeAutherizing,
    LabRevertSample,
    LabPrinting,
    LabReprinting,
    LabSummeriesLevel1,
    LabSummeriesLevel2,
    LabSummeriesLevel3,
    LabReportSearchOwn,
    LabReportSearchAll,
    LabReceive,
    LabEditPatient,
    LabInvestigationFee,
    LabAddInwardServices,
    LabSearchBillLoggedInstitution,
    IncomeReport,
    LabReport,
    DuesAndAccess,
    CheckEnteredData,
    LabAdiministrator,
    LabPatientDetailsEdit,
    
    PaymentBilling,
    PaymentBillSearch,
    PaymentBillReprint,
    PaymentBillCancel,
    PaymentBillRefund,
    PaymentBillReactivation,
    ReportsSearchCashCardOwn,
    ReportsSearchCreditOwn,
    ReportsItemOwn,
    ReportsSearchCashCardOther,
    ReportSearchCreditOther,
    ReportsItemOther,
    PharmacyOrderCreation,
    PharmacyOrderApproval,
    PharmacyOrderCancellation,
    PharmacySale,
    PharmacySaleReprint,
    PharmacySaleCancel,
    PharmacySaleReturn,
    //Wholesale
    PharmacySaleWh,
    PharmacySaleReprintWh,
    PharmacySaleCancelWh,
    PharmacySaleReturnWh,
    //end wholesale
    PharmacyInwardBilling,
    PharmacyInwardBillingCancel,
    PharmacyInwardBillingReturn,
    PharmacyGoodReceive,
    //Wholesale
    PharmacyGoodReceiveWh,
    //end Wholesale
    PharmacyGoodReceiveCancel,
    PharmacyGoodReceiveReturn,
    PharmacyGoodReceiveEdit,
    PharmacyPurchase,
    //Wholesale
    PharmacyPurchaseWh,
    //Whalesale
    PharmacyPurchaseReprint,
    PharmacyPurchaseCancellation,
    PharmacyPurchaseReturn,
    PharmacyStockAdjustment,
    PharmacyReAddToStock,
    PharmacyStockIssue,
    PharmacyDealorPayment,
    PharmacySearch,
    PharmacyReports,
    PharmacyTransfer,
    PharmacySummery,
    PharmacyAdministration,
    PharmacySetReorderLevel,
    PharmacyReturnWithoutTraising,
    //theater
    Theatre,
    TheatreAddSurgery,
    TheatreBilling,
    TheaterTransfer,
    TheaterTransferRequest,
    TheaterTransferIssue,
    TheaterTransferRecieve,
    TheaterTransferReport,
    TheaterReports,
    TheaterSummeries,
    TheaterIssue,
    TheaterIssuePharmacy,
    TheaterIssueStore,
    TheaterIssueStoreBhtBilling,
    TheaterIssueStoreBhtSearchBill,
    TheaterIssueStoreBhtSearchBillItem,
    TheaterIssueOpd,
    TheaterIssueOpdForCasheir,
    TheaterIssueOpdSearchPreBill,
    TheaterIssueOpdSearchPreBillForReturnItemOnly,
    TheaterIssueOpdSearchPreBillReturn,
    TheaterIssueOpdSearchPreBillAddToStock,
    
    
    ClinicalPatientSummery,
    ClinicalPatientDetails,
    ClinicalPatientPhoto,
    ClinicalVisitDetail,
    ClinicalVisitSummery,
    ClinicalHistory,
    ClinicalAdministration,
    ClinicalAdministrationEditLetter,
    ClinicalPatientDelete,
    ClinicalPatientAdd,
    ClinicalPatientEdit,
    ClinicalPatientCommentsView,
    ClinicalPatientCommentsEdit,
    ClinicalPatientNameChange,
    ClinicalMembershipAdd,
    ClinicalMembershipEdit,
    
    ChannelAdd,
    ChannelCancel,
    ChannelRefund,
    ChannelReturn,
    ChannelView,
    ChannelDoctorPayments,
    ChannelDoctorPaymentCancel,
    ChannelViewHistory,
    ChannelCreateSessions,
    ChannelManageSessions,
    ChannelAdministration,
    ChannelAgencyReports,
    AdminManagingUsers,
    AdminInstitutions,
    AdminStaff,
    AdminItems,
    AdminPrices,
    AdminFilterWithoutDepartment,
    ChangeProfessionalFee,
    ChangeCollectingCentre,
    StoreIssue,
    StoreIssueInwardBilling,
    StoreIssueSearchBill,
    StoreIssueBillItems,
    StorePurchase,
    StorePurchaseOrder,
    StorePurchaseOrderApprove,
    StorePurchaseOrderApproveSearch,
    StorePurchaseGRNRecive,
    StorePurchaseGRNReturn,
    StorePurchasePurchase,
    StoreTransfer,
    StoreTransferRequest,
    StoreTransferIssue,
    StoreTransferRecive,
    StoreTransferReport,
    StoreAdjustment,
    StoreAdjustmentDepartmentStock,
    StoreAdjustmentStaffStock,
    StoreAdjustmentPurchaseRate,
    StoreAdjustmentSaleRate,
    StoreDealorPayment,
    StoreDealorPaymentDueSearch,
    StoreDealorPaymentDueByAge,
    StoreDealorPaymentPayment,
    StoreDealorPaymentPaymentGRN,
    StoreDealorPaymentPaymentGRNSelect,
    StoreDealorPaymentGRNDoneSearch,
    StoreSearch,
    StoreReports,
    StoreSummery,
    StoreAdministration,
    SearchGrand,
    CashTransactionCashIn,
    CashTransactionCashOut,
    CashTransactionListToCashRecieve,
    ChannellingChannelBooking,
    ChannellingFutureChannelBooking,
    ChannellingPastBooking,
    ChannellingBookedList,
    ChannellingDoctorLeave,
    ChannellingDoctorLeaveByDate,
    ChannellingDoctorLeaveByServiceSession,
    ChannellingChannelSheduling,
    ChannellingChannelAgentFee,
    ChannellingDoctorSessionView,
    ChannellingPayment,
    ChannellingPaymentPayDoctor,
    ChannellingPaymentDueSearch,
    ChannellingPaymentDoneSearch,
    ChannellingApoinmentNumberCountEdit,
    MemberShip,
    MembershipSchemes,
    MemberShipInwardMemberShip,
    MemberShipInwardMemberShipSchemesDicounts,
    MemberShipInwardMemberShipInwardMemberShipReport,
    MemberShipOpdMemberShipDis,
    MemberShipOpdMemberShipDisByDepartment,
    MemberShipOpdMemberShipDisByCategory,
    MemberShipOpdMemberShipDisOpdMemberShipReport,
    
    HrAdmin,
    EmployeeHistoryReport,
    hrDeleteLateLeave,
    HrGenerateSalary,
    
    
    Developers,

    //Cashier
    AllCashierSummery,
    
    //Administration
    SearchAll,
    ChannelBookingChange,
    

    OpdCollectingCentreBillingMenu,

    OpdCollectingCentreBillSearch,


    InwardFinalBillCancel,
    InwardOutSideMarkAsUnPaid,

    InwardPharmacyMenu,
    InwardPharmacyIssueRequest,
    InwardPharmacyIssueRequestSearch,
    InwardBillSettleWithoutCheck,

    LabReportEdit,
    LabSummeries,

    LabReportSearchByLoggedInstitution,

    LabReports,
    LabItems,
    LabItemFeeUpadate,
    LabItemFeeDelete,
    LabLists,
    LabSetUp,
    LabInwardBilling,
    LabInwardSearchServiceBill,
    LabCollectingCentreBilling,
    LabCCBilling,
    LabCCBillingSearch,
    LabReportSearch,
    LabReporting,
    //dont remove



    PharmacyStockAdjustmentSingleItem,

    PharmacyBHTIssueAccept,
    //theater


    ChannellingChannelShedulRemove,
    ChannellingChannelShedulName,
    ChannellingChannelShedulStartingNo,
    ChannellingChannelShedulRoomNo,
    ChannellingChannelShedulMaxRowNo,

    ChannellingEditSerialNo,
    ChannellingEditPatientDetails,
    ChannellingPrintInPastBooking,
    ChannellingEditCreditLimitUserLevel,
    ChannellingEditCreditLimitAdminLevel,
    ChannelReports,
    ChannelSummery,
    ChannelManagement,
    ChannelAgencyAgencies,
    ChannelAgencyCreditLimitUpdate,
    ChannelAgencyCreditLimitUpdateBulk,
    ChannelAddChannelBookToAgency,
    ChannelManageSpecialities,
    ChannelManageConsultants,
    ChannelEditingAppoinmentCount,
    ChannelAddChannelingConsultantToInstitutions,
    ChannelFeeUpdate,
    ChannelCrdeitNote,
    ChannelCrdeitNoteSearch,
    ChannelDebitNote,
    ChannelDebitNoteSearch,
    ChannelCashCancelRestriction,

    ChannelBookingBokking,
    ChannelBookingReprint,
    ChannelBookingCancel,
    ChannelBookingRefund,
    ChannelBookingSettle,
    ChannelBookingSearch,
    ChannelBookingViews,
    ChannelBookingDocPay,
    ChannelBookingRestric,
    ChannelCashierTransaction,
    ChannelCashierTransactionIncome,
    ChannelCashierTransactionIncomeSearch,
    ChannelCashierTransactionExpencess,
    ChannelCashierTransactionExpencessSearch,
    ChannelActiveVat,
    

    MemberShipMemberDeActive,
    MemberShipMemberReActive,
    

    HrReports,
    HrReportsLevel1,
    HrReportsLevel2,
    HrReportsLevel3,
  
    HrGenerateSalarySpecial,
    HrAdvanceSalary,
    HrPrintSalary,
    HrWorkingTime,
    HrRosterTable,
    HrUploadAttendance,
    HrAnalyseAttendenceByRoster,
    HrAnalyseAttendenceByStaff,
    HrForms,
    HrLeaveForms,
    HrAdditionalForms,
    HrEditRetiedDate,
    HrRemoveResignDate,
    
    
    ChangePreferece,
    SendBulkSMS,

    
    
    
    
    
    
}
