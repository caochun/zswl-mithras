/**
 * 应收逾期集成结算表列配置
 */
import { AmountColumn, DateColumn, InputColumn, MatchOptionColumn } from '@/components/Format'
import { App } from '@zswl/components'
import { Space } from 'antd'

const initFormat = 1

export const OverdueColumns = [
  InputColumn({
    title: '收款计划编号',
    dataIndex: 'collectionCode',
    width: 150,
    search: true,
  }),
  MatchOptionColumn({
    title: '单据状态',
    dataIndex: 'recordStatus',
    matchOption: 'overdueRecordStatueEnum',
    search: true,
    render: (val) => {
      const colorMap = {
        REPORT_SUCCESS: 'green',
        NOT_REPORT: 'orange',
        REPORT_FAIL: 'red',
      }
      const text = App.matchOption('overdueRecordStatueEnum', val).label
      return (
        <Space>
          <span
            style={{
              display: 'inline-block',
              width: '8px',
              height: '8px',
              borderRadius: '50%',
              background: colorMap[val],
            }}
          />
          <span>{text}</span>
        </Space>
      )
    },
  }),
  InputColumn({
    title: '现金流编号',
    dataIndex: 'collectionCode',
    width: 200,
  }),
  InputColumn({
    title: '合同编号',
    dataIndex: 'contractCode',
    width: 300,
    search: true,
  }),
  InputColumn({
    title: '项目名称',
    dataIndex: 'projName',
    width: 200,
  }),

  DateColumn({
    title: '单据日期',
    dataIndex: 'recordBillDate',
    search: true,
  }),
  DateColumn({
    title: '结算日期',
    dataIndex: 'settlementDate',
    search: true,
  }),
  DateColumn({
    title: '结算记录的凭证记账日期',
    dataIndex: 'voucherAccountDate',
    width: 180,
  }),
  MatchOptionColumn({
    title: '结算关系',
    dataIndex: 'settlementRelation',
    matchOption: 'overdueSettlementRelationEnum',
  }),
  AmountColumn({
    title: '结算金额（元）',
    dataIndex: 'settlementAmount',
    width: 140,
    initFormat,
  }),
  InputColumn({ title: '客户名称', dataIndex: 'clientName', search: true, width: 200 }),
  MatchOptionColumn({
    title: '审批状态',
    dataIndex: 'approvalStatus',
    matchOption: 'projProcessStatus',
  }),
  MatchOptionColumn({ title: '国有类型', dataIndex: 'ownedType' }),
  InputColumn({ title: '实控人', dataIndex: 'actualController', width: 150 }),
  MatchOptionColumn({
    title: '款项内容',
    dataIndex: 'paymentNumber',
    matchOption: 'overduePaymentNumberEnum',
    width: 120,
  }),
  DateColumn({ title: '单据账龄起算日', dataIndex: 'recordStartDate', width: 130, search: true }),
  InputColumn({ title: '科目', dataIndex: 'accounttypeNumber', width: 120 }),
  DateColumn({ title: '约定收款日期', dataIndex: 'recordDueDate', width: 130 }),
  InputColumn({ title: '约定收款条件', dataIndex: 'recordPaymentTerms', width: 150 }),
  AmountColumn({ title: '应收金额（元）', dataIndex: 'receAmount', width: 130, initFormat }),
  InputColumn({ title: '行业政策收款周期', dataIndex: 'collectionCycle', width: 150 }),
]

export default OverdueColumns
