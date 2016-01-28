###
'use strict'

describe 'Controller: UpdatecommentCtrl', ->

# load the controller's module
  beforeEach module 'uiApp'


  scope = {}
  $httpBackend = {}
  $location = {}
  $routeParams = {}

  comment =
    id: 1
    content: "testComment"
    userId: 1
    restaurantId: 1

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope,_$httpBackend_,_$location_,_$routeParams_) ->
    scope = $rootScope.$new()
    $location = _$location_
    $httpBackend = _$httpBackend_
    $routeParams = _$routeParams_
    $controller 'UpdatecommentCtrl', $scope: scope

  it 'should delete a comment and go to the start page if successful', ->
    scope.comment = angular.extend scope.comment,comment
    $routeParams.commentId = 1
    $location.path "/updateComment"
    $httpBackend.expectPOST("/comment/1",comment).respond 200
    scope.update(comment.id)
    $httpBackend.flush()
    expect($location.path()).toBe "/"


  it "should provide an error message if delete fails", ->
    scope.comment = angular.extend scope.comment,comment
    $routeParams.commentId = 1
    $location.path "/updateComment"
    $httpBackend.expectPOST("/comment/1",comment).respond 500,message: "An Error occured!"
    scope.update(comment.id)
    $httpBackend.flush()
    expect($location.path()).toBe "/updateComment"
    expect(scope.error).toBe "An Error occured!"
###
