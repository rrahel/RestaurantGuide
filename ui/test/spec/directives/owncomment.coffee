###
'use strict'

describe 'Directive: ownComment', ->

  # load the directive's module
  beforeEach module 'uiApp'

  scope = {}

  beforeEach inject ($controller, $rootScope) ->
    scope = $rootScope.$new()

  it 'should make hidden element visible', inject ($compile) ->
    element = angular.element '<own-comment></own-comment>'
    element = $compile(element) scope
    expect(element.text()).toBe 'this is the ownComment directive'
###
