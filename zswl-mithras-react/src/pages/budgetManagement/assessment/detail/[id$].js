import { observer, getQuery } from '@zswl/admin'
import { Button, Page, Tabs } from '@zswl/components'
import DeliveryPlanExecutionReport from './DeliveryPlanExecutionReport'
import store from './store'
import BudgetExecutionReport from './BudgetExecutionReport'
import BudgetImplementation from './BudgetImplementation'

const Index = ({ params: { id, taskActivityId } }) => {
  const detail = store.page.getData()

  const isFormApproval = getQuery('typeId') == 'approval'
  const isRevocation = ['sendback', 'revocation'].includes(getQuery('tab'))
  const canEdit = isFormApproval
    ? ['userTask_financialmanager'].includes(taskActivityId) || isRevocation
    : ['UN_SUBMIT', 'REJECT', 'CANCEL'].includes(detail.approvalStatus)

  return (
    <Page params={{ id }} header={null} store={store.page}>
      <Tabs
        defaultActiveKey="1"
        items={[
          {
            label: '效益考核表',
            key: '1',
            children: <DeliveryPlanExecutionReport id={id} canEdit={canEdit} />,
          },
          {
            label: '预算执行情况表',
            key: '2',
            children: <BudgetImplementation id={id} canEdit={false} type="budget" />,
          },
          {
            label: '投放计划执行情况表',
            key: '3',
            children: <BudgetExecutionReport id={id} canEdit={canEdit} />,
          },
        ]}
        tabBarExtraContent={[
          canEdit && !isFormApproval && (
            <Button.Submit onClick={store.submit} type="primary">
              提交
            </Button.Submit>
          ),
        ]}
      />
    </Page>
  )
}

export default observer(Index)
