'use strict';

var NewApplicationEnvironmentCtrl = ['$scope', '$modalInstance', '$resource', '$state',
  function($scope, $modalInstance, $resource, $state) {
    $scope.environment = {};

    $scope.create = function(valid, cloudId, envType, version) {
      if (valid) {
        // prepare the good request
        var applicationId = $state.params.id;
        $scope.environment.cloudId = cloudId;
        $scope.environment.applicationId = applicationId;
        $scope.environment.environmentType = envType;
        $scope.environment.versionId = version;
        $modalInstance.close($scope.environment);
      }
    };

    $scope.cancel = function() {
      $modalInstance.dismiss('cancel');
    };
  }
];

angular.module('alienUiApp').controller('ApplicationEnvironmentsCtrl', ['$scope', '$state', '$translate', 'toaster', 'alienAuthService', '$modal', 'applicationEnvironmentServices', '$rootScope', '$resolve', 'applicationVersionServices', 'searchServiceFactory', 'appEnvironments',
  function($scope, $state, $translate, toaster, alienAuthService, $modal, applicationEnvironmentServices, $rootScope, $resolve, applicationVersionServices, searchServiceFactory, appEnvironments) {

    $scope.isManager = alienAuthService.hasRole('APPLICATIONS_MANAGER');
    $scope.envTypeList = applicationEnvironmentServices.environmentTypeList({}, {}, function() {});

    // Application versions search
    var searchVersions = function() {
      var searchRequestObject = {
        'query': '',
        'from': 0,
        'size': 50
      };
      applicationVersionServices.searchVersion({
        applicationId: $state.params.id
      }, angular.toJson(searchRequestObject), function versionSearchResult(result) {
        $scope.versions = result.data.data;
      });

    };
    searchVersions();

    // Cloud search
    $scope.onSearchCompleted = function(searchResult) {
      $scope.clouds = searchResult.data.data;
    };
    $scope.searchService = searchServiceFactory('rest/clouds/search', true, $scope, 50);
    $scope.searchClouds = function() {
      $scope.searchService.search();
    };
    $scope.searchClouds();

    // Modal to create an new application environment
    $scope.openNewAppEnv = function() {
      var modalInstance = $modal.open({
        templateUrl: 'newApplicationEnvironment.html',
        controller: NewApplicationEnvironmentCtrl,
        scope: $scope
      });
      modalInstance.result.then(function(environment) {
        applicationEnvironmentServices.create({
          applicationId: $scope.application.id
        }, angular.toJson(environment), function(successResponse) {
          $scope.search().then(function(searchResult){
            var environments = searchResult.data.data;
            var pushed = false;
            for(var i=0; i < environments.length && !pushed; i++) {
              if(environments[i].id === successResponse.data) {
                appEnvironments.addEnvironment(environments[i]);
                pushed = true;
              }
            }
          });
        });
      });
    };

    // Search for application environments
    $scope.search = function() {
      var searchRequestObject = {
        'query': $scope.query,
        'from': 0,
        'size': 50
      };
      return applicationEnvironmentServices.searchEnvironment({
        applicationId: $scope.application.id
      }, angular.toJson(searchRequestObject), function updateAppEnvSearchResult(result) {
        $scope.searchAppEnvResult = result.data.data;
        return $scope.searchAppEnvResult;
      }).$promise;
    };
    $scope.search();

    // Delete the app environment
    $scope.delete = function deleteAppEnvironment(appEnvId) {
      if (!angular.isUndefined(appEnvId)) {
        applicationEnvironmentServices.delete({
          applicationId: $scope.application.id,
          applicationEnvironmentId: appEnvId
        }, null, function deleteAppEnvironment(result) {
          if(result.data) {
            appEnvironments.removeEnvironment(appEnvId);
          }
          $scope.search();
        });
      }
    };

    var getVersionIdByName = function(name) {
      for (var i = 0; i < $scope.versions.length; i++) {
        if ($scope.versions[i].version === name) {
          return $scope.versions[i].id;
        }
      }
    };

    var getCloudIdByName = function(name) {
      for (var i = 0; i < $scope.clouds.length; i++) {
        if ($scope.clouds[i].name === name) {
          return $scope.clouds[i].id;
        }
      }
    };

    function updateEnvironment(environmentId, fieldName, fieldValue) {
      // update the environments
      var done = false;
      for(var i=0; i < $scope.searchAppEnvResult.length && !done; i++) {
        var environment = $scope.searchAppEnvResult[i];
        if(environment.id === environmentId) {
          environment[fieldName] = fieldValue;
          appEnvironments.updateEnvironment(environment);
          done = true;
        }
      }
    }

    $scope.updateApplicationEnvironment = function(fieldName, fieldValue, environmentId, oldValue) {
      if (fieldName !== 'name' || fieldValue !== oldValue) {
        var updateApplicationEnvironmentRequest = {};

        var realFieldValue = fieldValue;
        if (fieldName === 'currentVersionId') {
          realFieldValue = getVersionIdByName(fieldValue);
        } else if (fieldName === 'cloudId') {
          realFieldValue = getCloudIdByName(fieldValue);
        }
        updateApplicationEnvironmentRequest[fieldName] = realFieldValue;

        return applicationEnvironmentServices.update({
          applicationId: $scope.application.id,
          applicationEnvironmentId: environmentId
        }, angular.toJson(updateApplicationEnvironmentRequest)).$promise.then(function() {
          updateEnvironment(environmentId, fieldName, realFieldValue);
        }, function(errorResponse) {
          return $translate('ERRORS.' + errorResponse.data.error.code);
        });
      }
    };
  }
]);
