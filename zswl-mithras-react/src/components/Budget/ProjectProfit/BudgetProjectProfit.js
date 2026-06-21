import { observer } from '@zswl/admin'
import { Table, Page, Button, Modal, Form } from '@zswl/components'
import { getTableColumns, saveServer } from '@/utils'
import ALL_COLUMNS from '../ProjectProfitColumns'
import { DatePicker, Space } from 'antd'
import { useMemo, useEffect, useState } from 'react'
import Store from './store'
import moment from 'moment'
import SettingModal from './SettingModal'
import Api from '@/api/budget/projectProfitApi'

const nameColumns = [
  '月份',
  '状态',
  '本年累计收入',
  '本年累计资金成本',
  '本年累计风险金',
  '本年累计利润总额(扣除费用后)',
  '本年累计附加税',
  '本年累计印花税',
  '本月收入',
  '本月资金成本',
  '本月风险金计提/冲抵',
  '利润-当期值',
]

const CalculationModal = ({ store }) => {
  const [lastMonth, setLastMonth] = useState(null)

  const getLastMonth = async () => {
    const res = await Api.postFtpInterestLastMonth()
    setLastMonth(res)
  }

  useEffect(() => {
    getLastMonth()
  }, [])

  const disabledDate = (current) => {
    if (!lastMonth) return false
    return current && current < moment(lastMonth).endOf('month')
  }
  return (
    <Modal title="利润测算" store={store.calculationModal}>
      <Form>
        <Form.Item
          label="月份"
          name="yearAndMonth"
          transform={(val) => {
            return val && moment(val).format('YYYY-MM-DD')
          }}
        >
          <DatePicker disabledDate={disabledDate} picker="month" />
        </Form.Item>
      </Form>
    </Modal>
  )
}
const BudgetProjectProfit = ({ pathname }) => {
  const store = useMemo(() => new Store(), [])
  const columns = getTableColumns(ALL_COLUMNS({ pathname }), nameColumns)

  return (
    <Page>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
        <Space>
          <Button onClick={store.calculationModal.open}>利润测算</Button>
          <Button onClick={store.handleConfirm} disabled={!store.$table.getSelected().rows.length}>
            数据确认
          </Button>
          <Button.Setting key={'setting'} onClick={store.setModal.open}></Button.Setting>
        </Space>
        <div>货币单位：元</div>
      </div>
      <Table
        columnsFilter="budget_projProfit_1"
        onFilter={(key, val) => saveServer('budget_projProfit_1', val)}
        selectable={{
          type: 'checkBox',
          getCheckboxProps: (record) => {
            return {
              disabled: record.isConfirmed === 1,
            }
          },
        }}
        store={store.$table}
        columnWidth={180}
        editable={false}
        scroll={{ x: 1000 }}
        columns={columns}
      />
      <CalculationModal store={store} />
      <SettingModal store={store} />
    </Page>
  )
}
export default observer(BudgetProjectProfit)
