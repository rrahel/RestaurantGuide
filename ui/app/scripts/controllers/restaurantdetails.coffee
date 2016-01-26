'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:RestaurantdetailsCtrl
 # @description
 # # RestaurantdetailsCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'RestaurantdetailsCtrl', ($scope, $routeParams, RestaurantFactory, RatingFactory, $http, $route) ->
    restId = $routeParams.restId
    $scope.restaurant = {}
    $http.get("restaurants/#{restId}")
    .then (resp) ->
      $scope.restaurant = resp.data



    $scope.rate = ->
      $scope.rating = {}
      $scope.rating.rating = parseFloat($scope.newRate)
      $scope.rating.userId = 1
      $scope.rating.restaurantId = $scope.restaurant.id
      $http.post("/rating", $scope.rating)
       .then -> $route.reload()
       .catch (resp) -> $scope.error = resp.data.message or resp.data


