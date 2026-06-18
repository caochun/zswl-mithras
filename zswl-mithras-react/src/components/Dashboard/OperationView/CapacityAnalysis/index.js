import { useMemo, useEffect } from 'react'
import { observer } from '@zswl/admin'
import CardPanelFieldsFilter from '../../CardPanelFieldsFilter'
import IconFont from '@/components/Icon'
import RadioTabs from '../../RadioTabs'
import { UnorderedListOutlined } from '@ant-design/icons'
import { DatePicker } from 'antd'
import { Form, SearchBar, Select, App, Button } from '@zswl/components'
import ListDrawer from './ListDrawer'
import DepartCharts from './DepartCharts'
import PersonCharts from './PersonCharts'
import Store from './Store'
import styles from './index.less'

const { Item } = SearchBar
const { RangePicker } = DatePicker

const Index = () => {
  const { optionsType } = App.getData()

  const store = useMemo(() => {
    return new Store()
  }, [])

  const { activityKey, queryDate, curProjStage, getChartsData } = store

  const tabItems = optionsType.businessGroupEnum.map(({ label, value }) => {
    return {
      label: label,
      key: value,
      children: (
        <>
          <h4>部门产能分析</h4>
          <DepartCharts store={store} />
          <div style={{ height: 20 }}></div>
          <h4>人均产能分析</h4>
          <PersonCharts store={store} />
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
        title={'产能分析'}
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
                projStage: curProjStage,
              }}
            >
              <Item label="时间" name="queryDate">
                <RangePicker allowClear={false} picker={'month'}></RangePicker>
              </Item>
              <Item label="项目阶段" name="projStage">
                <Select options="dashboardProjStageEnum" allowClear={false}></Select>
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
