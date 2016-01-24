'use strict'

describe 'Service: CommentFactory', ->

  # load the service's module
  beforeEach module 'uiApp'

  # instantiate service
  CommentFactory = {}
  beforeEach inject (_CommentFactory_) ->
    CommentFactory = _CommentFactory_

  it 'should do something', ->
    expect(!!CommentFactory).toBe true
