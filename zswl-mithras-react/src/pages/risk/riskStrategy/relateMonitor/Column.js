import { Input } from 'antd'
import { FiledFormat } from '@/components/Format'
import AmountRange from '@/components/AmountRange'
import { Select } from '@zswl/components'
import { formatPercent, amountFormat, getKeyOptionsLabelMap } from '@/utils'

const ALL_COLUMNS = (pullSelect) => [
  {
    title: '关联方名称',
    dataIndex: 'clientName',
    width: 220,
    editable: {
      element: <Input></Input>,
    },
    render: (val) => {
      return <FiledFormat title={val} />
    },
  },
  {
    title: '合同编号',
    dataIndex: 'contractCode',
    width: 280,
  },
  {
    title: '关联方关系类型',
    dataIndex: 'relatedPartyType',
    width: 280,
    editable: {
      element: <Select options={pullSelect.relatedPartyType}></Select>,
    },
    render: (v) => {
      return getKeyOptionsLabelMap('relatedPartyType', pullSelect)[v] || '-'
    },
  },
  {
    title: '关联关系说明',
    dataIndex: 'description',
    width: 250,
  },
  {
    title: '交易金额',
    dataIndex: 'transactionAmount',
    width: 150,
    itemProps: {
      transform: (val) => {
        const [start, end] = val || []
        return {
          transactionAmount: undefined,
          transactionAmountFrom: start && start * 10000,
          transactionAmountTo: end && end * 10000,
        }
      },
    },
    editable: {
      element: <AmountRange />,
    },
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '现金流编号',
    dataIndex: 'cashFlowCode',
  },
  {
    title: '交易日期',
    width: 180,
    dataIndex: 'transactionDate',
    type: 'rangePicker',
    dateFormat: 'yyyy-MM-DD',
    itemProps: {
      transform: (val) => {
        const [transactionDateFrom, transactionDateTo] = val || []
        return {
          transactionDate: undefined,
          transactionDateFrom: transactionDateFrom?.format('yyyy-MM-DD'),
          transactionDateTo: transactionDateTo?.format('yyyy-MM-DD'),
        }
      },
    },
  },
]
export default ALL_COLUMNS
