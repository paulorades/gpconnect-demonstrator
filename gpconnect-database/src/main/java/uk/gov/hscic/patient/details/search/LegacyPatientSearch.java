/*
 * Copyright 2015 Ripple OSI
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package uk.gov.hscic.patient.details.search;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.mysema.query.BooleanBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import uk.gov.hscic.common.service.AbstractLegacyService;
import uk.gov.hscic.patient.details.model.PatientEntity;
import uk.gov.hscic.patient.details.model.QPatientEntity;
import uk.gov.hscic.patient.details.repo.PatientRepository;
import uk.gov.hscic.patient.summary.model.PatientDetails;
import uk.gov.hscic.patient.summary.model.PatientQueryParams;
import uk.gov.hscic.patient.summary.model.PatientSummary;
import uk.gov.hscic.patient.summary.search.PatientSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LegacyPatientSearch extends AbstractLegacyService implements PatientSearch {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientEntityToDetailsTransformer patientEntityToDetailsTransformer;

    @Autowired
    private PatientEntityToSummaryTransformer patientEntityToSummaryTransformer;

    @Override
    public List<PatientSummary> findAllPatients() {
        Sort sort = new Sort("nhsNumber");
        Iterable<PatientEntity> patients = patientRepository.findAll(sort);
        return CollectionUtils.collect(patients, patientEntityToSummaryTransformer, new ArrayList<>());
    }

    @Override
    public PatientDetails findPatient(String patientId) {
        PatientEntity patient = patientRepository.findByNhsNumber(patientId);
        return patientEntityToDetailsTransformer.transform(patient);
    }

    @Override
    public PatientSummary findPatientSummary(String patientId) {
        PatientEntity patient = patientRepository.findByNhsNumber(patientId);
        return patientEntityToSummaryTransformer.transform(patient);
    }

    @Override
    public List<PatientSummary> findPatientsByQueryObject(PatientQueryParams params) {
        BooleanBuilder predicate = generateAdvancedSearchPredicate(params);

        Iterable<PatientEntity> patients = patientRepository.findAll(predicate);
        return CollectionUtils.collect(patients, patientEntityToSummaryTransformer, new ArrayList<>());
    }

    private BooleanBuilder generateAdvancedSearchPredicate(PatientQueryParams params) {
        QPatientEntity blueprint = QPatientEntity.patientEntity;
        BooleanBuilder predicate = new BooleanBuilder();

        String nhsNumber = params.getNhsNumber();

        if (nhsNumber != null) {
            predicate.and(blueprint.nhsNumber.eq(nhsNumber));
        }
        else {
            String surname = StringUtils.stripToNull(params.getSurname());
            String forename = StringUtils.stripToNull(params.getForename());
            Date dateOfBirth = params.getDateOfBirth();
            String gender = StringUtils.stripToNull(params.getGender());

            if (surname != null) {
                predicate.and(blueprint.lastName.like(surname));
            }
            if (forename != null) {
                predicate.and(blueprint.firstName.like(forename));
            }
            if (dateOfBirth != null) {
                Date truncatedDateOfBirth = DateUtils.truncate(dateOfBirth, Calendar.DATE);

                predicate.and(blueprint.dateOfBirth.eq(truncatedDateOfBirth));
            }
            if (gender != null) {
                predicate.and(blueprint.gender.eq(gender));
            }
        }

        return predicate;
    }
}
