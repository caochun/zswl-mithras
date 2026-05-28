import { hasValue, amountFormat, formatPercent } from '@/utils'
import { FiledFormat } from '@/components/Format'
import { Input } from 'antd'

const ALL_COLUMNS = ({}) => {
  return [
    {
      title: '客户名称',
      dataIndex: 'clientId',
      width: 220,
      render: (val, { clientName }) => <FiledFormat title={clientName} />,
    },
    {
      title: '业务类型',
      dataIndex: 'bizType',
      width: 100,
    },
    {
      title: '项目类型',
      dataIndex: 'projectClassify',
    },
    {
      title: '业务部门',
      dataIndex: 'bizDeptId',
      render: (val, { bizDeptName }) => <FiledFormat title={bizDeptName} />,
      width: 140,
    },
    {
      title: '合同编号',
      dataIndex: 'contractCode',
      width: 280,
      editable: {
        element: <Input style={{ width: 250 }} allowClear></Input>,
      },
    },
    {
      title: '投放时间',
      dataIndex: 'contractStartDate',
    },
    {
      title: '当年已确认收入(税后)',
      dataIndex: 'confirmIncomeThisYear',
      width: 200,
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      },
    },
    {
      title: '当年测算利息收入(税后)',
      dataIndex: 'calculateInterestThisYear',
      width: 200,
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      },
    },
    {
      title: '营业收入',
      dataIndex: 'operatingIncome',
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      },
    },
    {
      title: 'FTP成本',
      dataIndex: 'ftpInterest',
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      },
    },

    {
      title: '上期末风险金余额',
      dataIndex: 'riskBalanceEndOfLastYear',
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      },
    },
    {
      title: '本期末风险金余额',
      dataIndex: 'riskBalanceEndOfThisYear',
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      },
    },
    {
      title: '本年风险金计提/转回',
      dataIndex: 'riskUsedThisYear',
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      },
    },
    {
      title: '附加税',
      dataIndex: 'additionalTax',
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      },
    },
    {
      title: '利润总额',
      dataIndex: 'profit',
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      },
    },
    {
      title: '利润总额(扣除费用)',
      width: 180,
      dataIndex: 'profitExcludeFee',
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      },
    },
    {
      title: '本年末剩余本金',
      dataIndex: 'remainingPrincipleEndOfThisYear',
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      },
    },
    {
      title: '本年末保证金余额',
      dataIndex: 'remainingEarnestEndOfThisYear',

      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      },
    },
    {
      title: '年末敞口',
      dataIndex: 'riskExposureEndOfThisYear',
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      },
    },
  ]
}

export default ALL_COLUMNS
