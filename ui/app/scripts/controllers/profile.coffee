'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:ProfileCtrl
 # @description
 # # ProfileCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'ProfileCtrl',($scope, $routeParams, $http) ->
    $scope.user

    getUser = () ->
      $http.get("/whoami")
      .then (resp) ->
        $scope.user = resp.data
      .catch (resp) -> $scope.error = resp.data.message or resp.data

    getUser();

    update = () ->
      $scope.user.firstname = $scope.firstnameUpdate
      $scope.user.lastname = $scope.lastnameUpdate
      $http.post("/user/update", $scope.user)
