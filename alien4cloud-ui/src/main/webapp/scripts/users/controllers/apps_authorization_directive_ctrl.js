define(function (require) {
  'use strict';
  
  var modules = require('modules');
  var _ = require('lodash');
  
  require('scripts/applications/services/application_environment_services');
  require('scripts/orchestrators/services/location_security_service');
  require('scripts/common/services/search_service_factory');
  require('scripts/common/directives/pagination');
  
  var NewAppAuthorizationController = ['$scope', '$uibModalInstance', 'searchServiceFactory', 'applicationEnvironmentServices',
    function ($scope, $uibModalInstance, searchServiceFactory, applicationEnvironmentServices) {
      $scope.query = '';
      $scope.batchSize = 5;
      $scope.selectedApps = {};
      // a map appId -> environment array
      $scope.environments = {};
      $scope.onSearchCompleted = function (searchResult) {
        $scope.appsData = searchResult.data;
        _.forEach($scope.appsData.data, function(app) {
            if (app.id in $scope.preSelection) {
                $scope.selectedApps[app.id] = $scope.preSelection[app.id];
            }
        });
      };

      $scope.expandEnvironments = function(app, isEnvironmentsCollapsed) {
        if (isEnvironmentsCollapsed) {
          return;
        }
        if (!$scope.environments[app.id]) {
          applicationEnvironmentServices.getAllEnvironments(app.id).then(function(result) {
            var data = result.data.data;
            $scope.environments[app.id] = data;
          });
        }
      };

      $scope.searchService = searchServiceFactory('/rest/latest/applications/search', false, $scope, $scope.batchSize);
      if (_.isUndefined($scope.application)) {
        $scope.editionMode = false;
        $scope.searchService.search();
      } else {
        $scope.editionMode = true;
        $scope.onSearchCompleted({ 'data': { 'data': [$scope.application] }});
        $scope.expandEnvironments($scope.application, false);
      }

      $scope.ok = function () {
        var result = { 'applicationsToDelete': [], 'environmentsToDelete': [], 'applicationsToAdd': [], 'environmentsToAdd': [] };
        _.forEach($scope.selectedApps, function(envs, appId) {
          if (envs.length > 0) {
            _.forEach(envs, function(env) {
              if (!(env in $scope.preSelectedEnvs)) {
                result.environmentsToAdd.push(env);
              }
            });
          } else {
            if (!(appId in $scope.preSelectedApps)) {
              result.applicationsToAdd.push(appId);
            }
          }
        });
        _.forEach($scope.preSelectedApps, function(status, appId) {
          if (status == 0) {
            result.applicationsToDelete.push(appId);
          }
        });
        _.forEach($scope.preSelectedEnvs, function(status, envId) {
          if (status == 0) {
            result.environmentsToDelete.push(envId);
          }
        });

        if (result.applicationsToDelete.length + result.environmentsToDelete.length + result.applicationsToAdd.length + result.environmentsToAdd.length > 0) {
          $uibModalInstance.close(result);
        }
      };

      $scope.searchApp = function () {
        $scope.selectedApps = {};
        $scope.environments = {};
        $scope.searchService.search();
      }

      $scope.toggleApplicationSelection = function (app) {
        if (app.id in $scope.selectedApps) {
          if ($scope.selectedApps[app.id].length == 0) {
            // no env for this app, toggle = no selection
            delete $scope.selectedApps[app.id];
            // app was previoulsy selected (or partially selected)
            if (app.id in $scope.preSelectedApps) {
              $scope.preSelectedApps[app.id] = 0;
            }
          } else {
            // some env exist, toggle = full selection
            $scope.selectedApps[app.id] = [];
            if (app.id in $scope.preSelectedApps) {
              $scope.preSelectedApps[app.id] = 1;
            }
          }
        } else {
          $scope.selectedApps[app.id] = [];
          if (app.id in $scope.preSelectedApps) {
            $scope.preSelectedApps[app.id] = 1;
          }
        }
      };

      /*
         0 : app not selected at all
         1 : partial selection (some env are selected)
         2 : the app itself is selected (means all environments)
      */
      $scope.getApplicationSelectionStatus = function (app) {
        if (app.id in $scope.selectedApps) {
          if ($scope.selectedApps[app.id].length == 0) {
            // no env for this app, full selection
            return 2;
          } else {
            // env selected for this app, partial selection
            return 1;
          }
        } else {
          return 0;
        }
      }

      $scope.toggleEnvironmentSelection = function (app, env) {
        if (app.id in $scope.preSelectedApps) {
          $scope.preSelectedApps[app.id] = 0;
        }
        if (app.id in $scope.selectedApps) {
          var indexOfEnv = $scope.selectedApps[app.id].indexOf(env.id);
          if (indexOfEnv < 0) {
            $scope.selectedApps[app.id].push(env.id);
            if (env.id in $scope.preSelectedEnvs) {
              $scope.preSelectedEnvs[env.id] = 1;
            }
          } else {
            $scope.selectedApps[app.id].splice(indexOfEnv, 1);
            if ($scope.selectedApps[app.id].length == 0) {
                delete $scope.selectedApps[app.id];
            }
            if (env.id in $scope.preSelectedEnvs) {
              $scope.preSelectedEnvs[env.id] = 0;
            }
          }
        } else {
          $scope.selectedApps[app.id] = [ env.id ];
        }
      };

      $scope.isEnvironmentSelected = function (app, env) {
        if (app.id in $scope.selectedApps && $scope.selectedApps[app.id].indexOf(env.id) > -1) {
          return true;
        } else {
          return false;
        }
      }

      $scope.toggleSelectAll = function () {
        var selectAllStatus = $scope.getSelectAllStatus();
        switch(selectAllStatus) {
          case 1:
            _.forEach($scope.appsData.data, function(app) {
              var appSelectionStatus = $scope.getApplicationSelectionStatus(app);
              if (appSelectionStatus == 0) {
                $scope.toggleApplicationSelection(app);
              }
            });
            break;
          case 0:
          case 2:
            _.forEach($scope.appsData.data, function(app) {
              $scope.toggleApplicationSelection(app);
            });
            break;
          case -1:
            // we don't authorize select all when partial selection
            return;
        }
      };

      /*
        -1 : at least 1 app is partially selected (env selected)
         0 : no app selected at all
         1 : partial fully selection (some apps are fully selected, others are not)
         2 : all apps are fully selected
      */
      $scope.getSelectAllStatus = function () {
        var result = 0;
        _.forEach($scope.appsData.data, function(app) {
          if (result == -1) {
            return;
          }
          var appSelectionStatus = $scope.getApplicationSelectionStatus(app);
          if (appSelectionStatus == 1) {
            result = -1;
          } else {
            result += appSelectionStatus;
          }
        });
        switch(result) {
          case -1:
            return -1;
          case 0:
            // no app is selected
            return 0;
          case $scope.appsData.data.length * 2:
            // all apps are fully selected
            return 2;
          default:
            // some app are fully selected, others not
            return 1;
        }
      }

      $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
      };
    }
  ];
  
  modules.get('a4c-security', ['a4c-search']).controller('AppsAuthorizationDirectiveCtrl', ['$scope', '$uibModal', 'locationSecurityService',
    function ($scope, $uibModal, locationSecurityService) {
      $scope.searchAuthorizedEnvironmentsPerApplication = function () {
        locationSecurityService.environmentsPerApplication.get({
          orchestratorId: $scope.orchestrator.id,
          locationId: $scope.location.id
        }, function (response) {
          $scope.authorizedEnvironmentsPerApplication = response.data;
        });
      };
      $scope.searchAuthorizedEnvironmentsPerApplication();
      
      $scope.openNewAppAuthorizationModal = function (app) {
        var childScope = {};
        $scope.application = app;
        $scope.preSelection = {};
        $scope.preSelectedApps = {};
        $scope.preSelectedEnvs = {};
        _.forEach($scope.authorizedEnvironmentsPerApplication, function(authorizedApp) {
          if (_.isEmpty(authorizedApp.environments)) {
            $scope.preSelectedApps[authorizedApp.application.id] = 1;
          }
          $scope.preSelection[authorizedApp.application.id] = [];
          _.forEach(authorizedApp.environments, function(environment) {
            $scope.preSelectedEnvs[environment.id] = 1;
            $scope.preSelection[authorizedApp.application.id].push(environment.id);
          });
        });

        var modalInstance = $uibModal.open({
          templateUrl: 'views/users/apps_authorization_popup.html',
          controller: NewAppAuthorizationController,
          scope: $scope
        });
        
        modalInstance.result.then(function (result) {
          locationSecurityService.environmentsPerApplication.save({
              orchestratorId: $scope.orchestrator.id,
              locationId: $scope.location.id
          }, result, $scope.searchAuthorizedEnvironmentsPerApplication);
        });
      };
      
      $scope.revoke = function (application) {
        // here if application has env or not then do different things
        locationSecurityService.applications.delete({
          orchestratorId: $scope.orchestrator.id,
          locationId: $scope.location.id,
          applicationId: application.application.id
        }, $scope.searchAuthorizedEnvironmentsPerApplication);
      };
    }
  ]);
});