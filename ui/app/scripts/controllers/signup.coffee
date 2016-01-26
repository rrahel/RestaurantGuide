'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:SignupctrlCtrl
 # @description
 # # SignupctrlCtrl
 # Controller of the uiApp
###
angular.module('uiApp')
  .controller 'SignUpCtrl', ($scope,$http,$rootScope,$auth,$alert) ->
    $scope.error = null
    $scope.signUpInfo = {}

    $scope.signUp = ->
      $auth.signup($scope.signUpInfo)
      .then ->
        $http.get('/whoami')
      .then (response) ->
        $rootScope.user = response.data
        $rootScope.$broadcast "userChanged"
        $alert("Welcome #{$rootScope.user.firstname}!","Success!", 'success', 'top-left')
      .catch (err) ->
        $scope.error = if err.data? then err.data.message else err
        $alert("There went something wrong!","Error!", 'danger', 'top-left')

