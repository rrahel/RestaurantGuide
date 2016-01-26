'use strict'

###*
 # @ngdoc service
 # @name uiApp.RatingFactory
 # @description
 # # RatingFactory
 # Factory in the uiApp.
###
angular.module 'uiApp'
  .factory 'RatingFactory', ($resource)->
    $resource "/rating/:id",id:"@id",{

    }
