'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:AddcategoryCtrl
 # @description
 # # AddcategoryCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'AddcategoryCtrl', ($scope,CategoryFactory,$route,$http) ->
   $scope.categories = CategoryFactory.query()
   $scope.category = new CategoryFactory()
   $scope.save = ->
     $scope.category.$save()
     .then -> $route.reload()
     .catch (resp) -> $scope.error = resp.data.message or resp.data

   $scope.deleteCategory = (id) ->
     $http.delete("/categories/#{id}")
     .then -> $route.reload()
     .catch (resp) -> $scope.error2 = "Category could not be deleted! Please, check if some restaurants refer to it!"