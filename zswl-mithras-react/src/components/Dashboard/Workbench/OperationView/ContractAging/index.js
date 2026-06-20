import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import CardPanelFieldsFilter from '../../../CardPanelFieldsFilter'
import { Form } from '@zswl/components'
import { DatePicker } from 'antd'
import ApprovalCard from './ApprovalCard'
import ReturnCard from './ReturnCard'
import Store from './Store'

const { RangePicker } = DatePicker
const { Item } = Form

const Index = () => {
  const store = useMemo(() => {
    return new Store()
  }, [])

  return (
    <>
      <CardPanelFieldsFilter
        style={{ padding: '10px 0' }}
        title={'合同审批时效及退回情况'}
        extra={
          <Form
            onValuesChange={store.onValuesChange}
            layout="inline"
            initialValues={{
              queryDate: store.queryDate,
            }}
          >
            <Item label="时间" name="queryDate">
              <RangePicker allowClear={true} picker={'month'}></RangePicker>
            </Item>
          </Form>
        }
      >
        <ApprovalCard store={store}></ApprovalCard>
        <div style={{ height: 20 }}></div>
        <ReturnCard store={store}></ReturnCard>
      </CardPanelFieldsFilter>
    </>
  )
}

export default observer(Index)
