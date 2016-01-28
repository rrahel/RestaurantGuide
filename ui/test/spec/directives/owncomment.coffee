###
'use strict'

describe 'Directive: ownComment', ->

# load the directive's module
  beforeEach ->
    module 'uiApp'

  scope = {}
  CommentFactory = {}
  $rootScope = {}

  beforeEach inject ($controller, _$rootScope_, _CommentFactory_) ->
    $rootScope = _$rootScope_
    scope = $rootScope.$new()
    CommentFactory = _CommentFactory_


  it 'should hide the element if the current user is not the owner', inject ($compile) ->
    element = angular.element '<div comment-factory="{{comment.id}}"></div>'
    scope.comment = id:4
    CommentFactory.expectQuery(4).respond no
    element = $compile(element) scope
    CommentFactory.flush()
    expect(element.css('display')).toBe 'none'

  it 'should make the element visible if the current user is the owner', inject ($compile) ->
    element = angular.element '<div comment-factory="{{comment.id}}"></div>'
    scope.comment = id:4
    CommentFactory.expectQuery(4).respond yes
    element = $compile(element) scope
    CommentFactory.flush()
    expect(element.css('display')).not.toBe 'none'

###
