import { observer } from '@zswl/admin'
import { App, Button } from '@zswl/components'
import { CardPanelFieldsFilter } from '@/pages/dashboard/workbench/components'
import { UnorderedListOutlined } from '@ant-design/icons'
import BarCharts from './BarCharts'
import { RadioTabs } from '@/components'
import Store from './Store'
import ListDrawer from './ListDrawer'
import { useMemo, useEffect } from 'react'

const Index = () => {
  const store = useMemo(() => new Store(), [])
  const { optionsType } = App.getData()
  const { activityKey, getChartsData } = store

  const businessGroupEnum = [{ label: '合计', value: 'ALL' }, ...optionsType.businessGroupEnum]

  const tabItems = businessGroupEnum.map(({ label, value }) => {
    return {
      label: label,
      key: value,
      children: (
        <>
          <BarCharts store={store} />
        </>
      ),
    }
  })
  useEffect(() => {
    getChartsData()
  }, [activityKey])

  return (
    <div>
      <CardPanelFieldsFilter
        title={'业务月度收入表'}
        innerModule={true}
        extra={
          <Button icon={<UnorderedListOutlined />} onClick={store.listDrawer.open}>
            查看详情
          </Button>
        }
      >
        <RadioTabs
          defaultActiveKey={activityKey}
          activityKey={activityKey}
          items={tabItems}
          onChange={(key) => {
            store.setActivityKey(key)
          }}
        ></RadioTabs>
      </CardPanelFieldsFilter>
      <ListDrawer store={store}></ListDrawer>
    </div>
  )
}

export default observer(Index)
