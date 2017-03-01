package uk.gov.hscic.patient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.activation.UnsupportedDataTypeException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Appointment;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Bundle.Entry;
import ca.uhn.fhir.model.dstu2.resource.Composition;
import ca.uhn.fhir.model.dstu2.resource.Composition.Section;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationDispense;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.model.dstu2.resource.Parameters;
import ca.uhn.fhir.model.dstu2.resource.Parameters.Parameter;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.valueset.AddressTypeEnum;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.BundleTypeEnum;
import ca.uhn.fhir.model.dstu2.valueset.CompositionStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointSystemEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.IssueSeverityEnum;
import ca.uhn.fhir.model.dstu2.valueset.IssueTypeEnum;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import uk.gov.hscic.OperationConstants;
import uk.gov.hscic.OperationOutcomeFactory;
import uk.gov.hscic.appointments.AppointmentResourceProvider;
import uk.gov.hscic.common.util.NhsCodeValidator;
import uk.gov.hscic.medication.model.PatientMedicationHTML;
import uk.gov.hscic.medication.search.MedicationSearch;
import uk.gov.hscic.medications.MedicationAdministrationResourceProvider;
import uk.gov.hscic.medications.MedicationDispenseResourceProvider;
import uk.gov.hscic.medications.MedicationOrderResourceProvider;
import uk.gov.hscic.medications.MedicationResourceProvider;
import uk.gov.hscic.organization.OrganizationResourceProvider;
import uk.gov.hscic.patient.adminitems.model.AdminItemData;
import uk.gov.hscic.patient.adminitems.search.AdminItemSearch;
import uk.gov.hscic.patient.allergies.model.AllergyData;
import uk.gov.hscic.patient.allergies.search.AllergySearch;
import uk.gov.hscic.patient.careRecordHtml.*;
import uk.gov.hscic.patient.clinicalitems.model.ClinicalItemData;
import uk.gov.hscic.patient.clinicalitems.search.ClinicalItemSearch;
import uk.gov.hscic.patient.encounters.model.EncounterData;
import uk.gov.hscic.patient.encounters.search.EncounterSearch;
import uk.gov.hscic.patient.immunisations.model.ImmunisationData;
import uk.gov.hscic.patient.immunisations.search.ImmunisationSearch;
import uk.gov.hscic.patient.investigations.model.InvestigationListHTML;
import uk.gov.hscic.patient.investigations.search.InvestigationSearch;
import uk.gov.hscic.patient.observations.model.ObservationData;
import uk.gov.hscic.patient.observations.search.ObservationSearch;
import uk.gov.hscic.patient.patientsummary.model.PatientSummaryListHTML;
import uk.gov.hscic.patient.patientsummary.search.PatientSummarySearch;
import uk.gov.hscic.patient.problems.model.ProblemListHTML;
import uk.gov.hscic.patient.problems.search.ProblemSearch;
import uk.gov.hscic.patient.referral.model.ReferralListHTML;
import uk.gov.hscic.patient.referral.search.ReferralSearch;
import uk.gov.hscic.patient.summary.model.PatientDetails;
import uk.gov.hscic.patient.summary.search.PatientSearch;
import uk.gov.hscic.patient.summary.store.PatientStore;
import uk.gov.hscic.practitioner.PractitionerResourceProvider;

@Component
public class PatientResourceProvider implements IResourceProvider {
    private static final String REGISTRATION_TYPE_EXTENSION_URL = "http://fhir.nhs.net/StructureDefinition/extension-registration-type-1";
    private static final String REGISTRATION_STATUS_EXTENSION_URL = "http://fhir.nhs.net/StructureDefinition/extension-registration-status-1";
    private static final String REGISTRATION_PERIOD_EXTENSION_URL = "http://fhir.nhs.net/StructureDefinition/extension-registration-period-1";
    private static final String TEMPORARY_RESIDENT_REGISTRATION_TYPE = "T";
    private static final String ACTIVE_REGISTRATION_STATUS = "A";

    @Autowired
    private PractitionerResourceProvider practitionerResourceProvider;
    @Autowired
    private OrganizationResourceProvider organizationResourceProvider;
    @Autowired
    private MedicationResourceProvider medicationResourceProvider;
    @Autowired
    private MedicationOrderResourceProvider medicationOrderResourceProvider;
    @Autowired
    private MedicationDispenseResourceProvider medicationDispenseResourceProvider;
    @Autowired
    private MedicationAdministrationResourceProvider medicationAdministrationResourceProvider;
    @Autowired
    private AppointmentResourceProvider appointmentResourceProvider;
    @Autowired
    private PatientStore patientStore;
    @Autowired
    private PatientSearch patientSearch;
    @Autowired
    private PatientSummarySearch patientSummarySearch;
    @Autowired
    private ProblemSearch problemSearch;
    @Autowired
    private EncounterSearch encounterSearch;
    @Autowired
    private AllergySearch allergySearch;
    @Autowired
    private ClinicalItemSearch clinicalItemsSearch;
    @Autowired
    private MedicationSearch medicationSearch;
    @Autowired
    private ReferralSearch referralSearch;
    @Autowired
    private ObservationSearch observationSearch;
    @Autowired
    private InvestigationSearch investigationSearch;
    @Autowired
    private ImmunisationSearch immunisationSearch;
    @Autowired
    private AdminItemSearch adminItemSearch;

    @Override
    public Class<Patient> getResourceType() {
        return Patient.class;
    }

    @Read()
    public Patient getPatientById(@IdParam IdDt internalId) {
        PatientDetails patientDetails = patientSearch.findPatientByInternalID(internalId.getIdPart());

        if (patientDetails == null) {
            throw new ResourceNotFoundException("No patient details found for patient ID: " + internalId.getIdPart(),
                    OperationOutcomeFactory.buildOperationOutcome(OperationConstants.SYSTEM_WARNING_CODE,
                            OperationConstants.CODE_PATIENT_NOT_FOUND, OperationConstants.COD_CONCEPT_RECORD_NOT_FOUND,
                            OperationConstants.META_GP_CONNECT_PATIENT, IssueTypeEnum.NOT_FOUND));
        }
        return patientDetailsToPatientResourceConverter(patientDetails);
    }

    @Search
    public List<Patient> getPatientByPatientId(@RequiredParam(name = Patient.SP_IDENTIFIER) TokenParam patientId) {
        List<Patient> patients = new ArrayList<>();
        PatientDetails patientDetailsReturned = patientSearch.findPatient(patientId.getValue());

        if (patientDetailsReturned != null) {
            List<PatientDetails> patientDetailsList = Collections.singletonList(patientDetailsReturned);

            for (PatientDetails patientDetails : patientDetailsList) {
                Patient patient = patientDetailsToPatientResourceConverter(patientDetails);
                patient.setId(patientDetails.getId());
                patients.add(patient);
            }
        }

        if (patients.isEmpty()) {
            throw new ResourceNotFoundException("No patient details found for patient ID: ",
                    OperationOutcomeFactory.buildOperationOutcome(OperationConstants.SYSTEM_WARNING_CODE,
                            OperationConstants.CODE_PATIENT_NOT_FOUND, OperationConstants.COD_CONCEPT_RECORD_NOT_FOUND,
                            OperationConstants.META_GP_CONNECT_OPERATIONOUTCOME, IssueTypeEnum.NOT_FOUND));

        }

        return patients;
    }

    @SuppressWarnings("deprecation")
    @Operation(name = "$gpc.getcarerecord")
    public Bundle getPatientCareRecord(@ResourceParam Parameters params) throws UnsupportedDataTypeException {
        ArrayList<String> nhsNumber = new ArrayList<>();
        ArrayList<String> sectionsParamList = new ArrayList<>();
        Date fromDate = null;
        Date toDate = null;
        Date requestedFromDate = null;
        Date requestedToDate = null;

        // Extract the parameters
        boolean recordSectionNotPresent = true;

        for (Parameter param : params.getParameter()) {
            if (!"patientNHSNumber".equals(param.getName()) && !"recordSection".equals(param.getName())
                    && !"timePeriod".equals(param.getName())) {
                throw new UnprocessableEntityException("Parameters are incorrect",
                        OperationOutcomeFactory.buildOperationOutcome(OperationConstants.SYSTEM_WARNING_CODE,
                                OperationConstants.CODE_INVALID_PARAMETER,
                                OperationConstants.COD_CONCEPT_RECORD_INVALID_PARAMETER,
                                OperationConstants.META_GP_CONNECT_OPERATIONOUTCOME, IssueTypeEnum.INVALID_CONTENT));
            }

            IDatatype value = param.getValue();
            if (value instanceof IdentifierDt) {
                nhsNumber.add(((IdentifierDt) value).getValue());

                if (((IdentifierDt) value).getValue() == null) {
                    throw new InvalidRequestException(OperationConstants.SYSTEM_INVALID,
                            OperationOutcomeFactory.buildOperationOutcome(OperationConstants.SYSTEM_WARNING_CODE,
                                    OperationConstants.CODE_INVALID_NHS_NUMBER,
                                    OperationConstants.COD_CONCEPT_RECORD_NOT_FOUND,
                                    OperationConstants.META_GP_CONNECT_OPERATIONOUTCOME, IssueTypeEnum.NOT_FOUND));
                }

                if (nhsNumber.get(0).isEmpty()) {
                    throw new InvalidRequestException(OperationConstants.SYSTEM_INVALID,
                            OperationOutcomeFactory.buildOperationOutcome(OperationConstants.SYSTEM_WARNING_CODE,
                                    OperationConstants.CODE_INVALID_NHS_NUMBER,
                                    OperationConstants.COD_CONCEPT_RECORD_NOT_FOUND,
                                    OperationConstants.META_GP_CONNECT_OPERATIONOUTCOME, IssueTypeEnum.NOT_FOUND));
                }

                if (nhsNumber.size() > 1) {
                    throw new InvalidRequestException("Bad Request Exception",
                            OperationOutcomeFactory.buildOperationOutcome(OperationConstants.SYSTEM_WARNING_CODE,
                                    OperationConstants.CODE_INVALID_IDENTIFIER_SYSTEM,
                                    OperationConstants.COD_CONCEPT_RECORD_NHS_NUMBER_INVALID,
                                    OperationConstants.META_GP_CONNECT_OPERATIONOUTCOME,
                                    IssueTypeEnum.INVALID_CONTENT));
                }
            } else if (value instanceof CodeableConceptDt) {
                recordSectionNotPresent = false;

                List<CodingDt> coading = ((CodeableConceptDt) value).getCoding();

                String systemCheck = coading.get(0).getSystem();
                String sectionName = coading.get(0).getCode();

                if (sectionName == null || systemCheck == null) {
                    throw new UnprocessableEntityException("System Invalid ",
                            OperationOutcomeFactory.buildOperationOutcome(OperationConstants.SYSTEM_WARNING_CODE,
                                    OperationConstants.CODE_INVALID_PARAMETER,
                                    OperationConstants.COD_CONCEPT_RECORD_NOT_FOUND,
                                    OperationConstants.META_GP_CONNECT_OPERATIONOUTCOME, IssueTypeEnum.NOT_FOUND));
                }

                checkSectionCase(sectionName);

                if (!coading.get(0).getSystem().equals(OperationConstants.SYSTEM_RECORD_SECTION)) {
                    throw new InvalidRequestException(OperationConstants.SYSTEM_INVALID);
                }

                sectionsParamList.add(coading.get(0).getCode());

                if (sectionsParamList.size() > 1) {
                    throw new InvalidRequestException("Bad Request Exception",
                            OperationOutcomeFactory.buildOperationOutcome(OperationConstants.SYSTEM_WARNING_CODE,
                                    OperationConstants.CODE_INVALID_IDENTIFIER_SYSTEM,
                                    OperationConstants.COD_CONCEPT_RECORD_MULTIPLE_SECTIONS_ADDED,
                                    OperationConstants.META_GP_CONNECT_OPERATIONOUTCOME,
                                    IssueTypeEnum.INVALID_CONTENT));
                }
            } else if (value instanceof PeriodDt) {
                fromDate = ((PeriodDt) value).getStart();
                toDate = ((PeriodDt) value).getEnd();
                requestedFromDate = ((PeriodDt) value).getStart();
                requestedToDate = ((PeriodDt) value).getEnd();
                
                if (fromDate != null && toDate != null && fromDate.after(toDate)) {
                    throw new UnprocessableEntityException("Dates are invalid: ",
                            OperationOutcomeFactory.buildOperationOutcome(OperationConstants.SYSTEM_WARNING_CODE,
                                    OperationConstants.CODE_INVALID_PARAMETER,
                                    OperationConstants.COD_CONCEPT_RECORD_NOT_FOUND,
                                    OperationConstants.META_GP_CONNECT_OPERATIONOUTCOME, IssueTypeEnum.NOT_FOUND));
                }
                Calendar toCalendar = Calendar.getInstance();
                if (toDate != null) {
                    toCalendar.setTime(toDate);

                    if (null != ((PeriodDt) value).getEndElement().getPrecision()) {
                        switch (((PeriodDt) value).getEndElement().getPrecision()) {
                        case YEAR:
                            toCalendar.add(Calendar.YEAR, 1);
                            break;
                        case MONTH:
                            toCalendar.add(Calendar.MONTH, 1);
                            break;
                        case DAY:
                            toCalendar.add(Calendar.DATE, 1);
                            break;
                        default:
                            break;
                        }
                    }

                    toDate = toCalendar.getTime();
                }
            }
        }

        if (recordSectionNotPresent) {
            throw new InvalidRequestException("No record section");
        }

        // Build Bundle
        Bundle bundle = new Bundle();
        bundle.setType(BundleTypeEnum.DOCUMENT);
        OperationOutcome operationOutcome = new OperationOutcome();

        for (int i = 0; i < sectionsParamList.size(); i++) {
            if (sectionsParamList.get(i) == null || sectionsParamList.get(i).length() != 3) {
                throw new ResourceNotFoundException(
                        "NHS number Invalid " + OperationOutcomeFactory.buildOperationOutcome(
                                OperationConstants.SYSTEM_WARNING_CODE, OperationConstants.CODE_INVALID_NHS_NUMBER,
                                OperationConstants.COD_CONCEPT_RECORD_NHS_NUMBER_INVALID,
                                OperationConstants.META_GP_CONNECT_OPERATIONOUTCOME, IssueTypeEnum.INVALID_CONTENT));
            }
        }

        if (nhsNumber.isEmpty()) {
            throw new InvalidRequestException("NHS number not supplied");
        } else {
            if (!NhsCodeValidator.nhsNumberValid(nhsNumber.get(0))) {
                throw new InvalidRequestException("NHS number Invalid " + OperationOutcomeFactory.buildOperationOutcome(
                        OperationConstants.SYSTEM_WARNING_CODE, OperationConstants.CODE_INVALID_NHS_NUMBER,
                        OperationConstants.COD_CONCEPT_RECORD_NHS_NUMBER_INVALID,
                        OperationConstants.META_GP_CONNECT_OPERATIONOUTCOME, IssueTypeEnum.INVALID_CONTENT));
            } else {
                // Build the Patient Resource and add it to the bundle
                try {
                    String patientID;
                    Entry patientEntry = new Entry();
                    List<Patient> patients = getPatientByPatientId(new TokenParam("", nhsNumber.get(0)));

                    if (patients != null && !patients.isEmpty()) {
                        patientEntry.setResource(patients.get(0));
                        patientEntry.setFullUrl("Patient/" + patients.get(0).getId().getIdPart());
                        patientID = patients.get(0).getId().getIdPart();
                    } else {
                        operationOutcome.addIssue().setSeverity(IssueSeverityEnum.ERROR)
                                .setDetails("No patient details found for patient NHS Number: " + nhsNumber.get(0));
                        operationOutcome.getMeta().addProfile(OperationConstants.META_GP_CONNECT_OPERATIONOUTCOME);
                        throw new InternalErrorException(
                                "No patient details found for patient NHS Number: " + nhsNumber.get(0),
                                operationOutcome);
                    }

                    Composition careRecordComposition = new Composition();
                    careRecordComposition.setDate(new DateTimeDt(Calendar.getInstance().getTime()));

                    CodingDt coding = new CodingDt().setSystem("http://snomed.info/sct").setCode("425173008")
                            .setDisplay("record extract (record artifact)");
                    CodeableConceptDt codableConcept = new CodeableConceptDt().addCoding(coding)
                            .setText("record extract (record artifact)");
                    careRecordComposition.setType(codableConcept);

                    CodingDt classCoding = new CodingDt().setSystem("http://snomed.info/sct").setCode("700232004")
                            .setDisplay("general medical service (qualifier value)");
                    CodeableConceptDt classCodableConcept = new CodeableConceptDt().addCoding(classCoding)
                            .setText("general medical service (qualifier value)");
                    careRecordComposition.setClassElement(classCodableConcept);

                    careRecordComposition.setTitle("Patient Care Record");
                    careRecordComposition.setStatus(CompositionStatusEnum.FINAL);
                    careRecordComposition.setSubject(new ResourceReferenceDt("Patient/" + patientID));
                    careRecordComposition.getMeta()
                            .addProfile(OperationConstants.META_GP_CONNECT_CARERECORD_COMPOSITION);

                    // Build requested sections
                    if (!sectionsParamList.isEmpty()) {
                        HtmlPage htmlPage;
                        ArrayList<Section> sectionsList = new ArrayList<>();
                        BuildHtmlTable buildTable = new BuildHtmlTable();

                        for (String sectionName : sectionsParamList) {
                            checkSectionCase(sectionName);

                            Section section = new Section();

                            switch (sectionName) {
                            case "SUM":
                                if (nhsNumber.get(0) == null) {
                                    throw new AssertionError();
                                }

                                List<PatientSummaryListHTML> patientSummaryList = patientSummarySearch
                                        .findAllPatientSummaryHTMLTables(nhsNumber.get(0));

                                if (patientSummaryList != null && !patientSummaryList.isEmpty()) {
                                    if (patientSummaryList.get(0).getHtml().contains("This is confidential")) {
                                        throw new ForbiddenOperationException("This Data Is Confidential",
                                                OperationOutcomeFactory.buildOperationOutcome(
                                                        OperationConstants.SYSTEM_WARNING_CODE,
                                                        OperationConstants.CODE_NO_PATIENT_CONSENT,
                                                        OperationConstants.COD_CONCEPT_RECORD_PATIENT_DATA_CONFIDENTIAL,
                                                        OperationConstants.META_GP_CONNECT_OPERATIONOUTCOME,
                                                        IssueTypeEnum.NOT_FOUND));
                                    } else {
                                        section = SectionsCreationClass.buildSection(
                                                OperationConstants.SYSTEM_RECORD_SECTION, "SUM",
                                                patientSummaryList.get(0).getHtml(), OperationConstants.SUMMARY,
                                                section, OperationConstants.SUMMARY);

                                        sectionsList.add(section);
                                    }
                                } else {
                                    String htmlTable = buildTable.buildEmptyHtml(OperationConstants.SUMMARY);
                                    section = SectionsCreationClass.buildSection(
                                            OperationConstants.SYSTEM_RECORD_SECTION, "SUM",

                                            htmlTable, OperationConstants.SUMMARY, section, OperationConstants.SUMMARY);

                                    sectionsList.add(section);
                                }

                                break;

                            case "PRB":
                                if (toDate != null && fromDate != null) {
                                    throw new InvalidRequestException(OperationConstants.DATE_RANGES_NOT_ALLOWED);
                                } else {
                                    htmlPage = new HtmlPage("Problems", "Problems" ,"PRB");
                                    PageSection activeProblems = new PageSection("Active Problems and Issues");
                                    PageSection inactiveProblems = new PageSection("Inactive Problems and Issues");
                                    List<List<Object>> problemActiveRows = new ArrayList<>();
                                    List<List<Object>> problemInactiveRows = new ArrayList<>();
                                    List<ProblemListHTML> problemList = problemSearch.findAllProblemHTMLTables(nhsNumber.get(0));
                                    if (problemList != null && !problemList.isEmpty()) {
                                        for (ProblemListHTML problemListHTML : problemList) {
                                            if ("Active".equals(problemListHTML.getActiveOrInactive())) {
                                                problemActiveRows.add(Arrays.asList(problemListHTML.getStartDate(), problemListHTML.getEntry(), problemListHTML.getSignificance(), problemListHTML.getDetails()));
                                            } else {
                                                problemInactiveRows.add(Arrays.asList(problemListHTML.getStartDate(), problemListHTML.getEndDate(), problemListHTML.getEntry(), problemListHTML.getSignificance(), problemListHTML.getDetails()));
                                            }
                                        }
                                    }
                                    activeProblems.setTable(new PageSectionHtmlTable(Arrays.asList("Start Date", "Entry", "Significance", "Details"), problemActiveRows));
                                    inactiveProblems.setTable(new PageSectionHtmlTable(Arrays.asList("Start Date", "End Date", "Entry", "Significance", "Details"), problemInactiveRows));
                                    htmlPage.addPageSection(activeProblems);
                                    htmlPage.addPageSection(inactiveProblems);
                                    sectionsList.add(FhirSectionBuilder.build(htmlPage));
                                }
                                break;

                            case "ENC":
                                htmlPage = new HtmlPage("Encounters", "Encounters" ,"ENC");
                                PageSection encountersSection = new PageSection("Encounters");
                                encountersSection.serDateRange(requestedFromDate, requestedToDate);
                                List<EncounterData> encounterList = encounterSearch.findAllEncounterHTMLTables(nhsNumber.get(0), fromDate, toDate);
                                List<List<Object>> encounterRows = new ArrayList<>();
                                if (encounterList != null && !encounterList.isEmpty()) {
                                    for (EncounterData encounter : encounterList) {
                                        encounterRows.add(Arrays.asList(encounter.getEncounterDate(), encounter.getTitle(), encounter.getDetails()));
                                    }
                                }
                                encountersSection.setTable(new PageSectionHtmlTable(Arrays.asList("Date", "Title", "Details"), encounterRows));
                                htmlPage.addPageSection(encountersSection);
                                sectionsList.add(FhirSectionBuilder.build(htmlPage));
                                break;

                            case "ALL":
                                if (toDate != null && fromDate != null) {
                                    throw new InvalidRequestException(OperationConstants.DATE_RANGES_NOT_ALLOWED);
                                } else {
                                    htmlPage = new HtmlPage("Allergies and Adverse Reactions", "Allergies and Adverse Reactions", "ALL");
                                    PageSection currentAllergiesSection = new PageSection("Current Allergies and Adverse Reactions");
                                    PageSection historicalAllergiesSection = new PageSection("Historical Allergies and Adverse Reactions");
                                    List<List<Object>> currentAllergyRows = new ArrayList<>();
                                    List<List<Object>> historicalAllergyRows = new ArrayList<>();
                                    List<AllergyData> allergyList = allergySearch.findAllAllergyHTMLTables(nhsNumber.get(0));
                                    if (allergyList != null && !allergyList.isEmpty()) {
                                        for (AllergyData allergyData : allergyList) {
                                            if ("Current".equals(allergyData.getCurrentOrHistoric())) {
                                                currentAllergyRows.add(Arrays.asList(allergyData.getStartDate(), allergyData.getDetails()));
                                            } else {
                                                historicalAllergyRows.add(Arrays.asList(allergyData.getStartDate(), allergyData.getEndDate(), allergyData.getDetails()));
                                            }
                                        }
                                    }
                                    currentAllergiesSection.setTable(new PageSectionHtmlTable(Arrays.asList("Start Date", "Details"), currentAllergyRows));
                                    historicalAllergiesSection.setTable(new PageSectionHtmlTable(Arrays.asList("Start Date", "End Date", "Details"), historicalAllergyRows));
                                    htmlPage.addPageSection(currentAllergiesSection);
                                    htmlPage.addPageSection(historicalAllergiesSection);
                                    sectionsList.add(FhirSectionBuilder.build(htmlPage));
                                }
                                break;

                            case "CLI":
                                htmlPage = new HtmlPage("Clinical Items", "Clinical Items" ,"CLI");
                                PageSection clinicalItemsSection = new PageSection("Clinical Items");
                                clinicalItemsSection.serDateRange(requestedFromDate, requestedToDate);
                                List<ClinicalItemData> clinicalItemList = clinicalItemsSearch.findAllClinicalItemHTMLTables(nhsNumber.get(0), fromDate, toDate);
                                List<List<Object>> clinicalItemsRows = new ArrayList<>();
                                if (clinicalItemList != null && !clinicalItemList.isEmpty()) {
                                    for (ClinicalItemData clinicalItemData : clinicalItemList) {
                                        clinicalItemsRows.add(Arrays.asList(clinicalItemData.getDate(), clinicalItemData.getEntry(), clinicalItemData.getDetails()));
                                    }
                                }
                                clinicalItemsSection.setTable(new PageSectionHtmlTable(Arrays.asList("Date", "Entry", "Details"), clinicalItemsRows));
                                htmlPage.addPageSection(clinicalItemsSection);
                                sectionsList.add(FhirSectionBuilder.build(htmlPage));
                                break;

                            case "MED":
                                if (toDate != null && fromDate != null) {
                                    throw new InvalidRequestException(OperationConstants.DATE_RANGES_NOT_ALLOWED);
                                } else {
                                    htmlPage = new HtmlPage("Medications", "Medications", "MED");
                                    PageSection currentMedSection = new PageSection("Current Medication Issues");
                                    PageSection repeatMedSection = new PageSection("Current Repeat Medications");
                                    PageSection pastMedSection = new PageSection("Past Medications");
                                    List<List<Object>> currentMedRows = new ArrayList<>();
                                    List<List<Object>> repeatMedRows = new ArrayList<>();
                                    List<List<Object>> pastMedRows = new ArrayList<>();
                                    List<PatientMedicationHTML> medicationList = medicationSearch.findPatientMedicationHTML(nhsNumber.get(0));
                                    if (medicationList != null && !medicationList.isEmpty()) {
                                        for (PatientMedicationHTML patientMedicationHTML : medicationList) {
                                            switch(patientMedicationHTML.getCurrentRepeatPast()){
                                                case "Current" :
                                                    currentMedRows
                                                        .add(Arrays.asList(patientMedicationHTML.getStartDate(),
                                                                patientMedicationHTML.getMedicationItem(),
                                                                patientMedicationHTML.getTypeMed(),
                                                                patientMedicationHTML.getScheduledEnd(),
                                                                patientMedicationHTML.getDaysDuration(),
                                                                patientMedicationHTML.getDetails()));
                                                    break;
                                                case "Repeat" :
                                                    repeatMedRows
                                                        .add(Arrays.asList(patientMedicationHTML.getLastIssued(),
                                                                patientMedicationHTML.getMedicationItem(),
                                                                patientMedicationHTML.getStartDate(),
                                                                patientMedicationHTML.getReviewDate(),
                                                                patientMedicationHTML.getNumberIssued(),
                                                                patientMedicationHTML.getMaxIssued(),
                                                                patientMedicationHTML.getDetails()));
                                                    break;
                                                case "Past" :
                                                    pastMedRows
                                                        .add(Arrays.asList(patientMedicationHTML.getStartDate(),
                                                                patientMedicationHTML.getMedicationItem(),
                                                                patientMedicationHTML.getTypeMed(),
                                                                patientMedicationHTML.getLastIssued(),
                                                                patientMedicationHTML.getReviewDate(),
                                                                patientMedicationHTML.getNumberIssued(),
                                                                patientMedicationHTML.getMaxIssued(),
                                                                patientMedicationHTML.getDetails()));
                                                    break;
                                            }
                                        }
                                    }
                                    currentMedSection.setTable(new PageSectionHtmlTable(Arrays.asList("Start Date", "Medication Item", "Type", "Scheduled End","Days Duration", "Details"), currentMedRows));
                                    repeatMedSection.setTable(new PageSectionHtmlTable(Arrays.asList("Last Issued", "Medication Item", "Start Date","Review Date", "Number Issued", "Max Issues", "Details"),repeatMedRows));
                                    pastMedSection.setTable(new PageSectionHtmlTable(Arrays.asList("Start Date", "Medication Item", "Type", "Last Issued","Review Date", "Number Issued", "Max Issues", "Details"),pastMedRows));
                                    htmlPage.addPageSection(currentMedSection);
                                    htmlPage.addPageSection(repeatMedSection);
                                    htmlPage.addPageSection(pastMedSection);
                                    sectionsList.add(FhirSectionBuilder.build(htmlPage));

                                    // // Sructured Data Search
                                    // List<MedicationOrder> medicationOrders =
                                    // medicationOrderResourceProvider
                                    // .getMedicationOrdersForPatientId(patientID);
                                    // HashSet<String>
                                    // medicationOrderMedicationsList = new
                                    // HashSet<>();
                                    // HashSet<String> medicationOrderList = new
                                    // HashSet<>();
                                    //
                                    // for (MedicationOrder medicationOrder :
                                    // medicationOrders) {
                                    // medicationOrderList.add(medicationOrder.getId().getIdPart());
                                    // }
                                    //
                                    // List<MedicationDispense>
                                    // medicationDispenses =
                                    // medicationDispenseResourceProvider
                                    // .getMedicationDispensesForPatientId(patientID);
                                    //
                                    // for (MedicationDispense
                                    // medicationDispense : medicationDispenses)
                                    // {
                                    // if (section == null) {
                                    // section = new Section();
                                    // }
                                    // // Add the medication Order to the bundle
                                    // Entry medicationDispenseEntry = new
                                    // Entry();
                                    // medicationDispenseEntry
                                    // .setFullUrl("MedicationDispense/" +
                                    // medicationDispense.getId().getIdPart());
                                    // medicationDispenseEntry.setResource(medicationDispense);
                                    //
                                    // medicationsToBundle.add(medicationDispenseEntry);
                                    // section.addEntry().setReference(medicationDispenseEntry.getFullUrl());
                                    // // If we have any new medicationOrders
                                    // //which
                                    // // were not found in the
                                    // // search for MedicationOrders for a
                                    // // patient
                                    // // we need to add them.
                                    // if
                                    // (!medicationOrderList.contains(medicationDispense.getAuthorizingPrescription()
                                    // .get(0).getReference().getIdPart())) {
                                    // try {
                                    // MedicationOrder medicationOrder =
                                    // medicationOrderResourceProvider
                                    // .getMedicationOrderById(medicationDispense
                                    // .getAuthorizingPrescription().get(0).getReference());
                                    // medicationOrders.add(medicationOrder);
                                    // medicationOrderList.add(medicationOrder.getId().getIdPart());
                                    // } catch (Exception ex) {
                                    // operationOutcome.addIssue().setSeverity(IssueSeverityEnum.ERROR)
                                    // .setDetails("MedicationOrder for
                                    // MedicaitonDispense (id: "
                                    // + medicationDispense.getId().getIdPart()
                                    // + ") could not be found in database");
                                    // }
                                    // }
                                    // }
                                    //
                                    // List<MedicationAdministration>
                                    // medicationAdministrations =
                                    // medicationAdministrationResourceProvider
                                    // .getMedicationAdministrationsForPatientId(patientID);
                                    //
                                    // for (MedicationAdministration
                                    // medicationAdministration :
                                    // medicationAdministrations) {
                                    // if (section == null) {
                                    // section = new Section();
                                    // }
                                    //
                                    // Entry medicationAdministrationEntry = new
                                    // Entry();
                                    // medicationAdministrationEntry.setFullUrl(
                                    // "MedicationAdministration/" +
                                    // medicationAdministration.getId().getIdPart());
                                    // medicationAdministrationEntry.setResource(medicationAdministration);
                                    // section.addEntry().setReference(medicationAdministrationEntry.getFullUrl());
                                    // medicationsToBundle.add(medicationAdministrationEntry);
                                    //
                                    // // If we have any new medicationOrders
                                    // which
                                    // // were not found in the
                                    // // search for MedicationOrders for a
                                    // patient
                                    // // we need to add them.
                                    // if (!medicationOrderList.contains(
                                    // medicationAdministration.getPrescription().getReference().getIdPart()))
                                    // {
                                    // try {
                                    // MedicationOrder medicationOrder =
                                    // medicationOrderResourceProvider
                                    // .getMedicationOrderById(
                                    // medicationAdministration.getPrescription().getReference());
                                    // medicationOrders.add(medicationOrder);
                                    // medicationOrderList.add(medicationOrder.getId().getIdPart());
                                    // } catch (Exception ex) {
                                    // operationOutcome.addIssue().setSeverity(IssueSeverityEnum.ERROR)
                                    // .setDetails("MedicationOrder for
                                    // MedicaitonAdministration (id: "
                                    // +
                                    // medicationAdministration.getId().getIdPart()
                                    // + ") could not be found in database");
                                    // }
                                    // }
                                    // }
                                    //
                                    // for (MedicationOrder medicationOrder :
                                    // medicationOrders) {
                                    // if (section == null) {
                                    // section = new Section();
                                    // }
                                    // // Add the medication Order to the bundle
                                    // Entry medicationOrderEntry = new Entry();
                                    // medicationOrderEntry
                                    // .setFullUrl("MedicationOrder/" +
                                    // medicationOrder.getId().getIdPart());
                                    // medicationOrderEntry.setResource(medicationOrder);
                                    // section.addEntry().setReference(medicationOrderEntry.getFullUrl());
                                    // medicationsToBundle.add(medicationOrderEntry);
                                    //
                                    // // Store the referenced medicaitons in a
                                    // set
                                    // // so we can get
                                    // // all the medications once and we won't
                                    // // have duplicates
                                    // IdDt medicationId =
                                    // ((ResourceReferenceDt)
                                    // medicationOrder.getMedication())
                                    // .getReference();
                                    // medicationOrderMedicationsList.add(medicationId.getValue());
                                    // medicationId = ((ResourceReferenceDt)
                                    // medicationOrder.getDispenseRequest()
                                    // .getMedication()).getReference();
                                    // medicationOrderMedicationsList.add(medicationId.getValue());
                                    // }
                                    //
                                    // for (String medicationId :
                                    // medicationOrderMedicationsList) {
                                    // try {
                                    // Entry medicationEntry = new Entry();
                                    // medicationEntry.setFullUrl(medicationId);
                                    // medicationEntry.setResource(
                                    // medicationResourceProvider.getMedicationById(new
                                    // IdDt(medicationId)));
                                    // section.addEntry().setReference(medicationEntry.getFullUrl());
                                    // medicationsToBundle.add(medicationEntry);
                                    // } catch (Exception ex) {
                                    // operationOutcome.addIssue().setSeverity(IssueSeverityEnum.ERROR)
                                    // .setDetails("Medication (ID: " +
                                    // medicationId
                                    // + ") for MedicaitonOrder could not be
                                    // found in database");
                                    // }
                                    //
                                    //
                                    // if (section != null) {
                                    // sectionsList.add(section);
                                }
                                break;

                            case "REF":
                                List<ReferralListHTML> referralList = referralSearch
                                        .findAllReferralHTMLTables(nhsNumber.get(0), fromDate, toDate);

                                if (referralList != null && !referralList.isEmpty()) {
                                    section = SectionsCreationClass.buildSection(
                                            OperationConstants.SYSTEM_RECORD_SECTION, "REF",
                                            referralList.get(0).getHtml(), "Referrals", section, "Referrals");

                                    sectionsList.add(section);
                                } else {
                                    String htmlTable = buildTable.buildEmptyHtml("Referrals");
                                    section = SectionsCreationClass.buildSection(
                                            OperationConstants.SYSTEM_RECORD_SECTION, "REF", htmlTable, "Referrals",
                                            section, "Referrals");
                                    sectionsList.add(section);

                                }

                                break;

                            case "OBS":
                                if (toDate != null && fromDate != null) {
                                    throw new InvalidRequestException(OperationConstants.DATE_RANGES_NOT_ALLOWED);
                                } else {
                                    htmlPage = new HtmlPage("Observations", "Observations" ,"OBS");
                                    PageSection observationSection = new PageSection("Observations");
                                    List<List<Object>> observationRows = new ArrayList<>();
                                    List<ObservationData> observationList = observationSearch.findAllObservationHTMLTables(nhsNumber.get(0));
                                    if (observationList != null && !observationList.isEmpty()) {
                                        for (ObservationData observationItemData : observationList) {
                                            observationRows.add(Arrays.asList(observationItemData.getObservationDate(),observationItemData.getEntry(), observationItemData.getValue(),observationItemData.getValue()));
                                        }
                                    }
                                    observationSection.setTable(new PageSectionHtmlTable(Arrays.asList("Date", "Entry", "Value", "Details"), observationRows));
                                    htmlPage.addPageSection(observationSection);
                                    sectionsList.add(FhirSectionBuilder.build(htmlPage));
                                }
                                break;

                            case "INV":
                                List<InvestigationListHTML> investigationList = investigationSearch
                                        .findAllInvestigationHTMLTables(nhsNumber.get(0));

                                if (investigationList != null && !investigationList.isEmpty()) {

                                    section = SectionsCreationClass.buildSection(
                                            OperationConstants.SYSTEM_RECORD_SECTION, "INV",
                                            investigationList.get(0).getHtml(), "Investigations", section,
                                            "Investigations");
                                    sectionsList.add(section);

                                } else {
                                    String htmlTable = buildTable.buildEmptyHtml("Investigations");
                                    section = SectionsCreationClass.buildSection(
                                            OperationConstants.SYSTEM_RECORD_SECTION, "INV", htmlTable,
                                            "Investigations", section, "Investigations");
                                    sectionsList.add(section);
                                }

                                break;

                            case "IMM":
                                if (toDate != null && fromDate != null) {
                                    throw new InvalidRequestException(OperationConstants.DATE_RANGES_NOT_ALLOWED);
                                } else {
                                    htmlPage = new HtmlPage("Immunisations", "Immunisations" ,"IMM");
                                    PageSection immunisationSection = new PageSection("Immunisations");
                                    List<List<Object>> immunisationRows = new ArrayList<>();
                                    List<ImmunisationData> immunisationDataList = immunisationSearch.findAllImmunisationHTMLTables(nhsNumber.get(0));
                                    if (immunisationDataList != null && !immunisationDataList.isEmpty()) {
                                        for (ImmunisationData immunisationData : immunisationDataList) {
                                            immunisationRows.add(Arrays.asList(immunisationData.getDateOfVac(),immunisationData.getVaccination(), immunisationData.getPart(),immunisationData.getContents(), immunisationData.getDetails()));
                                        }
                                    }
                                    immunisationSection.setTable(new PageSectionHtmlTable(Arrays.asList("Date", "Vaccination", "Part", "Contents", "Details"),immunisationRows));
                                    htmlPage.addPageSection(immunisationSection);
                                    sectionsList.add(FhirSectionBuilder.build(htmlPage));
                                }
                                break;

                            case "ADM":
                                htmlPage = new HtmlPage("Administrative Items", "Administrative Items" ,"ADM");
                                PageSection administativeItemsSection = new PageSection("Administrative Items");
                                administativeItemsSection.serDateRange(requestedFromDate, requestedToDate);
                                List<List<Object>> adminItemsRows = new ArrayList<>();
                                List<AdminItemData> adminItemList = adminItemSearch.findAllAdminItemHTMLTables(nhsNumber.get(0), fromDate, toDate);
                                if (adminItemList != null && !adminItemList.isEmpty()) {
                                    for (AdminItemData adminItemData : adminItemList) {
                                        adminItemsRows.add(Arrays.asList(adminItemData.getAdminDate(),adminItemData.getEntry(), adminItemData.getDetails()));
                                    }
                                }
                                administativeItemsSection.setTable(new PageSectionHtmlTable(Arrays.asList("Date", "Entry", "Details"), adminItemsRows));
                                htmlPage.addPageSection(administativeItemsSection);
                                sectionsList.add(FhirSectionBuilder.build(htmlPage));
                                break;

                            default:
                                throw new UnprocessableEntityException("Dates are invalid: ",
                                        OperationOutcomeFactory.buildOperationOutcome(
                                                OperationConstants.SYSTEM_WARNING_CODE,
                                                OperationConstants.CODE_INVALID_PARAMETER,
                                                OperationConstants.COD_CONCEPT_RECORD_NOT_FOUND,
                                                OperationConstants.META_GP_CONNECT_OPERATIONOUTCOME,
                                                IssueTypeEnum.NOT_FOUND));
                            }
                        }

                        careRecordComposition.setSection(sectionsList);
                    }

                    // Build the Care Record Composition
                    Entry careRecordEntry = new Entry();
                    careRecordEntry.setResource(careRecordComposition);
                    ArrayList<Entry> medicationsToBundle = new ArrayList<>();

                    bundle.addEntry(careRecordEntry);

                    for (Entry e : medicationsToBundle) {
                        bundle.addEntry(e);
                    }

                    List<ResourceReferenceDt> careProviderPractitionerList = ((Patient) patientEntry.getResource())
                            .getCareProvider();

                    if (!careProviderPractitionerList.isEmpty()) {
                        careRecordComposition.setAuthor(Collections.singletonList(new ResourceReferenceDt(
                                careProviderPractitionerList.get(0).getReference().getValue())));
                        try {
                            Practitioner practitioner = practitionerResourceProvider.getPractitionerById(
                                    new IdDt(careProviderPractitionerList.get(0).getReference().getValue()));

                            if (practitioner == null) {
                                throw new ResourceNotFoundException("Practitioner Reference returning null",
                                        OperationOutcomeFactory.buildOperationOutcome(
                                                OperationConstants.SYSTEM_WARNING_CODE,
                                                OperationConstants.CODE_REFERENCE_NOT_FOUND,
                                                OperationConstants.COD_CONCEPT_RECORD_INVALID_REFERENCE,
                                                OperationConstants.META_GP_CONNECT_PRACTITIONER,
                                                IssueTypeEnum.NOT_FOUND));
                            }

                            practitioner.getMeta().addProfile(OperationConstants.META_GP_CONNECT_PRACTITIONER);

                            Entry practitionerEntry = new Entry().setResource(practitioner)
                                    .setFullUrl(careProviderPractitionerList.get(0).getReference().getValue());

                            bundle.addEntry(practitionerEntry);

                            Entry organizationEntry = new Entry();
                            organizationEntry.setResource(organizationResourceProvider.getOrganizationById(practitioner
                                    .getPractitionerRoleFirstRep().getManagingOrganization().getReference()));

                            organizationEntry.setFullUrl(practitioner.getPractitionerRoleFirstRep()
                                    .getManagingOrganization().getReference());

                            if (organizationEntry.getResource() == null || organizationEntry.getFullUrl() == null) {
                                throw new ResourceNotFoundException("organizationResource returning null",
                                        OperationOutcomeFactory.buildOperationOutcome(
                                                OperationConstants.SYSTEM_WARNING_CODE,
                                                OperationConstants.CODE_REFERENCE_NOT_FOUND,
                                                OperationConstants.COD_CONCEPT_RECORD_INVALID_REFERENCE,
                                                OperationConstants.META_GP_CONNECT_PRACTITIONER,
                                                IssueTypeEnum.NOT_FOUND));
                            }

                            bundle.addEntry(organizationEntry);
                        } catch (InternalErrorException ex) {
                            operationOutcome.addIssue().setSeverity(IssueSeverityEnum.ERROR)
                                    .setDetails(ex.getLocalizedMessage());
                        }
                    }

                    bundle.addEntry(patientEntry);
                } catch (InternalErrorException ex) {
                    // If the patient details could not be found
                    operationOutcome.addIssue().setSeverity(IssueSeverityEnum.ERROR).setDetails(ex.getMessage());
                }
            }
        }

        if (!operationOutcome.getIssue().isEmpty()) {
            Entry operationOutcomeEntry = new Entry();
            operationOutcomeEntry.setResource(operationOutcome);
            bundle.addEntry(new Entry());
            bundle.addEntry(operationOutcomeEntry);
        }

        return bundle;
    }

    @Search(compartmentName = "MedicationOrder")
    public List<MedicationOrder> getPatientMedicationOrders(@IdParam IdDt patientLocalId) {
        return medicationOrderResourceProvider.getMedicationOrdersForPatientId(patientLocalId.getIdPart());
    }

    @Search(compartmentName = "MedicationDispense")
    public List<MedicationDispense> getPatientMedicationDispenses(@IdParam IdDt patientLocalId) {
        return medicationDispenseResourceProvider.getMedicationDispensesForPatientId(patientLocalId.getIdPart());
    }

    @Search(compartmentName = "MedicationAdministration")
    public List<MedicationAdministration> getPatientMedicationAdministration(@IdParam IdDt patientLocalId) {
        return medicationAdministrationResourceProvider
                .getMedicationAdministrationsForPatientId(patientLocalId.getIdPart());
    }

    @Search(compartmentName = "Appointment")
    public List<Appointment> getPatientAppointments(@IdParam IdDt patientLocalId,
            @OptionalParam(name = "start") DateRangeParam startDate) {
        return appointmentResourceProvider.getAppointmentsForPatientIdAndDates(patientLocalId, startDate);
    }

    @Operation(name = "$gpc.registerpatient")
    public Bundle registerPatient(@ResourceParam Parameters params) {
        Patient unregisteredPatient = null;
        Patient registeredPatient = null;

        for (Parameter param : params.getParameter()) {
            if ("registerPatient".equalsIgnoreCase(param.getName())) {
                unregisteredPatient = (Patient) param.getResource();
            }
        }

        if (unregisteredPatient != null) {
            // check if the patient already exists
            PatientDetails patientDetails = patientSearch
                    .findPatient(unregisteredPatient.getIdentifierFirstRep().getValue());

            if (patientDetails == null) {
                patientStore.create(registerPatientResourceConverterToPatientDetail(unregisteredPatient));
                registeredPatient = patientDetailsToRegisterPatientResourceConverter(
                        patientSearch.findPatient(unregisteredPatient.getIdentifierFirstRep().getValue()));
            } else {
                registeredPatient = patientDetailsToRegisterPatientResourceConverter(patientDetails);
            }
        } else {
            throw new UnprocessableEntityException("Section Case Invalid: ",
                    OperationOutcomeFactory.buildOperationOutcome(OperationConstants.SYSTEM_WARNING_CODE,
                            OperationConstants.CODE_INVALID_PARAMETER, OperationConstants.COD_CONCEPT_RECORD_NOT_FOUND,
                            OperationConstants.META_GP_CONNECT_PRACTITIONER, IssueTypeEnum.NOT_FOUND));
        }

        Bundle bundle = new Bundle();
        bundle.setType(BundleTypeEnum.TRANSACTION_RESPONSE);

        bundle.addEntry().setResource(registeredPatient);
        return bundle;
    }

    private boolean checkSectionCase(String sectionName) {
        if (!sectionName.equals(sectionName.toUpperCase())) {
            throw new UnprocessableEntityException("Section Case Invalid: ",
                    OperationOutcomeFactory.buildOperationOutcome(OperationConstants.SYSTEM_WARNING_CODE,
                            OperationConstants.CODE_INVALID_PARAMETER, OperationConstants.COD_CONCEPT_RECORD_NOT_FOUND,
                            OperationConstants.META_GP_CONNECT_OPERATIONOUTCOME, IssueTypeEnum.NOT_FOUND));
        } else {
            return false;
        }
    }

    private PatientDetails registerPatientResourceConverterToPatientDetail(Patient patientResource) {
        PatientDetails patientDetails = new PatientDetails();
        HumanNameDt name = patientResource.getNameFirstRep();
        patientDetails.setForename(name.getGivenAsSingleString());
        patientDetails.setSurname(name.getFamilyAsSingleString());
        patientDetails.setDateOfBirth(patientResource.getBirthDate());
        patientDetails.setGender(patientResource.getGender());
        patientDetails.setNhsNumber(patientResource.getIdentifierFirstRep().getValue());

        List<ExtensionDt> registrationPeriodExtensions = patientResource
                .getUndeclaredExtensionsByUrl(REGISTRATION_PERIOD_EXTENSION_URL);
        ExtensionDt registrationPeriodExtension = registrationPeriodExtensions.get(0);
        PeriodDt registrationPeriod = (PeriodDt) registrationPeriodExtension.getValue();

        Date registrationStart = registrationPeriod.getStart();

        if (registrationStart.compareTo(new Date()) <= 1) {
            patientDetails.setRegistrationStartDateTime(registrationStart);
        } else {
            throw new IllegalArgumentException(String.format(
                    "The given registration start (%c) is not valid. The registration start cannot be in the future.",
                    registrationStart));
        }

        Date registrationEnd = registrationPeriod.getEnd();

        if (registrationEnd != null) {
            throw new IllegalArgumentException(String.format(
                    "The given registration end (%c) is not valid. The registration end should be left blank to indicate an open-ended registration period.",
                    registrationStart));
        }

        List<ExtensionDt> registrationStatusExtensions = patientResource
                .getUndeclaredExtensionsByUrl(REGISTRATION_STATUS_EXTENSION_URL);
        ExtensionDt registrationStatusExtension = registrationStatusExtensions.get(0);
        CodeableConceptDt registrationStatusCode = (CodeableConceptDt) registrationStatusExtension.getValue();
        String registrationStatus = registrationStatusCode.getCodingFirstRep().getCode();

        if (ACTIVE_REGISTRATION_STATUS.equals(registrationStatus)) {
            patientDetails.setRegistrationStatus(registrationStatus);
        } else {
            throw new IllegalArgumentException(String.format(
                    "The given registration status is not valid. Expected - A. Actual - %s", registrationStatus));
        }

        List<ExtensionDt> registrationTypeExtensions = patientResource
                .getUndeclaredExtensionsByUrl(REGISTRATION_TYPE_EXTENSION_URL);
        ExtensionDt registrationTypeExtension = registrationTypeExtensions.get(0);
        CodeableConceptDt registrationTypeCode = (CodeableConceptDt) registrationTypeExtension.getValue();
        String registrationType = registrationTypeCode.getCodingFirstRep().getCode();

        if (TEMPORARY_RESIDENT_REGISTRATION_TYPE.equals(registrationType)) {
            patientDetails.setRegistrationType(registrationType);
        } else {
            throw new IllegalArgumentException(String
                    .format("The given registration type is not valid. Expected - T. Actual - %s", registrationType));
        }

        return patientDetails;
    }

    // a cut-down Patient
    private Patient patientDetailsToRegisterPatientResourceConverter(PatientDetails patientDetails) {
        Patient patient = new Patient();
        patient.setId(patientDetails.getId());
        patient.addIdentifier(new IdentifierDt("http://fhir.nhs.net/Id/nhs-number", patientDetails.getNhsNumber()));

        HumanNameDt name = patient.addName();
        name.addFamily(patientDetails.getSurname());
        name.addGiven(patientDetails.getForename());
        name.setUse(NameUseEnum.USUAL);

        patient.setBirthDate(new DateDt(patientDetails.getDateOfBirth()));

        patient.setGender(AdministrativeGenderEnum.forCode(patientDetails.getGender().toLowerCase()));

        PeriodDt registrationPeriod = new PeriodDt();
        registrationPeriod.setStartWithSecondsPrecision(patientDetails.getRegistrationStartDateTime());
        registrationPeriod.setEndWithSecondsPrecision(patientDetails.getRegistrationEndDateTime());
        patient.addUndeclaredExtension(true, REGISTRATION_PERIOD_EXTENSION_URL, registrationPeriod);

        CodeableConceptDt registrationStatus = new CodeableConceptDt(
                "http://fhir.nhs.net/ValueSet/registration-status-1", patientDetails.getRegistrationStatus());
        patient.addUndeclaredExtension(true, REGISTRATION_STATUS_EXTENSION_URL, registrationStatus);

        CodeableConceptDt registrationType = new CodeableConceptDt("http://fhir.nhs.net/ValueSet/registration-type-1",
                patientDetails.getRegistrationType());
        patient.addUndeclaredExtension(true, REGISTRATION_TYPE_EXTENSION_URL, registrationType);

        return patient;
    }

    public Patient patientDetailsToPatientResourceConverter(PatientDetails patientDetails) {
        Patient patient = new Patient();
        patient.setId(patientDetails.getId());
        patient.addIdentifier(new IdentifierDt("http://fhir.nhs.net/Id/nhs-number", patientDetails.getNhsNumber()));

        Date lastUpdated = patientDetails.getLastUpdated();

        if (lastUpdated != null) {
            patient.getMeta().setLastUpdated(lastUpdated);
            patient.getMeta().setVersionId(String.valueOf(lastUpdated.getTime()));
        }

        HumanNameDt name = patient.addName();
        name.setText(patientDetails.getName());
        name.addFamily(patientDetails.getSurname());
        name.addGiven(patientDetails.getForename());
        name.addPrefix(patientDetails.getTitle());
        name.setUse(NameUseEnum.USUAL);

        patient.setBirthDate(new DateDt(patientDetails.getDateOfBirth()));
        patient.getMeta().addProfile(OperationConstants.META_GP_CONNECT_PATIENT);

        String addressLines = patientDetails.getAddress();

        if (addressLines != null) {
            AddressDt address = patient.addAddress();
            address.setUse(AddressUseEnum.HOME);
            address.setType(AddressTypeEnum.PHYSICAL);
            address.setText(addressLines);
        }

        Long gpId = patientDetails.getGpId();

        if (gpId != null) {
            Practitioner practitioner = practitionerResourceProvider.getPractitionerById(new IdDt(gpId));
            ResourceReferenceDt practitionerReference = new ResourceReferenceDt("Practitioner/" + gpId);
            practitionerReference.setDisplay(practitioner.getName().getPrefixFirstRep() + " "
                    + practitioner.getName().getGivenFirstRep() + " " + practitioner.getName().getFamilyFirstRep());
            patient.getCareProvider().add(practitionerReference);
        } else {
            throw new ResourceNotFoundException("No GP record exists "
                    + OperationOutcomeFactory.buildOperationOutcome(OperationConstants.SYSTEM_WARNING_CODE,
                            OperationConstants.CODE_PATIENT_NOT_FOUND, OperationConstants.COD_CONCEPT_RECORD_NOT_FOUND,
                            OperationConstants.META_GP_CONNECT_PRACTITIONER, IssueTypeEnum.NOT_FOUND));
        }

        String gender = patientDetails.getGender();
        if (gender != null) {
            patient.setGender(AdministrativeGenderEnum.forCode(gender.toLowerCase()));
        }

        String telephoneNumber = patientDetails.getTelephone();
        if (telephoneNumber != null) {
            ContactPointDt telephone = new ContactPointDt();
            telephone.setSystem(ContactPointSystemEnum.PHONE);
            telephone.setValue(telephoneNumber);
            telephone.setUse(ContactPointUseEnum.HOME);
            patient.setTelecom(Collections.singletonList(telephone));
        }

        Date registrationStartDateTime = patientDetails.getRegistrationStartDateTime();
        if (registrationStartDateTime != null) {
            PeriodDt registrationPeriod = new PeriodDt();
            registrationPeriod.setStartWithSecondsPrecision(registrationStartDateTime);
            registrationPeriod.setEndWithSecondsPrecision(patientDetails.getRegistrationEndDateTime());
            patient.addUndeclaredExtension(true, REGISTRATION_PERIOD_EXTENSION_URL, registrationPeriod);
        }

        String registrationStatusValue = patientDetails.getRegistrationStatus();
        if (registrationStatusValue != null) {
            CodeableConceptDt registrationStatus = new CodeableConceptDt(
                    "http://fhir.nhs.net/ValueSet/registration-status-1", registrationStatusValue);
            patient.addUndeclaredExtension(true, REGISTRATION_STATUS_EXTENSION_URL, registrationStatus);
        }

        String registrationTypeValue = patientDetails.getRegistrationType();
        if (registrationTypeValue != null) {
            CodeableConceptDt registrationType = new CodeableConceptDt(
                    "http://fhir.nhs.net/ValueSet/registration-type-1", registrationTypeValue);
            patient.addUndeclaredExtension(true, REGISTRATION_TYPE_EXTENSION_URL, registrationType);
        }

        return patient;
    }
}