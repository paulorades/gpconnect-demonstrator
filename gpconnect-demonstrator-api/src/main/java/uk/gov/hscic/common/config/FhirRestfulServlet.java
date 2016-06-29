package uk.gov.hscic.common.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.WebApplicationContextUtils;

import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import uk.gov.hscic.appointments.ScheduleResourceProvider;
import uk.gov.hscic.location.LocationResourceProvider;
import uk.gov.hscic.medications.MedicationAdministrationResourceProvider;
import uk.gov.hscic.medications.MedicationDispenseResourceProvider;
import uk.gov.hscic.medications.MedicationOrderResourceProvider;
import uk.gov.hscic.medications.MedicationResourceProvider;
import uk.gov.hscic.organization.OrganizationResourceProvider;
import uk.gov.hscic.patient.PatientResourceProvider;
import uk.gov.hscic.practitioner.PractitionerResourceProvider;

@Configuration
@WebServlet(urlPatterns={"/fhir/*"}, displayName="FHIR Server")
public class FhirRestfulServlet extends RestfulServer {

    private static final long serialVersionUID = 1L;

    @Override
    protected void initialize() throws ServletException {
        List<IResourceProvider> resourceProviders = new ArrayList<>();
        
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());

        resourceProviders.add(organizationResourceProvider(applicationContext));
        resourceProviders.add(practitionerResourceProvider(applicationContext));
        resourceProviders.add(patientResourceProvider(applicationContext));
        resourceProviders.add(medicationResourceProvider(applicationContext));
        resourceProviders.add(medicationOrderResourceProvider(applicationContext));
        resourceProviders.add(medicationDispenseResourceProvider(applicationContext));
        resourceProviders.add(medicationAdministrationResourceProvider(applicationContext));
        resourceProviders.add(scheduleResourceProvider(applicationContext));
        resourceProviders.add(locationResourceProvider(applicationContext));

        setResourceProviders(resourceProviders);
    }

    @Bean(name = "organizationResourceProvider")
    public OrganizationResourceProvider organizationResourceProvider(ApplicationContext applicationContext) {
        return new OrganizationResourceProvider(applicationContext);
    }

    @Bean(name = "practitionerResourceProvider")
    public PractitionerResourceProvider practitionerResourceProvider(ApplicationContext applicationContext) {
        return new PractitionerResourceProvider(applicationContext);
    }

    @Bean(name = "patientResourceProvider")
    public PatientResourceProvider patientResourceProvider(ApplicationContext applicationContext) {
        return new PatientResourceProvider(applicationContext);
    }
    
    @Bean(name = "medicationResourceProvider")
    public MedicationResourceProvider medicationResourceProvider(ApplicationContext applicationContext) {
        return new MedicationResourceProvider(applicationContext);
    }
    
    @Bean(name = "medicationOrderResourceProvider")
    public MedicationOrderResourceProvider medicationOrderResourceProvider(ApplicationContext applicationContext) {
        return new MedicationOrderResourceProvider(applicationContext);
    }
    
    @Bean(name = "medicationDispenseResourceProvider")
    public MedicationDispenseResourceProvider medicationDispenseResourceProvider(ApplicationContext applicationContext) {
        return new MedicationDispenseResourceProvider(applicationContext);
    }
    
    @Bean(name = "medicationAdministrationResourceProvider")
    public MedicationAdministrationResourceProvider medicationAdministrationResourceProvider(ApplicationContext applicationContext) {
        return new MedicationAdministrationResourceProvider(applicationContext);
    }
    
    @Bean(name = "scheduleResourceProvider")
    public ScheduleResourceProvider scheduleResourceProvider(ApplicationContext applicationContext) {
        return new ScheduleResourceProvider(applicationContext);
    }
    
    @Bean(name = "locationResourceProvider")
    public LocationResourceProvider locationResourceProvider(ApplicationContext applicationContext) {
        return new LocationResourceProvider(applicationContext);
    }    
}
