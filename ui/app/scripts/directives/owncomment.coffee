'use strict'

###*
 # @ngdoc directive
 # @name uiApp.directive:ownComment
 # @description
 # # ownComment
###
angular.module 'uiApp'
  .directive 'ownComment', (CommentFactory) ->
    restrict: 'AC'
    link: ($scope, element, attrs) ->
      updateVisibility = ->
        element.css('display', if CommentFactory.ownComment parseInt attrs.ownComment then '' else 'none');
      $scope.$on "userChanged", updateVisibility
      attrs.$observe 'ownComment' , updateVisibility
      updateVisibility()
