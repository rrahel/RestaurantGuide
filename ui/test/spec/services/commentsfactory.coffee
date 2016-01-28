'use strict'

describe 'Service: CommentsFactory', ->

  # load the service's module
  beforeEach module 'uiApp'

  # instantiate service
  CommentsFactory = {}
  beforeEach inject (_CommentsFactory_) ->
    CommentsFactory = _CommentsFactory_

  it 'should do something', ->
    expect(!!CommentsFactory).toBe true
