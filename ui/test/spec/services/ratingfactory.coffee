'use strict'

describe 'Service: RatingFactory', ->

  # load the service's module
  beforeEach module 'uiApp'

  # instantiate service
  RatingFactory = {}
  beforeEach inject (_RatingFactory_) ->
    RatingFactory = _RatingFactory_

  it 'should do something', ->
    expect(!!RatingFactory).toBe true
