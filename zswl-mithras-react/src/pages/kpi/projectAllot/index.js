import { Page } from '@zswl/components'
import { observer } from '@zswl/admin'
import { Tabs } from 'antd'
import Deal from './Component/Deal'
import UnDeal from './Component/UnDeal'
import store from './store'

const Index = ({ pathname }) => {
  const { activeKey, setActiveKey } = store
  const commonProps = {
    store,
    activeKey,
    pathname,
  }
  return (
    <Page>
      <Tabs
        activeKey={activeKey}
        destroyInactiveTabPane
        onChange={setActiveKey}
        items={[
          {
            label: '未分配',
            key: 'undeal',
            children: <UnDeal {...commonProps}></UnDeal>,
          },
          {
            label: '已分配',
            key: 'deal',
            children: <Deal {...commonProps}></Deal>,
          },
        ]}
      />
    </Page>
  )
}
export default observer(Index)
