import { observer } from '@zswl/admin'
import { CardPanelFieldsFilter } from '@/pages/dashboard/workbench/components'
import { UnorderedListOutlined } from '@ant-design/icons'
import { Button } from '@zswl/components'
import BarCharts from './BarCharts'
import Store from './Store'
import ListDrawer from './ListDrawer'
import { useMemo, useEffect } from 'react'
import { canSeeDetailFn } from '@/pages/dashboard/overView/utils'

const Index = () => {
  const store = useMemo(() => new Store(), [])
  const { getChartsData } = store

  useEffect(() => {
    getChartsData()
  }, [])

  return (
    <div>
      <CardPanelFieldsFilter
        title={'本年租赁业务投放收益率情况表'}
        innerModule={true}
        extra={
          canSeeDetailFn() && (
            <Button icon={<UnorderedListOutlined />} onClick={store.listDrawer.open}>
              查看详情
            </Button>
          )
        }
      >
        <BarCharts store={store} />
      </CardPanelFieldsFilter>
      <ListDrawer store={store}></ListDrawer>
    </div>
  )
}

export default observer(Index)
