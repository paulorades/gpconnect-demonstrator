package uk.gov.hscic.appointments;

import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Appointment;
import ca.uhn.fhir.model.dstu2.resource.Appointment.Participant;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.model.dstu2.valueset.AppointmentStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.IssueSeverityEnum;
import ca.uhn.fhir.model.dstu2.valueset.IssueTypeEnum;
import ca.uhn.fhir.model.dstu2.valueset.ParticipationStatusEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.ResourceVersionConflictException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hscic.OperationOutcomeFactory;
import uk.gov.hscic.SystemCode;
import uk.gov.hscic.SystemURL;
import uk.gov.hscic.appointment.appointment.AppointmentSearch;
import uk.gov.hscic.appointment.appointment.AppointmentStore;
import uk.gov.hscic.appointment.slot.SlotSearch;
import uk.gov.hscic.appointment.slot.SlotStore;
import uk.gov.hscic.location.LocationSearch;
import uk.gov.hscic.model.appointment.AppointmentDetail;
import uk.gov.hscic.model.appointment.SlotDetail;
import uk.gov.hscic.model.location.LocationDetails;
import uk.gov.hscic.model.patient.PatientDetails;
import uk.gov.hscic.model.practitioner.PractitionerDetails;
import uk.gov.hscic.patient.details.PatientSearch;
import uk.gov.hscic.practitioner.PractitionerSearch;

@Component
public class AppointmentResourceProvider implements IResourceProvider {

    @Autowired
    private AppointmentSearch appointmentSearch;

    @Autowired
    private AppointmentStore appointmentStore;

    @Autowired
    private SlotSearch slotSearch;

    @Autowired
    private SlotStore slotStore;

    @Autowired
    private PatientSearch patientSearch;

    @Autowired
    private PractitionerSearch practitionerSearch;

    @Autowired
    private LocationSearch locationSearch;

    @Override
    public Class<Appointment> getResourceType() {
        return Appointment.class;
    }

    @Read(version=true)
    public Appointment getAppointmentById(@IdParam IdDt appointmentId) {
    	Appointment appointment = null;

    	try {
    		Long id = appointmentId.getIdPartAsLong();

    		AppointmentDetail appointmentDetail = null;

    		// are we dealing with a request for a specific version of the appointment
        	if(appointmentId.hasVersionIdPart()) {

        		try {
	        		Long versionId = appointmentId.getVersionIdPartAsLong();

	        		appointmentDetail = appointmentSearch.findAppointmentByIDAndLastUpdated(id, new Date(versionId));

	        		if(appointmentDetail == null) {
	            		// 404 version of resource not found
	        			String msg = String.format("No appointment details found for ID: %s with versionId %s", appointmentId.getIdPart(), versionId);
	            		throw OperationOutcomeFactory.buildOperationOutcomeException(new ResourceNotFoundException(msg),
	            															   		 SystemCode.REFERENCE_NOT_FOUND,
	            															   		 IssueTypeEnum.NOT_FOUND);
	        		}
        		}
                catch(NumberFormatException nfe) {
                	// 404 resource not found - the versionId is valid according to FHIR
                	// however we have no entities matching that versionId
                	String msg = String.format("The version ID %s of the Appointment (ID - %s) is not valid", appointmentId.getVersionIdPart(), id);
            		throw OperationOutcomeFactory.buildOperationOutcomeException(new ResourceNotFoundException(msg),
        			   		 													 SystemCode.REFERENCE_NOT_FOUND,
        			   		 													 IssueTypeEnum.NOT_FOUND);
                }
        	}
        	else {
        		appointmentDetail = appointmentSearch.findAppointmentByID(id);

        		if(appointmentDetail == null) {
            		// 404 resource not found
        			String msg = String.format("No appointment details found for ID: %s", appointmentId.getIdPart());
            		throw OperationOutcomeFactory.buildOperationOutcomeException(new ResourceNotFoundException(msg),
            															   		 SystemCode.REFERENCE_NOT_FOUND,
            															   		 IssueTypeEnum.NOT_FOUND);
        		}
        	}

        	appointment = appointmentDetailToAppointmentResourceConverter(appointmentDetail);
    	}
        catch(NumberFormatException nfe) {
        	// 404 resource not found - the identifier is valid according to FHIR
        	// however we have no entities matching that identifier
    		throw OperationOutcomeFactory.buildOperationOutcomeException(new ResourceNotFoundException("No appointment details found for ID: " + appointmentId.getIdPart()),
			   		 													 SystemCode.REFERENCE_NOT_FOUND,
			   		 													 IssueTypeEnum.NOT_FOUND);
        }

    	return appointment;
    }

    @Search
    public List<Appointment> getAppointmentsForPatientIdAndDates(@RequiredParam(name = "patient") IdDt patientLocalId, @OptionalParam(name = "start") DateRangeParam startDate) {
        Date startLowerDate = null;
        Date startUppderDate = null;

        if (startDate != null) {
            if (startDate.getLowerBound() != null) {
                if (startDate.getLowerBound().getPrefix() == ParamPrefixEnum.GREATERTHAN) {
                    startLowerDate = startDate.getLowerBound().getValue();
                } else {
                    if (startDate.getLowerBound().getPrecision() == TemporalPrecisionEnum.DAY) {
                        startLowerDate = startDate.getLowerBound().getValue(); // Remove a day to make time inclusive of parameter date
                    } else {
                        startLowerDate = new Date(startDate.getLowerBound().getValue().getTime() - 1000); // Remove a second to make time inclusive of parameter date
                    }
                }
            }

            if (startDate.getUpperBound() != null) {
                if (startDate.getUpperBound().getPrefix() == ParamPrefixEnum.LESSTHAN) {
                    startUppderDate = startDate.getUpperBound().getValue();
                } else {
                    if (startDate.getUpperBound().getPrecision() == TemporalPrecisionEnum.DAY) {
                        startUppderDate = new Date(startDate.getUpperBound().getValue().getTime() + 86400000); // Add a day to make time inclusive of parameter date
                    } else {
                        startUppderDate = new Date(startDate.getUpperBound().getValue().getTime() + 1000); // Add a second to make time inclusive of parameter date
                    }
                }
            }
        }

        return appointmentSearch.searchAppointments(patientLocalId.getIdPartAsLong(), startLowerDate, startUppderDate)
                .stream()
                .map(this::appointmentDetailToAppointmentResourceConverter)
                .collect(Collectors.toList());
    }

    @Create
    public MethodOutcome createAppointment(@ResourceParam Appointment appointment) {
        if (appointment.getStatus().isEmpty()) {
            throw new UnprocessableEntityException("No status supplied");
        }

        if (appointment.getStart() == null || appointment.getEnd() == null) {
            throw new UnprocessableEntityException("Both start and end date are required");
        }

        if (appointment.getParticipant().isEmpty()) {
            throw new UnprocessableEntityException("Atleast one participant is required");
        }

        for (Participant participant : appointment.getParticipant()) {
            String resourcePart = participant.getActor().getReference().getResourceType();
            String idPart = participant.getActor().getReference().getIdPart();

            switch (resourcePart) {
                case "Patient":
                    PatientDetails patient = patientSearch.findPatientByInternalID(idPart);
                    if (patient == null) {
                        throw new UnprocessableEntityException("Patient resource reference is not a valid resource");
                    }
                    break;
                case "Practitioner":
                    PractitionerDetails practitioner = practitionerSearch.findPractitionerDetails(idPart);
                    if (practitioner == null) {
                        throw new UnprocessableEntityException("Practitioner resource reference is not a valid resource");
                    }
                    break;
                case "Location":
                    LocationDetails location = locationSearch.findLocationById(idPart);
                    if (location == null) {
                        throw new UnprocessableEntityException("Location resource reference is not a valid resource");
                    }
                    break;
            }
        }

        // Store New Appointment
        AppointmentDetail appointmentDetail = appointmentResourceConverterToAppointmentDetail(appointment);
        List<SlotDetail> slots = new ArrayList<>();

        for (Long slotId : appointmentDetail.getSlotIds()) {
            SlotDetail slotDetail = slotSearch.findSlotByID(slotId);

            if (slotDetail == null) {
                throw new UnprocessableEntityException("Slot resource reference is not a valid resource");
            }

            slots.add(slotDetail);
        }

        appointmentDetail = appointmentStore.saveAppointment(appointmentDetail, slots);

        for (SlotDetail slot : slots) {
            //slot.setAppointmentId(appointmentDetail.getId());
            slot.setFreeBusyType("BUSY");
            slot.setLastUpdated(new Date());
            slotStore.saveSlot(slot);
        }

        // Build response containing the new resource id
        MethodOutcome methodOutcome = new MethodOutcome();
        methodOutcome.setId(new IdDt("Appointment", appointmentDetail.getId()));
        methodOutcome.setResource(appointmentDetailToAppointmentResourceConverter(appointmentDetail));
        methodOutcome.setCreated(Boolean.TRUE);

        return methodOutcome;
    }

    @Update
    public MethodOutcome updateAppointment(@IdParam IdDt appointmentId, @ResourceParam Appointment appointment) {
        MethodOutcome methodOutcome = new MethodOutcome();
        OperationOutcome operationalOutcome = new OperationOutcome();

        AppointmentDetail appointmentDetail = appointmentResourceConverterToAppointmentDetail(appointment);

        // URL ID and Resource ID must be the same
        if (!Objects.equals(appointmentId.getIdPartAsLong(), appointmentDetail.getId())) {
            operationalOutcome.addIssue().setSeverity(IssueSeverityEnum.ERROR).setDetails("Id in URL (" + appointmentId.getIdPart() + ") should match Id in Resource (" + appointmentDetail.getId() + ")");
            methodOutcome.setOperationOutcome(operationalOutcome);
            return methodOutcome;
        }

        // Make sure there is an existing appointment to be amended
        AppointmentDetail oldAppointmentDetail = appointmentSearch.findAppointmentByID(appointmentId.getIdPartAsLong());
        if (oldAppointmentDetail == null) {
            operationalOutcome.addIssue().setSeverity(IssueSeverityEnum.ERROR).setDetails("No appointment details found for ID: " + appointmentId.getIdPart());
            methodOutcome.setOperationOutcome(operationalOutcome);
            return methodOutcome;
        }

        String oldAppointmentVersionId = String.valueOf(oldAppointmentDetail.getLastUpdated().getTime());
        String newAppointmentVersionId = appointmentId.getVersionIdPart();
        if (newAppointmentVersionId != null && !newAppointmentVersionId.equalsIgnoreCase(oldAppointmentVersionId)) {
            throw new ResourceVersionConflictException("The specified version (" + newAppointmentVersionId + ") did not match the current resource version (" + oldAppointmentVersionId + ")");
        }

        //Determin if it is a cancel or an amend
        if (appointmentDetail.getCancellationReason() != null) {
            if (appointmentDetail.getCancellationReason().isEmpty()) {
                operationalOutcome.addIssue().setSeverity(IssueSeverityEnum.ERROR).setDetails("The cancellation reason can not be blank");
                methodOutcome.setOperationOutcome(operationalOutcome);
                return methodOutcome;
            }

            // This is a Cancellation - so copy across fields which can be altered
            oldAppointmentDetail.setCancellationReason(appointmentDetail.getCancellationReason());
            String oldStatus = oldAppointmentDetail.getStatus();
            appointmentDetail = oldAppointmentDetail;
            appointmentDetail.setStatus("cancelled");

            if (!"cancelled".equalsIgnoreCase(oldStatus)) {
                for (Long slotId : appointmentDetail.getSlotIds()) {
                    SlotDetail slotDetail = slotSearch.findSlotByID(slotId);
                    //slotDetail.setAppointmentId(null);
                    slotDetail.setFreeBusyType("FREE");
                    slotDetail.setLastUpdated(new Date());
                    slotStore.saveSlot(slotDetail);
                }
            }
        } else {
            // This is an Amend
            oldAppointmentDetail.setComment(appointmentDetail.getComment());
            oldAppointmentDetail.setReasonCode(appointmentDetail.getReasonCode());
            oldAppointmentDetail.setReasonDisplay(appointmentDetail.getReasonDisplay());
            oldAppointmentDetail.setTypeCode(appointmentDetail.getTypeCode());
            oldAppointmentDetail.setTypeDisplay(appointmentDetail.getTypeDisplay());
            appointmentDetail = oldAppointmentDetail;
        }

        List<SlotDetail> slots = new ArrayList<>();
        for (Long slotId : appointmentDetail.getSlotIds()) {
            SlotDetail slotDetail = slotSearch.findSlotByID(slotId);

            if (slotDetail == null) {
                throw new UnprocessableEntityException("Slot resource reference is not a valid resource");
            }

            slots.add(slotDetail);
        }

        appointmentDetail.setLastUpdated(new Date()); // Update version and lastUpdated timestamp
        appointmentDetail = appointmentStore.saveAppointment(appointmentDetail, slots);

        methodOutcome.setId(new IdDt("Appointment", appointmentDetail.getId()));
        methodOutcome.setResource(appointmentDetailToAppointmentResourceConverter(appointmentDetail));

        return methodOutcome;
    }

    public Appointment appointmentDetailToAppointmentResourceConverter(AppointmentDetail appointmentDetail) {
        Appointment appointment = new Appointment();
        appointment.setId(String.valueOf(appointmentDetail.getId()));
        appointment.getMeta().setLastUpdated(appointmentDetail.getLastUpdated());
        appointment.getMeta().setVersionId(String.valueOf(appointmentDetail.getLastUpdated().getTime()));
        appointment.getMeta().addProfile(SystemURL.SD_GPC_APPOINTMENT);
        appointment.addUndeclaredExtension(false, SystemURL.SD_EXTENSION_GPC_APPOINTMENT_CANCELLATION_REASON, new StringDt(appointmentDetail.getCancellationReason()));
        appointment.setIdentifier(Collections.singletonList(new IdentifierDt(SystemURL.ID_GPC_APPOINTMENT_IDENTIFIER, String.valueOf(appointmentDetail.getId()))));

        switch (appointmentDetail.getStatus().toLowerCase(Locale.UK)) {
            case "pending":
                appointment.setStatus(AppointmentStatusEnum.PENDING);
                break;
            case "booked":
                appointment.setStatus(AppointmentStatusEnum.BOOKED);
                break;
            case "arrived":
                appointment.setStatus(AppointmentStatusEnum.ARRIVED);
                break;
            case "fulfilled":
                appointment.setStatus(AppointmentStatusEnum.FULFILLED);
                break;
            case "cancelled":
                appointment.setStatus(AppointmentStatusEnum.CANCELLED);
                break;
            case "noshow":
                appointment.setStatus(AppointmentStatusEnum.NO_SHOW);
                break;
            default:
                appointment.setStatus(AppointmentStatusEnum.PENDING);
                break;
        }

        CodingDt coding = new CodingDt().setSystem(SystemURL.HL7_VS_C80_PRACTICE_CODES).setCode(String.valueOf(appointmentDetail.getTypeCode())).setDisplay(appointmentDetail.getTypeDisplay());
        CodeableConceptDt codableConcept = new CodeableConceptDt().addCoding(coding);
        codableConcept.setText(appointmentDetail.getTypeDisplay());
        appointment.setType(codableConcept);

        String reasonCode = appointmentDetail.getReasonCode();
		String reasonDisplay = appointmentDetail.getReasonDisplay();
		if(reasonCode != null && reasonDisplay != null) {
			CodingDt codingReason = new CodingDt().setSystem(SystemURL.SNOMED).setCode(String.valueOf(reasonCode)).setDisplay(reasonDisplay);
			CodeableConceptDt codableConceptReason = new CodeableConceptDt().addCoding(codingReason);
			codableConceptReason.setText(reasonDisplay);
			appointment.setReason(codableConceptReason);
		}

        appointment.setStartWithMillisPrecision(appointmentDetail.getStartDateTime());
        appointment.setEndWithMillisPrecision(appointmentDetail.getEndDateTime());

        List<ResourceReferenceDt> slotResources = new ArrayList<>();

        for (Long slotId : appointmentDetail.getSlotIds()) {
            slotResources.add(new ResourceReferenceDt("Slot/" + slotId));
        }

        appointment.setSlot(slotResources);

        appointment.setComment(appointmentDetail.getComment());

        Participant patientParticipant = appointment.addParticipant();
        patientParticipant.setActor(new ResourceReferenceDt("Patient/" + appointmentDetail.getPatientId()));
        patientParticipant.setStatus(ParticipationStatusEnum.ACCEPTED);

        Participant practitionerParticipant = appointment.addParticipant();
        practitionerParticipant.setActor(new ResourceReferenceDt("Practitioner/" + appointmentDetail.getPractitionerId()));
        practitionerParticipant.setStatus(ParticipationStatusEnum.ACCEPTED);

        Participant locationParticipant = appointment.addParticipant();
        locationParticipant.setActor(new ResourceReferenceDt("Location/" + appointmentDetail.getLocationId()));
        locationParticipant.setStatus(ParticipationStatusEnum.ACCEPTED);
        return appointment;
    }

    private Date getLastUpdated(Date lastUpdated) {
    	if(lastUpdated == null) {
    		lastUpdated = new Date();
    	}

    	// trim off milliseconds as we do not store
    	// to this level of granularity
    	Instant instant = lastUpdated.toInstant();
        instant = instant.truncatedTo(ChronoUnit.SECONDS);

        lastUpdated = Date.from(instant);

        return lastUpdated;
    }

    public AppointmentDetail appointmentResourceConverterToAppointmentDetail(Appointment appointment) {
        AppointmentDetail appointmentDetail = new AppointmentDetail();
        appointmentDetail.setId(appointment.getId().getIdPartAsLong());
        appointmentDetail.setLastUpdated(getLastUpdated(appointment.getMeta().getLastUpdated()));

        List<ExtensionDt> extension = appointment.getUndeclaredExtensionsByUrl(SystemURL.SD_EXTENSION_GPC_APPOINTMENT_CANCELLATION_REASON);

        if (extension != null && !extension.isEmpty()) {
            IBaseDatatype value = extension.get(0).getValue();

            if (null == value) {
                throw OperationOutcomeFactory.buildOperationOutcomeException(
                        new InvalidRequestException("Cancellation reason missing."), SystemCode.BAD_REQUEST, IssueTypeEnum.INVALID_CONTENT);
            }

            appointmentDetail.setCancellationReason(value.toString());
        }

        appointmentDetail.setStatus(appointment.getStatus().toLowerCase(Locale.UK));
        appointmentDetail.setTypeDisplay(appointment.getType().getCodingFirstRep().getDisplay());

        CodingDt codingFirstRep = appointment.getReason().getCodingFirstRep();

        // we only support the SNOMED coding system
        if(SystemURL.SNOMED.equals(codingFirstRep.getSystem())) {
	        String reasonCode = codingFirstRep.getCode();
			String reasonDisplay = codingFirstRep.getDisplay();
			if(reasonCode != null) {
	        	if(reasonDisplay != null) {
	        		appointmentDetail.setReasonCode(reasonCode);
	                appointmentDetail.setReasonDisplay(reasonDisplay);
	        	}
	        	else {
	        		// 422 missing data
	        		String msg = String.format("Problem with reason property of the appointment. If the reason is provided then both the code (%s) and display (%s) properties must be set.", reasonCode, reasonDisplay);
	        		throw OperationOutcomeFactory.buildOperationOutcomeException(new UnprocessableEntityException(msg),
	    			   		 													 SystemCode.INVALID_RESOURCE,
	    			   		 													 IssueTypeEnum.REQUIRED_ELEMENT_MISSING);
	        	}
	        }
	        else if(reasonDisplay != null) {
        		// 422 missing data
        		String msg = String.format("Problem with reason property of the appointment. If the reason is provided then both the code (%s) and display (%s) properties must be set.", reasonCode, reasonDisplay);
        		throw OperationOutcomeFactory.buildOperationOutcomeException(new UnprocessableEntityException(msg),
    			   		 													 SystemCode.INVALID_RESOURCE,
    			   		 													 IssueTypeEnum.REQUIRED_ELEMENT_MISSING);
	        }
        }

        appointmentDetail.setStartDateTime(appointment.getStart());
        appointmentDetail.setEndDateTime(appointment.getEnd());

        List<Long> slotIds = new ArrayList<>();

        for (ResourceReferenceDt slotReference : appointment.getSlot()) {
            slotIds.add(slotReference.getReference().getIdPartAsLong());
        }

        appointmentDetail.setSlotIds(slotIds);

        appointmentDetail.setComment(appointment.getComment());

        for (Appointment.Participant participant : appointment.getParticipant()) {
            if (participant.getActor() != null) {
                String participantReference = participant.getActor().getReference().getValue();
                Long actorId = Long.valueOf(participantReference.substring(participantReference.lastIndexOf("/") + 1));
                if (participantReference.contains("Patient/")) {
                    appointmentDetail.setPatientId(actorId);
                } else if (participantReference.contains("Practitioner/")) {
                    appointmentDetail.setPractitionerId(actorId);
                } else if (participantReference.contains("Location/")) {
                    appointmentDetail.setLocationId(actorId);
                }
            }
        }

        return appointmentDetail;
    }
}
