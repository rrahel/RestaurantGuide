'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:AddrestaurantCtrl
 # @description
 # # AddrestaurantCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'AddrestaurantCtrl', ($http,$scope,RestaurantFactory,$location,GeoCoder)->
   $scope.restaurant = {}
   $scope.categories = []
   $scope.error = null

   $http.get("/categories")
    .then (response) ->
      $scope.categories = response.data

   $scope.save = ->
     address = $scope.restaurant.zip+" "+$scope.restaurant.city+", "+$scope.restaurant.street
     GeoCoder.geocode(address)
     .then (result) ->
       $scope.restaurant.lat = result.lat()
       $scope.restaurant.lng = result.lng()
       $scope.restaurant.category = parseInt($scope.restaurant.category)
       $http.post("/restaurants", $scope.restaurant)
        .then -> $route.reload()
        .catch (resp) -> $scope.error = resp.data.message or resp.data


   $scope.deleteRestaurant = (id) ->
     $http.delete("/restaurants/#{id}")
     .then -> $route.reload()
     .catch (resp) -> $scope.error2 = resp.data.message or resp.data
