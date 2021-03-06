'use strict';

angular.module('gpConnect')
  .controller('AppointmentsSearchModalCtrl', function ($scope, $modalInstance, $modal, $timeout, modal, ProviderRouting) {

    $scope.modal = modal;
    $scope.appointmentSearch = {};
    
    $scope.appointmentSearch.primaryPractice = ProviderRouting.defaultPractice();
    $scope.appointmentSearch.primaryPractice.checked = true;
    $scope.appointmentSearch.federatedPractices = [];
    $.each(ProviderRouting.practices, function (index, practice) {
        if (practice.odsCode != $scope.appointmentSearch.primaryPractice.odsCode) {
            practice.checked = false;
            $scope.appointmentSearch.federatedPractices.push(practice);
        }
    });
    
    var highlightMonth = function(){
        $(".text-muted").parent().addClass("otherMonth");
        $("button").unbind('click', highlightMonth);
        $("button").bind('click', highlightMonth);
    };
    
    $scope.openDatePicker = function ($event, name) {
      $event.preventDefault();
      $event.stopPropagation();
      $scope.startDate = false;
      $scope.endDate = false;
      $scope[name] = true;
      highlightMonth();
    };
    
    $scope.initaliseDates = function() {
        $scope.minStartDate = new Date();
        $scope.endDateInvalid = false;
    };
    $scope.initaliseDates();
        
    $scope.onStartDate = function() {
    	var startDate = $scope.appointmentSearch.startDate
    	$scope.minEndDate = startDate;

    	var maxEndDateMoment = moment(startDate).add(2, 'weeks')
    	$scope.maxEndDate = maxEndDateMoment.toDate();
    	
    	var endDate = $scope.appointmentSearch.endDate;
    	if(endDate) {
    		// does the end date need resetting?
    		if(moment(maxEndDateMoment).isBefore(endDate) || moment(startDate).isAfter(endDate)) {
    			$scope.appointmentSearch.endDate = null;
    			$scope.endDateInvalid = true;
    		}
    	}
    };
    
    $scope.onEndDate = function() {
    	var endDate = $scope.appointmentSearch.endDate;

    	if(endDate) {
    		var maxEndDate = moment($scope.maxEndDate)
    		
    		if(maxEndDate.isSame(endDate) || maxEndDate.isAfter(endDate)) {
    			$scope.endDateInvalid = false;
    		}
    	}
    };
  
    $scope.$watch("appointmentSearch.startDate", function(newValue, oldValue) {
        if($scope.appointmentSearch !== undefined) {
        	$scope.onStartDate();
        }
    }); 
    
    $scope.$watch("appointmentSearch.endDate", function(newValue, oldValue) {
        if($scope.appointmentSearch !== undefined) {
        	$scope.onEndDate();
        }
    });      

    $scope.ok = function (appointmentSearchForm) {
      $scope.formSubmitted = true;
      var appointmentSearch = $scope.appointmentSearch;

      if (appointmentSearchForm.$valid) {
       
    	  $modalInstance.close();
    	  
    	  $modal.open({
          templateUrl: 'views/appointments/appointments-slots-modal.html',
          size: 'lg',
          controller: 'AppointmentsSlotsCtrl',
          resolve: {
            modal: function () {
              return {
                title: 'Available Appointments'
              };
            },
            appointmentSearchParams: function () {
              return appointmentSearch;
            }
          }
        });
      }
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };

  });
