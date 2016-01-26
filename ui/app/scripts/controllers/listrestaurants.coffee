'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:ListrestaurantsCtrl
 # @description
 # # ListrestaurantsCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'ListrestaurantsCtrl', ($scope, $routeParams,$http)->
    $scope.restaurants = []
    catId = $routeParams.catId
    $scope.error = null

    getRestaurants = () ->
      $http.get("/categories/#{catId}")
      .then (resp) ->
        $scope.restaurants = resp.data
      .catch (resp) -> $scope.error = resp.data.message or resp.data

    getRestaurants();
