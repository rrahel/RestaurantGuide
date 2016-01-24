'use strict'

###*
 # @ngdoc service
 # @name uiApp.CommentFactory
 # @description
 # # CommentFactory
 # Factory in the uiApp.
###
angular.module 'uiApp'
  .factory 'CommentFactory', ($resource)->
    $resource "/comment/:id",id:"@id",{
      admin:
        method: 'DELETE'
        params:
          admin: 'admin'
    }
