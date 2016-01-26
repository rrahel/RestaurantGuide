'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:SigninCtrl
 # @description
 # # SigninCtrl
 # Controller of the uiApp
###
angular.module('uiApp')
  .controller 'SigninCtrl', ($scope,$auth,$rootScope,$alert,UserFactory) ->
    $scope.signInInfo = {}
    $scope.signIn = ->
      $auth.setStorage($scope.signInInfo.rememberMe ? 'localStorage' : 'sessionStorage');
      $auth.login( email: $scope.signInInfo.email, password: $scope.signInInfo.password, rememberMe: $scope.signInInfo.rememberMe )
      .then ->
          UserFactory.get()
      .then (response) ->
        $rootScope.user = response.data
        $rootScope.$broadcast "userChanged"
        $alert({
          titel: "Success"
          content: 'You have successfully signed in'
          effect: 'fade-in'
          speed: 'normal'
          alertType:'success'
          duration: 10000})
      .catch (response) ->
        console.log(response)
        $alert({
        content: 'There went something wrong! Please check your email address and password!'
        effect: 'fade-in'
        speed: 'normal'
        alertType:'danger'
        duration: 10000})
