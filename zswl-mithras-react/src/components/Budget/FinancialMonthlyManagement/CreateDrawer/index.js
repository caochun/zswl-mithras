import { observer } from '@zswl/admin'
import { Drawer, Button } from '@zswl/components'
import { Descriptions, Steps, Card, Space, Divider } from 'antd'
import { getLocalStorage } from '@zswl/admin'
import IncomeConfirm from './IncomeConfirm'
import IncomeProvision from './IncomeProvision'
import CostRecovery from './CostRecovery'
import StampDuty from './StampDuty'
import moment from 'moment'
import ReceiptModal from './ReceiptModal'

const BudgetFinancialMonthlyCreateDrawer = ({ store }) => {
  const { currentStep, setCurrentStep } = store
  const { editType } = store.createDrawer.getInitialValues() ?? {}

  const userInfo = getLocalStorage('userInfo')
  const isCreate = editType === 'create'
  const isRead = editType === 'read'
  const isEdit = editType === 'edit'
  const operationColumns = (source) =>
    !isRead
      ? {
          title: '操作',
          width: 180,
          fixed: 'right',
          actions: (record) => {
            const canIgnore = record.isEffect
            const canSend = record.isSendCq
            const canUpdate = record.newUpdated === 1
            const canConfirm = record.isSendCq === 0 && record.isConfirmed === 0
            return [
              !isCreate && {
                name: '反结算',
                confirm: canSend && '本操作将删除原苍穹单据，请再次确认！',
                disabled: !canSend,
                onClick: () => store.reverse({ ...record, source }),
              },
              !isCreate && {
                name: '更新',
                confirm: canUpdate,
                disabled: !canUpdate,
                onClick: () => store.updateRecord(record, source),
              },
              !isCreate && {
                name: '确认',
                confirm: canConfirm,
                disabled: !canConfirm,
                onClick: () => store.pushSingle(record),
              },
              isCreate && {
                name: '忽略',
                confirm: canIgnore,
                disabled: !canIgnore,
                onClick: () => store.changeStatus(record),
              },
              isCreate && {
                name: '激活',
                confirm: !canIgnore,
                disabled: canIgnore,
                onClick: () => store.changeStatus(record),
              },
            ]
          },
        }
      : {}
  const steps = [
    {
      title: '收入确认-实际利率法',
      key: 'incomeConfirm',
      content: <IncomeConfirm store={store} operationColumns={operationColumns} />,
    },
    {
      title: '收入计提-剩余本金法',
      key: 'incomeProvision',
      content: <IncomeProvision store={store} operationColumns={operationColumns} />,
    },
    {
      title: '成本计提',
      key: 'costRecovery',
      content: <CostRecovery store={store} operationColumns={operationColumns} />,
    },
    {
      title: '印花税',
      key: 'stampDuty',
      content: <StampDuty store={store} operationColumns={operationColumns} />,
    },
  ]
  const items = steps.map((item) => ({ key: item.title, title: item.title }))

  const canClick = !isCreate
  const title = isCreate ? '创建月结' : '修改月结'
  return (
    <Drawer
      store={store.createDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title={title}
      maskClosable={false}
      onClose={() => {
        store.createDrawer.close()
        setCurrentStep(0)
      }}
    >
      <div>
        <Card
          title={steps[currentStep].title}
          style={{ marginTop: 20 }}
          extra={
            <Space>
              {isEdit && (
                <Button type="primary" onClick={store.update}>
                  更新
                </Button>
              )}
              {isCreate && currentStep > 0 && (
                <Button onClick={() => store.setCurrentStep(currentStep - 1)}>上一步</Button>
              )}
              {isCreate && currentStep === steps.length - 1 && (
                <Button type="primary" onClick={store.confirmSubmit}>
                  确认并入账
                </Button>
              )}
              {isCreate && currentStep < steps.length - 1 && (
                <Button type="primary" onClick={() => store.setCurrentStep(currentStep + 1)}>
                  下一步
                </Button>
              )}
            </Space>
          }
        >
          <Steps current={currentStep} items={items} onChange={canClick && setCurrentStep} />
          <Divider></Divider>
          <div>{steps[currentStep].content}</div>
        </Card>
        <ReceiptModal store={store.receiptModal} />
      </div>
    </Drawer>
  )
}

export default observer(BudgetFinancialMonthlyCreateDrawer)
