'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:AddcategoryCtrl
 # @description
 # # AddcategoryCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'AddcategoryCtrl', ($scope,CategoryFactory,$location) ->
   $scope.category = new CategoryFactory()
   $scope.save = ->
     $scope.category.$save()
     .then -> $location.path "/"
     .catch (resp) -> $scope.error = resp.data.message or resp.data
