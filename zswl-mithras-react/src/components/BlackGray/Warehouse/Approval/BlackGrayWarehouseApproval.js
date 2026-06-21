import { makeAutoObservable, observer } from '@zswl/admin'
import { Page, Tabs } from '@zswl/components'
import InsideList from './InsideList'
import OutsideList from './OutsideList'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  activeKey = 'inside'
  activeKeyChange = (e) => {
    this.activeKey = e
  }
}
const store = new Store()
const BlackGrayWarehouseApproval = ({ path }) => {
  const items = [
    { key: 'inside', label: '内部名单', children: <InsideList path={path} /> },
    {
      key: 'outside',
      label: '外部名单',
      children: <OutsideList path={path} />,
      access: 'externalBlackgrayapprovalwarehouseauditList',
    },
  ]
  return (
    <Page>
      <Tabs
        items={items}
        type="card"
        activeKey={store.activeKey}
        onChange={store.activeKeyChange}
      ></Tabs>
    </Page>
  )
}

export default observer(BlackGrayWarehouseApproval)
