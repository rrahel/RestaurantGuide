'use strict'


 # @ngdoc function
 # @name uiApp.controller:UpdaterestaurantCtrl
 # @description
 # # UpdaterestaurantCtrl
 # Controller of the uiApp
angular.module 'uiApp'
  .controller 'UpdaterestaurantCtrl', ($scope,CommentFactory,$location,$routeParams,$http,GeoCoder) ->
    restId = $routeParams.restId
    $scope.categories = []
    $scope.restaurant = {}
    $scope.error = null

    $http.get("/categories")
    .then (response) ->
      $scope.categories = response.data

    $http.get("/restaurants/#{restId}")
    .then (response) ->
      $scope.restaurant = response.data
    $scope.updateRestaurant = ->
      address = $scope.restaurant.zip+" "+$scope.restaurant.city+", "+$scope.restaurant.street
      GeoCoder.geocode(address)
      .then (result) ->
        $scope.restaurant.lat = result.lat()
        $scope.restaurant.lng = result.lng()
        $scope.restaurant.category = parseInt($scope.restaurant.category)
        $http.post("/restaurants/#{restId}", $scope.restaurant)
         .then -> $location.path "/addRestaurant"
         .catch (resp) -> $scope.error = resp.data.message or resp.data
