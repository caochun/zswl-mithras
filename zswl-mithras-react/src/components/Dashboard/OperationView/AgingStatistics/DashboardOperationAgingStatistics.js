import { useMemo, useEffect } from 'react'
import { observer } from '@zswl/admin'
import CardPanelFieldsFilter from '../../CardPanelFieldsFilter'
import { UnorderedListOutlined } from '@ant-design/icons'
import RadioTabs from '../../RadioTabs'
import { DatePicker } from 'antd'
import { Form, SearchBar, App, Button } from '@zswl/components'
import AgingStatisticsListDrawer from './ListDrawer/AgingStatisticsListDrawer'
import AgingStatisticsBarChart from './BarCharts/AgingStatisticsBarChart'
import Store from './Store'

const { Item } = SearchBar
const { RangePicker } = DatePicker

const DashboardOperationAgingStatistics = ({ title = '时效统计' }) => {
  const { optionsType } = App.getData()

  const store = useMemo(() => {
    return new Store()
  }, [])

  const { activityKey, queryDate, getChartsData } = store

  const tabItems = optionsType.businessGroupEnum.map(({ label, value }) => {
    return {
      label: label,
      key: value,
      children: (
        <>
          <AgingStatisticsBarChart store={store} />
        </>
      ),
    }
  })

  useEffect(() => {
    getChartsData()
  }, [activityKey])

  return (
    <>
      <CardPanelFieldsFilter
        title={title}
        extra={
          <Button icon={<UnorderedListOutlined />} onClick={store.listDrawer.open}>
            查看详情
          </Button>
        }
      >
        <RadioTabs
          activityKey={activityKey}
          items={tabItems}
          onChange={(key) => {
            store.setActivityKey(key)
          }}
          tabBarExtraContent={
            <Form
              onValuesChange={store.onValuesChange}
              layout="inline"
              initialValues={{
                queryDate,
              }}
            >
              <Item label="时间" name="queryDate">
                <RangePicker allowClear={false} picker={'month'}></RangePicker>
              </Item>
            </Form>
          }
        ></RadioTabs>
      </CardPanelFieldsFilter>
      <AgingStatisticsListDrawer store={store}></AgingStatisticsListDrawer>
    </>
  )
}

export default observer(DashboardOperationAgingStatistics)
