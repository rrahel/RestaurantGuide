'use strict'

describe 'Controller: AdduserCtrl', ->

  # load the controller's module
  beforeEach module 'uiApp'

  AdduserCtrl = {}

  scope = {}

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope) ->
    scope = $rootScope.$new()
    AdduserCtrl = $controller 'AdduserCtrl', {
      # place here mocked dependencies
    }

  it 'should attach a list of awesomeThings to the scope', ->
    expect(AdduserCtrl.awesomeThings.length).toBe 3
