'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:AddrestaurantCtrl
 # @description
 # # AddrestaurantCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'AddrestaurantCtrl', ($scope,RestaurantFactory,$location)->
    $scope.restaurant = new RestaurantFactory()
    $scope.error = null
    $scope.savedRestaurant = {}
    $scope.save = ->
      $scope.savedRestaurant = $scope.restaurant.$save()
      .then -> $location.path "/"
      .catch (resp) -> $scope.error = resp.data.message or resp.data
