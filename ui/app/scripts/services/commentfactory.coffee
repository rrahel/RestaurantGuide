'use strict'

###*
 # @ngdoc service
 # @name uiApp.CommentFactory
 # @description
 # # CommentFactory
 # Factory in the uiApp.
###
angular.module 'uiApp'
  .factory 'CommentFactory', ($resource,$http,$rootScope)->
    $resource "/comment/:id",id:"@id",{
      admin:
        method: 'DELETE'
        params:
          admin: 'admin'
    }

    class Comment
      comments = []
      ownComment: (commentId) ->
        commentWithId = this.comments.map (i) -> i.id
        commentId in commentWithId

    myComments = new Comment()
    $rootScope.$on 'userChanged', () ->
      $http.get("/comments")
       .then (response) ->
        myComments.comments = response.data

    myComments