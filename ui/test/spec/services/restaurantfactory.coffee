'use strict'

describe 'Service: RestaurantFactory', ->

# load the service's module
  beforeEach module 'uiApp'

  # instantiate service
  RestaurantFactory = {}
  beforeEach inject (_RestaurantFactory_) ->
    RestaurantFactory = _RestaurantFactory_

  it 'should do something', ->
    expect(!!RestaurantFactory).toBe true
