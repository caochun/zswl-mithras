import { useMemo, useEffect } from 'react'
import { observer } from '@zswl/admin'
import { CardPanelFieldsFilter } from '@/pages/dashboard/workbench/components'
import { RadioTabs } from '@/components'
import { DatePicker } from 'antd'
import { Form, SearchBar, App, Button } from '@zswl/components'
import { UnorderedListOutlined } from '@ant-design/icons'
import ListDrawer from './ListDrawer'
import BarCharts from './BarCharts'
import Store from './Store'

const { Item } = SearchBar
const { RangePicker } = DatePicker

const Index = ({ initialQuery, innerModule }) => {
  const [form] = Form.useForm()
  const { optionsType } = App.getData()

  const store = useMemo(() => {
    return new Store()
  }, [])

  const { activityKey, queryDate, getChartsData } = store

  const tabItems = optionsType.businessGroupEnum.map(({ label, value }) => {
    return {
      label: label,
      key: value,
      children: <BarCharts store={store} />,
    }
  })

  useEffect(() => {
    // 经营全景视图入口进来
    if (initialQuery && initialQuery.queryDate) {
      store.setQueryDate(initialQuery.queryDate)
      form.setFieldsValue({
        queryDate: initialQuery.queryDate,
      })
    }
  }, [initialQuery])

  useEffect(() => {
    getChartsData()
  }, [activityKey, initialQuery])

  return (
    <>
      <CardPanelFieldsFilter
        innerModule={innerModule}
        title={'投放完成情况'}
        extra={
          <Button icon={<UnorderedListOutlined />} onClick={store.listDrawer.open}>
            查看详情
          </Button>
        }
      >
        <RadioTabs
          defaultActiveKey={activityKey}
          items={tabItems}
          activityKey={activityKey}
          onChange={(key) => {
            store.setActivityKey(key)
          }}
          tabBarExtraContent={
            <Form
              form={form}
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
      <ListDrawer store={store}></ListDrawer>
    </>
  )
}

export default observer(Index)
