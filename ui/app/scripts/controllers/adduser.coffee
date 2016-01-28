'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:AdduserCtrl
 # @description
 # # AdduserCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'AdduserCtrl', ($scope,User,$route,$http) ->
    $scope.users = User.query()
    $scope.user = new User()
    $scope.save = ->
      $scope.user.$save()
      .then -> $route.reload()
      .catch (resp) -> $scope.error = resp.data.message or resp.data

    $scope.deleteUser = (id) ->
      $http.delete("/user/#{id}")
      .then -> $route.reload()
      .catch (resp) -> $scope.error2 = resp.data.message or resp.data