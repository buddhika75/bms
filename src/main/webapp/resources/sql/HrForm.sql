select `ID`,`SHIFTDATE`,`STAFF_ID`,`RETIRED`,`DAYTYPE`,`DTYPE`,`OVERTIMEFROMSTARTRECORDVARIFIED`,`OVERTIMEFROMENDRECORDVARIFIED`,
`OVERTIMECOMPLETERECORDVARIFIED`,`OVERTIMECOMPLETERECORDLOGGED`,`MULTIPLYINGFACTOR`,`BASICPERSECOND`,`ADDITIONALFORM_ID`
,(`OVERTIMEFROMENDRECORDVARIFIED`*`BASICPERSECOND`*`MULTIPLYINGFACTOR`),(`OVERTIMEFROMSTARTRECORDVARIFIED`*`BASICPERSECOND`*`MULTIPLYINGFACTOR`),`SHIFTDATE` from 
staffshift where `DAYTYPE`='Normal' and `STAFF_ID`=61464 ;
