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
    members:
      method: 'GET'
      isArray: true
      params:
        members: 'members'
    join:
      method: 'POST'
      params:
        members: 'join'
    leave:
      method: 'DELETE'
      params:
        members: 'join'
  }
