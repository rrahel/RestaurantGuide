'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:ProfileCtrl
 # @description
 # # ProfileCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'ProfileCtrl',($scope, $routeParams, $http ,$rootScope, $location) ->
    $scope.user = {}
    $scope.restaurants = []
    $scope.error = null

    getUser = () ->
      $http.get("/whoami")
      .then (resp) ->
        $scope.user = resp.data
      .catch (resp) -> $scope.error = resp.data.message or resp.data

    getUser();

    getRestaurants = () ->
      $http.get("/rating/favorites")
      .then (resp) ->
        $scope.restaurants = resp.data
      .catch (resp) -> $scope.error = resp.data.message or resp.data

    getRestaurants();

    $scope.update = () ->
      updatedUser={
        id: $scope.user.id
        firstname: $scope.user.firstname
        lastname: $scope.user.lastname
        email: $scope.user.email
        providerID: $scope.user.providerID
        providerKey: $scope.user.providerKey
        roles: $scope.user.roles
      }
      $http.post("/user/update", updatedUser)
 #     .then -> $location.path "/"
 #     .then (resp)-> $rootScope.user = resp.data
      .catch (resp) -> $scope.error = resp.data.message or resp.data
