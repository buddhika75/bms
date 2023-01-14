set foreign_key_checks=0;
ALTER TABLE PAYMENT DROP FOREIGN KEY FK_PAYMENT_BILL_ID;
set foreign_key_checks=1;