import { observer } from '@zswl/admin'
import { Select, Table } from '@zswl/components'
import { AmountColumn } from '@/components/Format'
import { OrgSelect } from '@/components/Select'

const INIT_FORMAT = 10000 * 10000
const columns = [
  {
    title: '部门',
    dataIndex: 'belongDeptName',
    fixed: 'left',
    onCell: (record, index) => {
      if (index % 5 === 0) return { rowSpan: 5 }
      return {
        rowSpan: 0,
      }
    },
  },
  {
    title: 'FTP行业',
    dataIndex: 'ftpIndustryCategoryDisplay',
    fixed: 'left',
  },
  AmountColumn({ title: '投放额（万元）', dataIndex: 'payAmountFeature', initFormat: INIT_FORMAT }),
  {
    title: '存量项目情况',
    children: [
      AmountColumn({ title: '收入（万元）', dataIndex: 'incomeHistory', initFormat: INIT_FORMAT }),
      AmountColumn({ title: '成本（万元）', dataIndex: 'costHistory', initFormat: INIT_FORMAT }),
      AmountColumn({ title: '差价（万元）', dataIndex: 'diffHistory', initFormat: INIT_FORMAT }),
      AmountColumn({
        title: '税费+拨备（万元）',
        dataIndex: 'taxRiskHistory',
        initFormat: INIT_FORMAT,
        width: 180,
      }),
      AmountColumn({
        title: '利润总额（万元）',
        dataIndex: 'profitHistory',
        initFormat: INIT_FORMAT,
        width: 180,
      }),
    ],
  },
  {
    title: '新增项目情况',
    children: [
      AmountColumn({ title: '收入（万元）', dataIndex: 'incomeFeature', initFormat: INIT_FORMAT }),
      AmountColumn({ title: '成本（万元）', dataIndex: 'costFeature', initFormat: INIT_FORMAT }),
      AmountColumn({ title: '差价（万元）', dataIndex: 'diffFeature', initFormat: INIT_FORMAT }),
      AmountColumn({
        title: '税费（万元）',
        dataIndex: 'taxFeature',
        initFormat: INIT_FORMAT,
        width: 180,
      }),
      AmountColumn({
        title: '拨备（万元）',
        dataIndex: 'riskFeature',
        initFormat: INIT_FORMAT,
        width: 180,
      }),
      AmountColumn({
        title: '利润总额（万元）',
        dataIndex: 'profitFeature',
        initFormat: INIT_FORMAT,
        width: 180,
      }),
    ],
  },
  {
    title: '合计',
    children: [
      AmountColumn({
        title: '营业收入（万元）',
        dataIndex: 'incomeTotal',
        initFormat: INIT_FORMAT,
        width: 180,
      }),
      AmountColumn({
        title: '利润（万元）',
        dataIndex: 'profitTotal',
        initFormat: INIT_FORMAT,
      }),
      AmountColumn({
        title: '费用（万元）',
        dataIndex: 'expenseTotal',
        initFormat: INIT_FORMAT,
      }),
      AmountColumn({
        title: '目标利润（万元）',
        dataIndex: 'profitGoalTotal',
        initFormat: INIT_FORMAT,
        width: 200,
      }),
      AmountColumn({
        title: '目标利润（拨备前）（万元）',
        dataIndex: 'profitGoalWithoutRiskFundTotal',
        initFormat: INIT_FORMAT,
        width: 240,
      }),
      AmountColumn({
        title: '年初资产总额（万元）',
        dataIndex: 'beginOfThisPeriodBalance',
        initFormat: INIT_FORMAT,
        width: 200,
      }),
      AmountColumn({
        title: '资产总额（万元）',
        dataIndex: 'endOfThisPeriodBalance',
        initFormat: INIT_FORMAT,
        width: 200,
      }),
    ],
  },
  {
    title: '参数（%）',
    children: [
      AmountColumn({ title: '收益率水平（IRR）', dataIndex: 'irrFeature', width: 180 }),
      AmountColumn({
        title: '年化咨询服务费率',
        dataIndex: 'consultingFeeRateYearFeature',
        width: 180,
      }),
      AmountColumn({ title: '咨询服务费率', dataIndex: 'consultingFeeRateFeature' }),
      AmountColumn({ title: '资金成本（FTP）', dataIndex: 'ftpFeature', width: 180 }),
    ],
  },
]

const BudgetSummary = ({ store }) => {
  return (
    <>
      <Table
        bordered
        columns={columns}
        store={store.budgetSummaryTable}
        editable={false}
        columnWidth={140}
        searchbar={[
          {
            label: '部门',
            dataIndex: 'belongDeptId',
            element: <OrgSelect functionCode="selectorgs-buggetNotmonth" />,
          },
          {
            label: '行业分类',
            dataIndex: 'ftpIndustryCategory',
            element: <Select options="ftpIndustryCategoryEnum" />,
          },
        ]}
      />
    </>
  )
}

export default observer(BudgetSummary)
