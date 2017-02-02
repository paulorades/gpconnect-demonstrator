'use strict';

angular.module('gpConnect')
  .factory('ClinicalItem', function ($rootScope, $http, FhirEndpointLookup, fhirJWTFactory) {
      
    var findAllHTMLTables = function (patientId) {
      return FhirEndpointLookup.getEndpoint($rootScope.patientOdsCode,"urn:nhs:names:services:gpconnect:fhir:operation:gpc.getcarerecord").then(function (response) {
        var endpointLookupResult = response;
        return $http.post(endpointLookupResult.restUrlPrefix+'/Patient/$gpc.getcarerecord',
              '{"resourceType" : "Parameters","parameter" : [{"name" : "patientNHSNumber","valueIdentifier" : { "system": "http://fhir.nhs.net/Id/nhs-number", "value" : "'+patientId+'" }},{"name" : "recordSection","valueCodeableConcept" :{"coding" : [{"system":"http://fhir.nhs.net/ValueSet/gpconnect-record-section-1","code":"CLI","display":"ClinicalItems"}]}},{"name" : "timePeriod","valuePeriod" : { "start" : "2015", "end" : "2016" }}]}',
              {
                  headers: {
                      'Ssp-From': endpointLookupResult.fromASID,
                      'Ssp-To': endpointLookupResult.toASID,
                      'Ssp-InteractionID': "urn:nhs:names:services:gpconnect:fhir:operation:gpc.getcarerecord",
                      'Ssp-TraceID': fhirJWTFactory.guid(),
                      'Authorization': "Bearer " + fhirJWTFactory.getJWT("patient", "read", patientId)
                  }
              }
              );
        });
    };

    return {
      findAllHTMLTables: findAllHTMLTables
    };

  });
