LOCK TABLES gpconnect.patients WRITE;
INSERT INTO gpconnect.patients
  (id,    title,   first_name,   last_name,    address_1,address_2,address_3,postcode,phone,date_of_birth, gender,nhs_number,pas_number,department_id, gp_id, lastUpdated,sensitive_flag)
VALUE
   (1,'MRS','Minnie','DAWES','24 GRAMMAR SCHOOL ROAD','BRIGG','','DN20 8AF','01454587554','1952-05-31','Female',9476719931,352541,1,3,'2016-07-25 12:00:00','S'),
   (2,'MRS','Dolly','BANTON','11 QUEENSWAY','SCUNTHORPE','S HUMBERSIDE','DN16 2BZ','0121454552','1955-09-18','Female',9476719958,623454,1,2,'2016-07-25 12:00:00','S'),
   (3,'MISS','Ruby','MACKIN','3 WILDERSPIN HEIGHTS','BARTON-UPON-HUMBER','S HUMBERSIDE','DN18 5SN','013256541523','1953-01-01','Female',9476719966,346574,2,1,'2016-07-25 12:00:00','S'),
   (4,'MR','Julian','PHELAN','FARM HOUSE','BONNYHALE ROAD','S HUMBERSIDE','DN17 4JQ','02123636563','1992-07-02','Male',9476719974,332546,1,3,'2016-07-25 12:00:00','S');

UNLOCK TABLES;
