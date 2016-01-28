'use strict'


 # @ngdoc function
 # @name uiApp.controller:UpdaterestaurantCtrl
 # @description
 # # UpdaterestaurantCtrl
 # Controller of the uiApp
angular.module 'uiApp'
  .controller 'UpdaterestaurantCtrl', ($scope,CommentFactory,$location,$routeParams,$http) ->
    restId = $routeParams.restId
    $scope.categories = []
    $scope.restaurant = {}

    $http.get("/categories")
    .then (response) ->
      $scope.categories = response.data

    $http.get("/restaurants/#{restId}")
    .then (response) ->
      $scope.restaurant = response.data
    $scope.updateRestaurant = ->
      $scope.restaurant.category = parseInt($scope.restaurant.category)
      $http.post("/restaurants/#{restId}", $scope.restaurant)
      .then -> $location.path "/addRestaurant"
      .catch (resp) -> $scope.error = resp.data.message or resp.data