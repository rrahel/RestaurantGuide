'use strict'

###*
 # @ngdoc service
 # @name uiApp.CategoryFactory
 # @description
 # # CategoryFactory
 # Factory in the uiApp.
###
angular.module 'uiApp'
  .factory 'CategoryFactory', ($resource)->
    $resource "/categories/:id",id:"@id",{

    }
