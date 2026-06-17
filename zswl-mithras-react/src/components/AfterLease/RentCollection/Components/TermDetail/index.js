import { Drawer } from '@zswl/components'
import { Tabs } from 'antd'
import Notice from './Notice'
import OverdueCollection from './OverdueSituation'
import RentInfo from './RentInfo'
import Store from './store'
import { useEffect, useMemo } from 'react'

const TermDetail = ({ baseStore, defaultActiveKey = '1', ...props }) => {
  const store = useMemo(() => new Store(), [])
  useEffect(() => {
    store.setPageParams(props)
    store.init(props)
  }, [props.collectionId, props.phase])

  return (
    <div>
      <Drawer
        width={1000}
        title={'期项租金卡'}
        placement="right"
        store={baseStore.$termDetailDrawer}
        destroyOnClose
        extra={null}
      >
        <Tabs defaultActiveKey={defaultActiveKey} destroyInactiveTabPane>
          <Tabs.TabPane tab="租金信息" key="1">
            <RentInfo baseStore={baseStore} store={store} />
          </Tabs.TabPane>
          <Tabs.TabPane tab="到期通知" key="2">
            <Notice store={store} />
          </Tabs.TabPane>
          <Tabs.TabPane tab="逾期情况" key="3">
            <OverdueCollection store={store} />
          </Tabs.TabPane>
        </Tabs>
      </Drawer>
    </div>
  )
}

export default TermDetail
