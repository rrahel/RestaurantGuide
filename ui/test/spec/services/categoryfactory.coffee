'use strict'

describe 'Service: CategoryFactory', ->

  # load the service's module
  beforeEach module 'uiApp'

  # instantiate service
  CategoryFactory = {}
  beforeEach inject (_CategoryFactory_) ->
    CategoryFactory = _CategoryFactory_

  it 'should do something', ->
    expect(!!CategoryFactory).toBe true
