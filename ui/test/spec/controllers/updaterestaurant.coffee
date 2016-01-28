###
'use strict'

describe 'Controller: UpdaterestaurantCtrl', ->

  # load the controller's module
  beforeEach module 'uiApp'

  UpdaterestaurantCtrl = {}

  scope = {}

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope) ->
    scope = $rootScope.$new()
    UpdaterestaurantCtrl = $controller 'UpdaterestaurantCtrl', {
      # place here mocked dependencies
    }

  it 'should attach a list of awesomeThings to the scope', ->
    expect(UpdaterestaurantCtrl.awesomeThings.length).toBe 3
###
