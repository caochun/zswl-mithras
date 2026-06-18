import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { Summary } from '@/components/Table'
import { AmountColumn } from '@/components/Format'

const BudgetDetail = ({ store }) => {
  // 上月末业务余额表格
  const renderBusinessBalance = () => {
    const columns = [
      {
        title: '部门名称',
        dataIndex: 'deptName',
        width: 160,
        fixed: 'left',
      },
      AmountColumn({
        title: '上月末业务余额',
        dataIndex: 'lastMonthBalance',
        width: 160,
      }),
      AmountColumn({
        title: '预算收入合计(不含税)',
        dataIndex: 'budgetIncomeTotal',
        width: 160,
      }),
      AmountColumn({
        title: '预算成本(不含税)',
        dataIndex: 'budgetCostTotal',
        width: 160,
      }),
      AmountColumn({
        title: '增值税',
        dataIndex: 'valueAddedTax',
        width: 120,
      }),
      AmountColumn({
        title: '税金及附加',
        dataIndex: 'taxAndSurcharge',
        width: 120,
      }),
      AmountColumn({
        title: '当月利润调整金额(增)',
        dataIndex: 'profitAdjustIncrease',
        width: 160,
      }),
      AmountColumn({
        title: '风险资金余额(一般风险准备)',
        dataIndex: 'riskFundBalance',
        width: 180,
      }),
    ]

    return (
      <>
        <h3>I.公司存量业务</h3>
        <Table
          columns={columns}
          store={store.businessBalanceList}
          scroll={{ x: 'max-content', y: 550 }}
          summary={(data) => <Summary columns={columns} sumData={store?.summary} startIndex={1} />}
        />
      </>
    )
  }

  // 公司新增业务表格
  const renderNewBusiness = () => {
    const columns = [
      {
        title: '部门名称',
        dataIndex: 'deptName',
        width: 120,
        fixed: 'left',
      },
      {
        title: '项目名称',
        dataIndex: 'projectName',
        width: 160,
      },
      {
        title: '服务客户',
        dataIndex: 'customerName',
        width: 160,
      },
      {
        title: '行业',
        dataIndex: 'industry',
        width: 120,
      },
      {
        title: '业务类型',
        dataIndex: 'businessType',
        width: 120,
      },
      {
        title: '预计投放时间',
        dataIndex: 'expectedPutTime',
        width: 160,
      },
      {
        title: '平均期限(期限/月)',
        dataIndex: 'averagePeriod',
        width: 160,
      },
      {
        title: '还款周期',
        dataIndex: 'repaymentCycle',
        width: 120,
      },
      AmountColumn({
        title: '本月新增投放额',
        dataIndex: 'monthlyNewAmount',
        width: 160,
      }),
    ]

    return (
      <>
        <h3>II.公司新增业务</h3>
        <Table
          columns={columns}
          store={store.newBusinessList}
          scroll={{ x: 'max-content' }}
          summary={(data) => <Summary columns={columns} sumData={store?.summary} startIndex={1} />}
        />
      </>
    )
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      {renderBusinessBalance()}
      {renderNewBusiness()}
    </div>
  )
}

export default observer(BudgetDetail)
