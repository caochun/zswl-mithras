import { FiledFormat } from '@/components/Format'
import { formatPercent, amountFormat } from '@/utils'

const ALL_COLUMNS = [
  {
    title: '序号',
    dataIndex: 'sequence',
    width: 80,
    render: (val) => <FiledFormat value={val} />,
  },
  {
    title: '租赁物类型',
    dataIndex: 'category',
    render: (val) => <FiledFormat value={val} />,
  },
  {
    title: '识别号类型',
    dataIndex: 'uniqueIdentifyCodeType',
    render: (val) => <FiledFormat value={val} />,
  },
  {
    title: '租赁物名称',
    dataIndex: 'name',
    render: (val) => <FiledFormat value={val} />,
  },
  {
    title: '唯一识别号',
    dataIndex: 'uniqueIdentifyCode',
    render: (val) => <FiledFormat value={val} />,
  },
  {
    title: '供应商',
    dataIndex: 'supplier',
    render: (val) => <FiledFormat value={val} />,
  },

  {
    title: '数量',
    dataIndex: 'quantity',
    render: (val) => <FiledFormat value={val} />,
  },
  {
    title: '计量单位',
    dataIndex: 'unit',
    render: (val) => <FiledFormat value={val} />,
  },
  {
    title: '购置日期',
    dataIndex: 'purchaseDate',
    render: (val) => <FiledFormat value={val} />,
  },
  {
    title: '账面原值(元)',
    dataIndex: 'originalBookValue',
    align: 'right',
    render: (val) => {
      return (
        <FiledFormat
          value={amountFormat(formatPercent(val?.value ?? val))}
          isChange={val?.isChange}
        />
      )
    },
  },
  {
    title: '评估原值(元)',
    dataIndex: 'assessedValue',
    align: 'right',
    render: (val) => (
      <FiledFormat
        value={amountFormat(formatPercent(val?.value ?? val))}
        isChange={val?.isChange}
      />
    ),
  },
  {
    title: '账面净值(元)',
    dataIndex: 'originalBookNetValue',
    align: 'right',
    render: (val) => (
      <FiledFormat
        value={amountFormat(formatPercent(val?.value ?? val))}
        isChange={val?.isChange}
      />
    ),
  },
  {
    title: '评估净值(元)',
    dataIndex: 'assessedNetValue',
    align: 'right',
    render: (val) => (
      <FiledFormat
        value={amountFormat(formatPercent(val?.value ?? val))}
        isChange={val?.isChange}
      />
    ),
  },
  {
    title: '发票号',
    dataIndex: 'invoiceCode',
    render: (val) => <FiledFormat value={val} />,
  },
  {
    title: '存放地点',
    dataIndex: 'storagePlace',
    render: (val) => <FiledFormat value={val} />,
  },
]
export default ALL_COLUMNS
