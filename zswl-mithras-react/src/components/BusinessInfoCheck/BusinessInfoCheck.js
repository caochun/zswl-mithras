import { Button } from '@zswl/components'
import { observer } from '@zswl/admin'
import { forwardRef, useImperativeHandle, useMemo } from 'react'
import CompareInfo from './CompareInfo'
import TipsConfirm from './TipsConfirm'
import Store from './store'

const Index = (
  {
    flowId,
    contractId,
    modelKey,
    taskActivityId,
    paymentId,
    creditSearchId,
    taskStatus,
    needOption = true,
    needButton = true,
  },
  ref
) => {
  const store = useMemo(() => {
    return new Store({
      contractId,
      flowId,
      modelKey,
      taskActivityId,
      paymentId,
      taskStatus,
      creditSearchId,
    })
  }, [contractId, flowId, modelKey, taskActivityId, paymentId, taskStatus, creditSearchId])

  useImperativeHandle(ref, () => ({
    store,
    setSubmitFn: (fn) => {
      store.submitFn = fn
    },
  }))
  return (
    <>
      {needButton && (
        <Button
          type="link"
          onClick={() => {
            store.submitFn = null
            store.checkCompare()
          }}
        >
          工商信息校验
        </Button>
      )}
      <CompareInfo store={store} needOption={needOption}></CompareInfo>
      <TipsConfirm store={store}></TipsConfirm>
    </>
  )
}

export default observer(forwardRef(Index))
