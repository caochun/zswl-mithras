import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import CardPanelFieldsFilter from '../../../CardPanelFieldsFilter'
import { Form } from '@zswl/components'
import { DatePicker } from 'antd'
import ContractApprovalAgingCard from './ApprovalCard/ContractApprovalAgingCard'
import ContractReturnStatisticsCard from './ReturnCard/ContractReturnStatisticsCard'
import Store from './Store'

const { RangePicker } = DatePicker
const { Item } = Form

const WorkbenchContractAging = () => {
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
        <ContractApprovalAgingCard store={store}></ContractApprovalAgingCard>
        <div style={{ height: 20 }}></div>
        <ContractReturnStatisticsCard store={store}></ContractReturnStatisticsCard>
      </CardPanelFieldsFilter>
    </>
  )
}

export default observer(WorkbenchContractAging)
