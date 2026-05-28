import { history, observer } from '@zswl/admin'
import { Access, Button, Page, Tabs } from '@zswl/components'
import InsideList from './InsideList'
import OutsideList from './OutsideList'
import Store from './store'

const Index = ({ props: { sub }, path }) => {
  const items = [
    { key: 'inside', label: '内部名单', children: <InsideList path={path} /> },
    {
      key: 'outside',
      label: '外部名单',
      children: <OutsideList path={path} />,
      access: 'externalBlackGrayWarehouseRecordList',
    },
  ]
  return (
    <Page>
      <Tabs
        items={items}
        type="card"
        activeKey={Store.activeKey}
        onChange={Store.activeKeyChange}
      ></Tabs>
    </Page>
  )
}

export default observer(Index)
