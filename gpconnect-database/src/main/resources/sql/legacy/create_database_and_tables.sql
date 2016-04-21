/* Create a new poc_legacy database and begin configuration */
DROP DATABASE IF EXISTS gpconnect;
CREATE DATABASE         gpconnect DEFAULT CHARACTER SET utf8;
USE                     gpconnect;

/* Destroy all existing data */
DROP TABLE IF EXISTS gpconnect.general_practitioners;
DROP TABLE IF EXISTS gpconnect.medical_departments;
DROP TABLE IF EXISTS gpconnect.patients;
DROP TABLE IF EXISTS gpconnect.allergies;

DROP TABLE IF EXISTS gpconnect.transfers_of_care;
DROP TABLE IF EXISTS gpconnect.allergy_headlines;
DROP TABLE IF EXISTS gpconnect.contact_headlines;
DROP TABLE IF EXISTS gpconnect.medication_headlines;
DROP TABLE IF EXISTS gpconnect.problem_headlines;

/* Create new table schemas */
CREATE TABLE gpconnect.general_practitioners (
  id            BIGINT        NOT NULL    AUTO_INCREMENT,
  gp_name       VARCHAR(150)  NULL,
  address_1     VARCHAR(100)  NULL,
  address_2     VARCHAR(100)  NULL,
  address_3     VARCHAR(100)  NULL,
  address_4     VARCHAR(100)  NULL,
  address_5     VARCHAR(100)  NULL,
  postcode      VARCHAR(10)   NULL,
  PRIMARY KEY   (id)
);

CREATE TABLE gpconnect.medical_departments (
  id            BIGINT        NOT NULL    AUTO_INCREMENT,
  department    VARCHAR(150)  NULL,
  PRIMARY KEY   (id)
);

CREATE TABLE gpconnect.patients (
  id              BIGINT          NOT NULL    AUTO_INCREMENT,
  title           VARCHAR(10)     NULL,
  first_name      VARCHAR(30)     NULL,
  last_name       VARCHAR(30)     NULL,
  address_1       VARCHAR(100)    NULL,
  address_2       VARCHAR(100)    NULL,
  address_3       VARCHAR(100)    NULL,
  address_4       VARCHAR(100)    NULL,
  address_5       VARCHAR(100)    NULL,
  postcode        VARCHAR(10)     NULL,
  phone           VARCHAR(20)     NULL,
  date_of_birth   DATE            NULL,
  gender          VARCHAR(10)     NULL,
  nhs_number      VARCHAR(20)     NULL,
  pas_number      VARCHAR(20)     NULL,
  department_id   BIGINT          NOT NULL,
  gp_id           BIGINT          NOT NULL,
  PRIMARY KEY     (id),
  FOREIGN KEY     (department_id) REFERENCES  gpconnect.medical_departments(id),
  FOREIGN KEY     (gp_id)         REFERENCES  gpconnect.general_practitioners(id)
);

CREATE TABLE gpconnect.allergies (
  id                  BIGINT        NOT NULL    AUTO_INCREMENT,
  html                VARCHAR(2048) NULL,
  provider            VARCHAR(10)   NULL,
  PRIMARY KEY         (id)
);

CREATE TABLE gpconnect.transfers_of_care (
  id                  BIGINT        NOT NULL    AUTO_INCREMENT,
  patient_id          BIGINT        NOT NULL,
  reason_for_contact  VARCHAR(256)  NULL,
  clinical_summary    VARCHAR(256)  NULL,
  site_from           VARCHAR(256)  NULL,
  site_to             VARCHAR(256)  NULL,
  date_of_transfer    DATE          NULL,
  source              VARCHAR(30)   NOT NULL,
  PRIMARY KEY         (id),
  FOREIGN KEY         (patient_id)  REFERENCES  gpconnect.patients(id)
);

CREATE TABLE gpconnect.allergy_headlines (
  id            BIGINT        NOT NULL    AUTO_INCREMENT,
  source_id     VARCHAR(100)  NOT NULL,
  transfer_id   BIGINT        NOT NULL,
  allergy       VARCHAR(256)  NULL,
  source        VARCHAR(30)   NOT NULL,
  PRIMARY KEY   (id),
  FOREIGN KEY   (transfer_id)  REFERENCES  gpconnect.transfers_of_care(id)
);

CREATE TABLE gpconnect.contact_headlines (
  id            BIGINT        NOT NULL    AUTO_INCREMENT,
  source_id     VARCHAR(100)  NOT NULL,
  transfer_id   BIGINT        NOT NULL,
  contact_name  VARCHAR(256)  NULL,
  source        VARCHAR(30)   NOT NULL,
  PRIMARY KEY   (id),
  FOREIGN KEY   (transfer_id)  REFERENCES  gpconnect.transfers_of_care(id)
);

CREATE TABLE gpconnect.medication_headlines (
  id            BIGINT        NOT NULL    AUTO_INCREMENT,
  source_id     VARCHAR(100)  NOT NULL,
  transfer_id   BIGINT        NOT NULL,
  medication    VARCHAR(256)  NULL,
  source        VARCHAR(30)   NOT NULL,
  PRIMARY KEY   (id),
  FOREIGN KEY   (transfer_id)  REFERENCES  gpconnect.transfers_of_care(id)
);

CREATE TABLE gpconnect.problem_headlines (
  id            BIGINT        NOT NULL    AUTO_INCREMENT,
  source_id     VARCHAR(100)  NOT NULL,
  transfer_id   BIGINT        NOT NULL,
  problem       VARCHAR(256)  NULL,
  source        VARCHAR(30)   NOT NULL,
  PRIMARY KEY   (id),
  FOREIGN KEY   (transfer_id)  REFERENCES  gpconnect.transfers_of_care(id)
);

/* Delete the answer user (grant all to workaround MySQL not supporting 'IF EXISTS' for users) */
GRANT ALL ON gpconnect.* TO 'answer' IDENTIFIED BY 'answer99q';
DROP USER 'answer';
FLUSH PRIVILEGES;

/* Create a new answer user with full privileges */
CREATE USER 'answer'                              IDENTIFIED BY 'answer99q';
GRANT ALL ON gpconnect.* TO 'answer'@'%'          IDENTIFIED BY 'answer99q';
GRANT ALL ON gpconnect.* TO 'answer'@'localhost'  IDENTIFIED BY 'answer99q';
FLUSH PRIVILEGES;