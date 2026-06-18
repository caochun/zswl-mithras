import { useMemo, useEffect } from 'react'
import { observer } from '@zswl/admin'
import CardPanelFieldsFilter from '../../CardPanelFieldsFilter'
import { UnorderedListOutlined } from '@ant-design/icons'
import RadioTabs from '../../RadioTabs'
import { DatePicker } from 'antd'
import { Form, SearchBar, App, Button } from '@zswl/components'
import ListDrawer from './ListDrawer'
import BarCharts from './BarCharts'
import Store from './Store'

const { Item } = SearchBar
const { RangePicker } = DatePicker

const Index = ({ initialQuery, title }) => {
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
      children: (
        <>
          <BarCharts store={store} />
        </>
      ),
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
        title={title || '转化率情况'}
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
