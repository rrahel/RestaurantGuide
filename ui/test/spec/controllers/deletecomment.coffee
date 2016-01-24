'use strict'

describe 'Controller: DeletecommentCtrl', ->

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
    $controller 'DeletecommentCtrl', $scope: scope

  it 'should delete a comment and go to the start page if successful', ->
    scope.comment = angular.extend scope.comment,comment
    $routeParams.commentId = 1
    $location.path "/deleteComment"
    $httpBackend.expectDELETE("/comment/1").respond 200
    scope.delete(comment.id)
    $httpBackend.flush()
    expect($location.path()).toBe "/"


  it "should provide an error message if delete fails", ->
    scope.comment = angular.extend scope.comment,comment
    $routeParams.commentId = 1
    $location.path "/deleteComment"
    $httpBackend.expectDELETE("/comment/1").respond 500,message: "An Error occured!"
    scope.delete(comment.id)
    $httpBackend.flush()
    expect($location.path()).toBe "/deleteComment"
    expect(scope.error).toBe "An Error occured!"