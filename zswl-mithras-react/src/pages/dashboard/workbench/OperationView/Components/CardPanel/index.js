import { observer } from '@zswl/admin'
import { DatePicker } from 'antd'
import { CardPanelFieldsFilter } from '@/pages/dashboard/workbench/components'
import { UnorderedListOutlined } from '@ant-design/icons'
import { RadioTabs } from '@/components'
import { Form, SearchBar, Button } from '@zswl/components'
import { useMemo, useState } from 'react'

const { Item } = SearchBar
const { RangePicker } = DatePicker

const Index = ({ title, store, form, tabBarExtraContent = null, renderChildren = () => null }) => {
  const [activityKey, setActivityKey] = useState('PUBLIC_CATEGORY')

  const tabChildren = useMemo(() => {
    return renderChildren(activityKey)
  }, [activityKey])

  return (
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
        onChange={(key) => {
          store.setActivityKey(key)
          setActivityKey(key)
        }}
        tabBarExtraContent={
          <Form onValuesChange={store.handleFormChange} layout="inline" form={form}>
            <Item label="时间" name="time">
              <RangePicker></RangePicker>
            </Item>
            {tabBarExtraContent}
          </Form>
        }
        items={[
          {
            label: '公用组',
            key: 'PUBLIC_CATEGORY',
            children: tabChildren,
          },
          {
            label: '产业组',
            key: 'INDUSTRY_CATEGORY',
            children: tabChildren,
          },
        ]}
      ></RadioTabs>
    </CardPanelFieldsFilter>
  )
}

export default observer(Index)
