const requestQueue = (maxNum = 6) => {
  const queue = []
  let current = 0
  let resolvedCount = 0

  const dequeue = () => {
    while (current < maxNum && queue.length > 0) {
      current++
      const currentPromise = queue.shift()
      currentPromise()
        .then(() => {
          resolvedCount++
          current--
          checkQueue()
        })
        .catch((error) => {
          console.error('请求执行失败:', error)
          current--
          checkQueue()
        })
    }
  }

  const enqueue = (promiseFunc) => {
    queue.push(promiseFunc)
    if (queue.length <= maxNum) {
      dequeue()
    }
  }

  const checkQueue = () => {
    if (queue.length === 0 && current === 0) {
      resolveQueue()
    } else if (queue.length > 0 && current < maxNum) {
      dequeue()
    }
  }

  let resolveQueueCallback
  let rejectQueueCallback

  const queueEmptyPromise = new Promise((resolve, reject) => {
    resolveQueueCallback = resolve
    rejectQueueCallback = reject
  })

  const resolveQueue = () => {
    if (typeof resolveQueueCallback === 'function') {
      resolveQueueCallback(resolvedCount)
      resolveQueueCallback = null
    }
  }

  const rejectQueue = (error) => {
    if (typeof rejectQueueCallback === 'function') {
      rejectQueueCallback(error)
      rejectQueueCallback = null
    }
  }

  return {
    enqueue,
    queueEmptyPromise,
    rejectQueue,
  }
}

export default requestQueue
