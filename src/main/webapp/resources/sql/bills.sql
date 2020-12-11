select `ID`, `TODEPARTMENT_ID`,`REFERREDBY_ID`, `CREATEDAT`,`BILLTYPE`, `TOTAL`,`GRANTTOTAL`,`GRNNETTOTAL`,`NETTOTAL`,`BALANCE`,`PAIDAMOUNT`,`BALANCE`
 from bill 
where billtype='OpdBill'
and `TODEPARTMENT_ID` <> 0
and `REFERREDBY_ID` <> 0
order by `ID` desc limit 10;