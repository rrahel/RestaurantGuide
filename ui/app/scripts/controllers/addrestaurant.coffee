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
   $scope.error = null
   $scope.save = ->
     address = $scope.restaurant.zip+" "+$scope.restaurant.city+", "+$scope.restaurant.street
     GeoCoder.geocode(address)
     .then (result) ->
       $scope.restaurant.lat = result.lat()
       $scope.restaurant.lng = result.lng()
       $http.post("/restaurants", $scope.restaurant)
        .then -> $location.path "/"
        .catch (resp) -> $scope.error = resp.data.message or resp.data