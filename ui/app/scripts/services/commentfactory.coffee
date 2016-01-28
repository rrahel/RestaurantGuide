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
      commentSeq: []
      ownComment: (commentId) ->
        commentWithId = this.commentSeq.map (i) -> i.id
        commentId in commentWithId

    myComments = new Comment()
    $rootScope.$on 'userChanged', () ->
      $http.get("/comments")
       .then (response) ->
        myComments.commentSeq = response.data

    myComments