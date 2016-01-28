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
    $scope.user = {}

    getUser = () ->
      $http.get("/whoami")
      .then (resp) ->
        $scope.user = resp.data
      .catch (resp) -> $scope.error = resp.data.message or resp.data

    getUser();

    $scope.update = () ->
      updatedUser={
        id: $scope.user.id
        firstname: $scope.user.firstname
        lastname: $scope.user.lastname
        email: $scope.user.email
      }
      $http.post("/user/update", updatedUser)
