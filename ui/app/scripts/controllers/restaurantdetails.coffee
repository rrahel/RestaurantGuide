'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:RestaurantdetailsCtrl
 # @description
 # # RestaurantdetailsCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'RestaurantdetailsCtrl', ($scope, $routeParams, RestaurantFactory, $http) ->
    restId = $routeParams.restId
    $scope.restaurant = {}
    $http.get("restaurants/#{restId}")
    .then (resp) ->
      $scope.restaurant = resp.data



