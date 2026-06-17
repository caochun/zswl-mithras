import { Input, DatePicker } from 'antd'
import { FiledFormat } from '@/components/Format'
import { Select, App } from '@zswl/components'
import { formatPercent, amountFormat, hasValue, timeFormat } from '@/utils'

const ALL_COLUMNS = [
  {
    title: '客户名称',
    dataIndex: 'clientName',
    width: 300,
    editable: {
      element: <Input />,
    },
    render: (val) => {
      return <FiledFormat title={val} />
    },
  },
  {
    title: '集团名称',
    dataIndex: 'groupName',
    width: 280,
    editable: {
      element: <Input />,
    },
    render: (val) => {
      return <FiledFormat title={val} />
    },
  },
  {
    title: '所属集团',
    dataIndex: 'groupName',
    width: 300,
    editable: {
      element: <Input />,
    },
    // render: (val) => <FiledFormat title={val} />,
  },
  {
    title: '预警状态',
    dataIndex: 'state',
    matchOption: 'alertState',
    render: (val) => {
      return val ? (
        <span style={val === 'OVER' ? { color: 'red' } : {}}>
          {App.matchOption('alertState', val).label}
        </span>
      ) : (
        '-'
      )
    },
  },
  {
    title: '浙江省内/集团协同业务',
    dataIndex: 'zhejiangInnerGroup',
    width: 220,
    matchOption: 'yesOrNo',
    editable: {
      element: <Select options="yesOrNo"></Select>,
    },
  },
  {
    title: '是否金控关联方',
    dataIndex: 'jinKon',
    width: 220,
    matchOption: 'yesOrNo',
    editable: {
      element: <Select options="yesOrNo"></Select>,
    },
  },
  {
    title: '数据时点',
    dataIndex: 'dataTimePoint',
    editable: {
      element: <DatePicker picker="month" allowClear={false} />,
    },
    itemProps: {
      transform: (val) => (val ? timeFormat(val) : undefined),
    },
  },
  {
    title: '剩余未还本金(元)',
    width: 200,
    dataIndex: 'remainingPrincipal',
    align: 'right',
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '保证金余额(元)',
    width: 200,
    dataIndex: 'remainingMargin',
    align: 'right',
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '存量敞口(元)',
    width: 200,
    dataIndex: 'stockValue',
    align: 'right',
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '集中度占比',
    dataIndex: 'concentrationRatio',
    align: 'right',
    render: (val) => {
      return hasValue(val) ? val / 100 + '%' : '-'
    },
  },
  {
    title: '不良余额(元)',
    dataIndex: 'badBalance',
    align: 'right',
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '不良余额占比',
    dataIndex: 'badBalanceRatio',
    align: 'right',
    render: (val) => {
      return hasValue(val) ? val / 100 + '%' : '-'
    },
  },
]
export default ALL_COLUMNS
