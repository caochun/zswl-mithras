import { AmountEditable } from '@/components/Format'
import { AmountFormat } from '@/pages/creditManage/creditTable/Tab/config'
import AmountRange from '@/components/AmountRange'
import { hasValue, amountFormat, formatPercent } from '@/utils'
import { Select, App } from '@zswl/components'
import { Tag } from 'antd'
import { ApiSelect } from '@/components'
import Api from '@/api/risk/metricValue/controlGliy'

const reportStatusEnum = {
  REPORTED: '#87d068',
  NOT_REPORT: '#f50',
}
const transformResult = (data) => {
  const res = []
  data.map((item) => {
    res.push({
      label: item,
      value: item,
    })
  })
  return res
}

const ALL_COLUMNS = [
  {
    title: '交易对手上一年末审计净资产(万)',
    dataIndex: 'tradePartyAssets',
    width: 240,
    align: 'right',
    editable: (record, rowIndex) => {
      return AmountEditable(record, 'tradePartyAssets', {
        required: true,
        disabled: false,
      })
    },
    render: (value) => {
      return hasValue(value) ? AmountFormat(value) : '-'
    },
  },
  {
    title: '交易日期',
    width: 180,
    dataIndex: 'tradeDate',
    type: 'rangePicker',
    dateFormat: 'yyyy-MM-DD',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          tradeDate: undefined,
          tradeDateFrom: startDataTime?.format('yyyy-MM-DD'),
          tradeDateTo: endDataTime?.format('yyyy-MM-DD'),
        }
      },
    },
  },
  {
    title: '交易金额',
    dataIndex: 'amount',
    requiredMark: true,
    width: 160,
    itemProps: {
      transform: (val) => {
        const [start, end] = val || []
        return {
          amount: undefined,
          amountFrom: start && start * 10000,
          amountTo: end && end * 10000,
        }
      },
    },
    render: (val) => amountFormat(formatPercent(val)),
    editable: {
      element: <AmountRange />,
    },
  },
  {
    title: '关联交易级别',
    dataIndex: 'level',
    matchOption: 'gljyReportLevel',
    editable: (record) => {
      return {
        element: <Select options="gljyReportLevel" />,
        rules: [{ required: true, message: '请选择' }],
      }
    },
  },
  {
    title: '交易对手名称',
    dataIndex: 'tradePartyName',
    width: 220,
    editable: (record) => {
      return {
        element: (
          <ApiSelect
            api={Api.postRelatedClients}
            transformResult={transformResult}
            searchField="name"
            debounceSearch
          ></ApiSelect>
        ),
        rules: [{ required: true, message: '请选择' }],
      }
    },
  },
  {
    title: '主体机构名称',
    dataIndex: 'subjectPartyName',
    width: 200,
  },
  {
    title: '报送状态',
    dataIndex: 'reportStatus',
    matchOption: 'gljyReportStatus',
    editable: (record) => {
      return {
        element: <Select options="gljyReportStatus" />,
        rules: [{ required: true, message: '请选择' }],
      }
    },
    render: (val) => {
      return (
        <Tag color={reportStatusEnum[val]}>{App.matchOption('gljyReportStatus', val)?.label}</Tag>
      )
    },
    width: 120,
  },
  {
    title: '交易描述',
    dataIndex: 'description',
    width: 200,
  },
  {
    title: '重大交易原因',
    dataIndex: 'importantReason',
    width: 220,
    matchOption: 'gljyReportImportantReason',
    editable: (record) => {
      return {
        element: <Select options="gljyReportImportantReason" allowClear />,
        rules: [{ required: true, message: '请选择' }],
      }
    },
  },
  {
    title: '董事会/委员会意见',
    dataIndex: 'opinion',
    width: 200,
  },
  {
    title: '交易目的',
    dataIndex: 'purpose',
  },
  {
    title: '风险/影响',
    dataIndex: 'risk',
  },
  {
    title: '一级分类',
    dataIndex: 'tradeCategoryParentName',
    matchOption: 'gljyReportCategoryOne',
    editable: (record) => {
      return {
        element: <Select options="gljyReportCategoryOne" />,
        rules: [{ required: true, message: '请选择' }],
      }
    },
  },
  {
    title: '二级分类',
    dataIndex: 'tradeCategoryName',
    matchOption: 'gljyReportCategoryTwo',
    editable: (record) => {
      return {
        element: <Select options="gljyReportCategoryTwo" />,
        rules: [{ required: true, message: '请选择' }],
      }
    },
  },
]
export default ALL_COLUMNS
