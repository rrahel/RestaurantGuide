'use strict'

describe 'Controller: UpdatecommentCtrl', ->

# load the controller's module
  beforeEach module 'uiApp'


  scope = {}
  $httpBackend = {}
  $routeParams = {}

  comment = {
    id: 1
    content: "Best Restaurant"
    userId: 1
    restaurantId: 1
  }

  updateComment = {
    id: 1
    content: "Good Restaurant"
    userId: 1
    restaurantId: 1
  }

  commId = undefined

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope,_$httpBackend_,_$routeParams_) ->
    scope = $rootScope.$new()
    $httpBackend = _$httpBackend_
    $routeParams = _$routeParams_
    $controller 'UpdatecommentCtrl', $scope: scope

  it 'should find and update a comment',  ->
    expect(scope.comment).toEqual {}
    $routeParams.commId = 1
    commId = $routeParams.commId
    $httpBackend.expectGET("/comment/#{commId}").respond 200, comment
    scope.update
    $httpBackend.expectPOST("/comment/#{commId}", updateComment).respond 200
    $httpBackend.flush()
    expect(scope.comment.content).toBe "Good Restaurant"
