import { observer } from '@zswl/admin'
import { Button, Page, Tabs } from '@zswl/components'
import Store from './store'
import { useMemo } from 'react'
import BudgetSummary from './components/BudgetSummary'
import PutProgress from './components/PutProgress'
import BudgetDetail from './components/BudgetDetail'
import { TableExportAction as TableExport } from '@/components/Actions'
import OtherBudgetSummary from './components/OtherBudgetSummary'
import { Empty } from 'antd'

const CALC_ENUM = {
  FAILURE: '利润预算计算失败，请联系管理重新计算...',
  DOING: '利润预算计算中...',
}
const CostBudget = ({ params }) => {
  const { id } = params
  const store = useMemo(() => new Store(), [])
  const detail = store.page.getData()
  const isOther = detail.budgetType === 'OTHER'
  const isYear = detail.budgetType === 'YEAR'
  const isCollect = detail.budgetStatus === 'COLLECT'
  const isConfirm = detail.budgetStatus === 'CONFIRM'
  const isNeedCollect = detail.needCollect === 1
  const needNotice = detail.isCollectTaskNotify === 0
  const isSuccess = detail.calculateStatus === 'SUCCESS'
  // const isSuccess = false

  const tabItems = [
    !isSuccess && {
      key: '0',
      label: '预算汇总',
      children: (
        <div className="z-flex-center" style={{ height: 'calc(100vh - 300px)' }}>
          <Empty
            description={CALC_ENUM[detail.calculateStatus]}
            image={'/public/assets/budgetManage/collect.png'}
            imageStyle={{ height: 200 }}
          />
        </div>
      ),
    },
    isSuccess &&
      !isOther && {
        key: '1',
        label: '预算汇总',
        children: <BudgetSummary store={store} />,
      },
    isSuccess &&
      isYear && {
        key: '2',
        label: '投放进度',
        forceRender: true,
        children: <PutProgress store={store} />,
      },
    isSuccess &&
      !isOther && {
        key: '3',
        label: '预算明细',
        forceRender: true,
        children: <BudgetDetail store={store} />,
      },
    isSuccess &&
      isOther && {
        key: '4',
        label: '预算汇总',
        children: <OtherBudgetSummary store={store} />,
      },
  ]

  return (
    <Page store={store} params={{ id }}>
      <Tabs
        forceRender
        defaultActiveKey="1"
        items={tabItems}
        tabBarExtraContent={[
          isNeedCollect && needNotice && (
            <Button type="primary" onClick={store.sendNotice}>
              待办发送
            </Button>
          ),
          !isNeedCollect && !isConfirm && (
            <Button type="primary" onClick={store.confirmBudget}>
              预算确认
            </Button>
          ),
          <Button onClick={store.goPlan}>投放计划</Button>,
          isSuccess && (
            <TableExport
              otherExcelProps={{ fileName: '利润预算表' }}
              table={[
                !isOther && { tableStore: store.budgetSummaryTable, sheetName: '预算汇总' },
                isYear && { tableStore: store.putProgressList, sheetName: '投放进度' },
                !isOther && { tableStore: store.budgetDetailList, sheetName: '预算明细' },
                !isOther && { tableStore: store.newBusinessList, sheetName: '公司新增业务' },
                isOther && { tableStore: store.otherBudgetSummaryList, sheetName: '预算汇总' },
              ].filter(Boolean)}
            />
          ),
        ]}
      />
    </Page>
  )
}

export default observer(CostBudget)
