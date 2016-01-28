'use strict'

###*
 # @ngdoc service
 # @name uiApp.CommentsFactory
 # @description
 # # CommentsFactory
 # Factory in the uiApp.
###
angular.module 'uiApp'
  .factory 'CommentsFactory', ($resource)->
    $resource "/comments/:id",id:"@id",{

    }
