'use strict'

###*
 # @ngdoc service
 # @name uiApp.RestaurantFactory
 # @description
 # # RestaurantFactory
 # Factory in the uiApp.
###
angular.module 'uiApp'
  .factory 'RestaurantFactory', ($resource) ->
    $resource '/restaurants/:id', id: "@id", {

    }
