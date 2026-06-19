import { Link, observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { AmountColumn, AmountEditable, DateColumn, InputColumn } from '@/components/Format'
import { isFinancialManager, isFinancialOfficer } from '@/utils'

// 上月末业务余额表格
const RenderBusinessBalance = observer(({ store }) => {
  const id = store.page.getParams()?.id
  const detail = store.page.getData()
  const isYear = ['HALF_OF_YEAR', 'YEAR'].includes(detail.budgetType)

  const columns = [
    {
      title: '部门名称',
      dataIndex: 'belongDeptName',
      width: 160,
      fixed: 'left',
      excelRender: (val, { belongDeptId }) => val,
      render: (val, { belongDeptId }) => {
        if (!belongDeptId) return val
        return (
          <Link
            to={`/budgetManagement/plan/profit/businessDetail/${id}?belongDeptId=${belongDeptId}&isYear=${isYear}`}
          >
            {val}
          </Link>
        )
      },
    },
    AmountColumn({
      title: isYear ? '上年末业务余额（元）' : '上月末业务余额（元）',
      dataIndex: 'lastPeriodBalance',
      width: 160,
    }),
    AmountColumn({
      title: '营业收入（不含税）（元）',
      dataIndex: 'incomeWithoutTax',
      width: 160,
    }),
    AmountColumn({
      title: '营业成本（元）',
      dataIndex: 'costWithoutTax',
      width: 160,
    }),
    AmountColumn({
      title: '增值税（元）',
      dataIndex: 'valueAddedTax',
    }),
    AmountColumn({
      title: '税金及附加（元）',
      dataIndex: 'taxOther',
    }),
    AmountColumn({
      title: isYear ? '全年平均资金占用额（元）' : '当月平均资金占用额（元）',
      dataIndex: 'averageOccupyThisPeriod',
      width: 160,
    }),
    AmountColumn({
      title: '风险准备金（元）',
      dataIndex: 'riskFund',
      width: 160,
    }),
    AmountColumn({
      title: '考核利润（元）',
      dataIndex: 'profit',
    }),
    AmountColumn({
      title: isYear ? '本年末资产余额（元）' : '本月末资产余额（元）',
      dataIndex: 'thisPeriodBalance',
      width: 160,
    }),
  ]

  return (
    <>
      <div className="z-sub-title">公司存量业务</div>
      <Table
        columns={columns}
        store={store.budgetDetailList}
        columnWidth={120}
        scroll={{ x: 'max-content', y: 400 }}
      />
    </>
  )
})

// 公司新增业务表格
const RenderNewBusiness = observer(({ store }) => {
  const detail = store.page.getData()
  const { editIndex } = store
  const isYear = ['HALF_OF_YEAR', 'YEAR'].includes(detail.budgetType)
  const isFinancial = isFinancialManager() || isFinancialOfficer()
  const AmountEditableRow = ({ title, dataIndex, initFormat, suffix, required, width }) => {
    return AmountColumn({
      title,
      dataIndex,
      initFormat,
      suffix,
      required,
      width,
      editable: (record, rowIndex) =>
        editIndex === rowIndex
          ? AmountEditable(record, dataIndex, {
              disabled: false,
              required,
              initFormat,
              inputConfig: {
                min: -Infinity,
              },
            })
          : false,
    })
  }
  const columns = [
    InputColumn({
      title: '部门名称',
      dataIndex: 'belongDeptName',
      width: 120,
      fixed: 'left',
      editable: false,
    }),
    InputColumn({ title: '客户名称', dataIndex: 'clientName', width: 240, editable: false }),
    InputColumn({ title: '合同编号', dataIndex: 'contractCode', width: 290, editable: false }),
    InputColumn({
      title: 'FTP行业分类',
      dataIndex: 'ftpIndustryCategoryDisplay',
      width: 120,
      editable: false,
    }),
    InputColumn({
      title: '风控行业分类',
      dataIndex: 'riskControlIndustryClassifyDisplay',
      width: 120,
      editable: false,
    }),
    InputColumn({ title: '租赁类型', dataIndex: 'leaseTypeDisplay', width: 120, editable: false }),
    DateColumn({ title: '预计投放日', dataIndex: 'planPayDate', width: 120, editable: false }),
    {
      title: '租赁期限（月）',
      dataIndex: 'termMonth',
      width: 150,
      editable: false,
    },
    { title: '还款周期', dataIndex: 'repayFrequencyDisplay', width: 120, editable: false },
    AmountColumn({ title: '投放额（元）', dataIndex: 'payAmount', width: 160 }),
    AmountColumn({
      title: '保证金比例',
      dataIndex: 'depositRate',
      suffix: '%',
      width: 120,
    }),
    AmountColumn({
      title: '合同利率',
      dataIndex: 'contractInterestRate',
      suffix: '%',
      width: 120,
    }),
    AmountColumn({
      title: 'IRR',
      dataIndex: 'irr',
      suffix: '%',
      width: 120,
    }),
    AmountColumn({
      title: 'XIRR',
      dataIndex: 'xirr',
      suffix: '%',
      width: 120,
    }),
    AmountColumn({
      title: '年化咨询费率',
      suffix: '%',
      dataIndex: 'consultingFeeRateYear',
      width: 160,
    }),
    AmountColumn({
      title: '咨询费率',
      dataIndex: 'consultingFeeRate',
      suffix: '%',
      width: 120,
    }),
    AmountColumn({
      title: 'FTP',
      dataIndex: 'ftp',
      suffix: '%',
      width: 120,
    }),

    AmountEditableRow({
      title: '利息收入（元）（不含税）',
      dataIndex: 'interestIncomeWithoutTax',
      width: 180,
    }),
    AmountEditableRow({
      title: '咨询服务费收入（元）（不含税）',
      dataIndex: 'consultingFeeIncomeWithoutTax',
      width: 180,
    }),
    AmountEditableRow({
      title: '营业收入（不含税）（元）',
      dataIndex: 'incomeWithoutTax',
      width: 220,
    }),
    AmountColumn({
      title: '营业成本（元）',
      dataIndex: 'costWithoutTax',
      width: 220,
    }),
    AmountEditableRow({
      title: '增值税',
      dataIndex: 'valueAddedTax',
      width: 120,
    }),
    AmountEditableRow({
      title: '印花税',
      dataIndex: 'stampTax',
      width: 120,
    }),
    AmountEditableRow({
      title: '附加税',
      dataIndex: 'additionalTax',
      width: 120,
    }),
    AmountEditableRow({
      title: isYear ? '当年平均资金占用额（元）' : '当月平均资金占用额（元）',
      dataIndex: 'fundOccupyAverage',
      width: 200,
    }),
    AmountEditableRow({
      title: '风险准备金',
      dataIndex: 'riskFundDiff',
      width: 160,
    }),
    AmountEditableRow({
      title: '考核利润',
      dataIndex: 'assessmentProfit',
      width: 120,
    }),
    AmountEditableRow({
      title: isYear ? '本年末资产余额（元）' : '本月末资产余额（元）',
      dataIndex: 'endOfThisPeriodBalance',
      width: 200,
    }),
    isFinancial && {
      title: '操作',
      dataIndex: 'action',
      width: 120,
      fixed: 'right',
      actions: (record, rowIndex) => {
        if (!record.id) return false
        return [
          editIndex !== rowIndex && {
            name: '编辑',
            key: 'edit',
            onClick: () => store.editItem({ rowIndex }),
          },
          editIndex === rowIndex && {
            name: '取消',
            key: 'cancel',
            onClick: () => store.cancelEdit({ rowIndex }),
          },
          editIndex === rowIndex && {
            name: '确定',
            key: 'confirm',
            onClick: () => store.confirmEdit({ record, rowIndex }),
          },
        ]
      },
    },
  ]

  return (
    <>
      <div className="z-sub-title">公司新增业务</div>
      <Table
        resizable
        columns={columns}
        editable={isFinancial}
        store={store.newBusinessList}
        columnWidth={120}
        scroll={{ x: 2400, y: 550 }}
      />
    </>
  )
})
const BudgetDetail = ({ store }) => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <RenderBusinessBalance store={store} />
      <RenderNewBusiness store={store} />
    </div>
  )
}

export default observer(BudgetDetail)
