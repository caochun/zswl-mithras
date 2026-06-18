import { FiledFormat } from '@/components/Format'

const ALL_COLUMNS = [
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
]
export default ALL_COLUMNS
