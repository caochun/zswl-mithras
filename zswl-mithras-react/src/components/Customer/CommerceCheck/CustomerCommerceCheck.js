import { Button } from '@zswl/components'
import { observer } from '@zswl/admin'
import { useMemo } from 'react'
import CompareInfo from './CompareInfo/CustomerCommerceCheckCompareInfo'
import TipsConfirm from './TipsConfirm/CustomerCommerceCheckTipsConfirm'
import Store from './store'

const CustomerCommerceCheck = ({ clientId }) => {
  const store = useMemo(() => {
    return new Store({ clientId })
  }, [clientId])

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

export default observer(CustomerCommerceCheck)
