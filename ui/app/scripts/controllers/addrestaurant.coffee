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
   $scope.restaurant = new RestaurantFactory()
   $scope.error = null
   $scope.save = ->
     addr = $scope.restaurant.zip+" "+$scope.restaurant.city+", "+$scope.restaurant.street
     GeoCoder.geocode(addr)
     .then (result) ->
      $scope.restaurant.lat = result.lat()
      $scope.restaurant.lng = result.lng()
     $scope.restaurant.$save()
     .then -> $location.path "/"
     .catch (resp) -> $scope.error = resp.data.message or resp.data