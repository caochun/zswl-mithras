import { Button } from '@zswl/components'
import { observer } from '@zswl/admin'
import { forwardRef, useImperativeHandle, useMemo } from 'react'
import CompareInfo from './CompareInfo'
import TipsConfirm from './TipsConfirm'
import Store from './store'

const Index = ({ flowId, clientId, modelKey, taskActivityId, taskStatus }, ref) => {
  const store = useMemo(() => {
    return new Store({ clientId, flowId, modelKey, taskActivityId, taskStatus })
  }, [clientId, flowId, modelKey, taskActivityId, taskStatus])

  useImperativeHandle(ref, () => ({
    store,
  }))
  return (
    <div>
      <Button type="link" onClick={() => store.checkCompare()}>
        工商信息校验
      </Button>
      <CompareInfo store={store}></CompareInfo>
      <TipsConfirm store={store}></TipsConfirm>
    </div>
  )
}

export default observer(forwardRef(Index))
