'use strict'

describe 'Controller: UpdatecommentCtrl', ->

# load the controller's module
  beforeEach module 'uiApp'


  scope = {}
  $httpBackend = {}
  $routeParams = {}
  $controller = {}

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

  commId = 1

  # Initialize the controller and a mock scope
  beforeEach inject (_$controller_, $rootScope,_$httpBackend_,_$routeParams_) ->
    scope = $rootScope.$new()
    $httpBackend = _$httpBackend_
    $routeParams = _$routeParams_
    $controller = _$controller_


  it 'should find and update a comment',  ->
    $routeParams.commId = 1
    $controller 'UpdatecommentCtrl', $scope: scope
    $httpBackend.expectGET("/comment/#{commId}").respond 200, comment
    $httpBackend.expectPOST("/comment/#{commId}", scope.comment).respond 200, updateComment
    scope.updateComment()
    $httpBackend.flush()
