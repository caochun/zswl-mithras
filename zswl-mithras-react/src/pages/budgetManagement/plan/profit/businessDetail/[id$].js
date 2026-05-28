import { observer } from '@zswl/admin'
import { Page, Table } from '@zswl/components'
import {
  AmountColumn,
  AmountEditable,
  DateColumn,
  InputColumn,
  TextAreaColumn,
  TextAreaEditable,
} from '@/components/Format'
import TableExport from '@/components/Actions/TableExport'
import Store from './store'
import { useMemo } from 'react'
import { isFinancialManager, isFinancialOfficer } from '@/utils'

const BudgetDetail = ({ params, query }) => {
  const { id } = params
  const { belongDeptId, isYear } = query

  const isYearText = isYear === 'true' ? '年' : '月'
  const store = useMemo(() => new Store(), [])
  const { editIndex } = store
  const isFinancial = isFinancialManager() || isFinancialOfficer()
  const AmountEditableRow = ({ title, dataIndex, initFormat, suffix, required, canEdit }) => {
    return AmountColumn({
      title,
      dataIndex,
      initFormat,
      suffix,
      required,
      editable: (record, rowIndex) =>
        editIndex === rowIndex && canEdit
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
    InputColumn({ title: '部门名称', dataIndex: 'belongDeptName' }),
    InputColumn({
      title: '客户名称',
      dataIndex: 'clientName',
      search: true,
      width: 160,
    }),
    InputColumn({ title: '项目主办', dataIndex: 'sponsorUserName', width: 160 }),
    InputColumn({ title: '合同编号', dataIndex: 'contractCode', width: 160 }),
    InputColumn({ title: '借据编号', dataIndex: 'receiptCode', width: 160 }),
    InputColumn({ title: '业务类型', dataIndex: 'leaseTypeDisplay' }),
    DateColumn({ title: '起租日', dataIndex: 'payDate' }),
    InputColumn({ title: 'FTP行业分类', dataIndex: 'ftpIndustryCategoryDisplay' }),
    AmountEditableRow({
      canEdit: isFinancial,
      title: `上${isYearText}末业务余额（元）`,
      dataIndex: 'endOfLastPeriodBalance',
      width: 160,
    }),
    AmountEditableRow({
      canEdit: isFinancial,
      title: `本${isYearText}末业务余额（元）`,
      dataIndex: 'endOfThisPeriodBalance',
      width: 160,
    }),
    AmountEditableRow({
      canEdit: isFinancial,
      title: `本${isYearText}累计收入（不含税）（元）`,
      dataIndex: 'incomeWithoutTax',
      width: 160,
    }),
    AmountEditableRow({
      canEdit: isFinancial,
      title: `本${isYearText}资金成本/经营租赁成本（元）`,
      dataIndex: 'cost',
      width: 160,
    }),
    AmountEditableRow({
      canEdit: isFinancial,
      title: `本${isYearText}累计毛利（元）`,
      dataIndex: 'grossProfit',
      width: 160,
    }),
    AmountEditableRow({
      canEdit: isFinancial,
      title: `${isYearText}末风险金余额（元）`,
      dataIndex: 'endOfThisPeriodRiskFund',
      width: 160,
    }),
    AmountEditableRow({
      canEdit: isFinancial,
      title: `${isYearText}初风险金余额（元）`,
      dataIndex: 'endOfLastPeriodRiskFund',
      width: 160,
    }),
    AmountEditableRow({
      canEdit: isFinancial,
      title: '累计风险金（元）',
      dataIndex: 'riskFundDiff',
      width: 160,
    }),
    AmountEditableRow({
      canEdit: isFinancial,
      title: `本${isYearText}累计差价（元）`,
      dataIndex: 'diff',
      width: 160,
    }),
    AmountEditableRow({
      canEdit: isFinancial,
      title: '附加税（元）',
      dataIndex: 'additionalTax',
    }),
    AmountEditableRow({
      canEdit: isFinancial,
      title: '印花税（元）',
      dataIndex: 'stampTax',
    }),
    AmountEditableRow({
      canEdit: isFinancial,
      title: '项目累计利润总额（元）',
      dataIndex: 'assessmentProfit',
      width: 160,
    }),
    AmountEditableRow({
      canEdit: isFinancial,
      title: '扣费后项目累计利润总额（元）',
      dataIndex: 'assessmentProfitWithoutExpense',
      width: 160,
    }),
    AmountEditableRow({
      canEdit: isFinancial,
      title: '项目累计利润总额（非负值）（元）',
      dataIndex: 'assessmentProfitOriginal',
      width: 160,
    }),
    AmountEditableRow({
      canEdit: isFinancial,
      title: '扣费后项目累计利润总额（非负值）（元）',
      dataIndex: 'assessmentProfitWithoutExpenseOriginal',
      width: 180,
    }),
    AmountEditableRow({
      title: '项目利润调整项',
      dataIndex: 'profitAdjust',
      width: 160,
      canEdit: true,
    }),
    TextAreaColumn({
      title: '备注说明',
      dataIndex: 'remark',
      width: 200,
      editable: (record, rowIndex) =>
        editIndex === rowIndex ? TextAreaEditable(record, 'remark') : false,
    }),

    {
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
    <Page
      extra={[<TableExport table={store.table} otherExcelProps={{ fileName: '存量业务明细' }} />]}
      params={{
        belongDeptId,
        budgetPlanProfitId: id,
      }}
      store={store}
    >
      <Table
        columns={columns}
        store={store.table}
        columnWidth={120}
        scroll={{ x: 'max-content' }}
      />
    </Page>
  )
}

export default observer(BudgetDetail)
