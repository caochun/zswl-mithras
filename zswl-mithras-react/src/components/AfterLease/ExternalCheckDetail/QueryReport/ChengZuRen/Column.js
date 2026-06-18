import { Input, DatePicker } from 'antd'

const { TextArea } = Input

const { RangePicker } = DatePicker

const ALL_COLUMNS = [
  {
    title: '客户名称',
    dataIndex: 'clientName',
    editable: false,
  },
  {
    title: '查询区间',
    dataIndex: 'queryTime',
    requiredMark: true,
    editable: (record) => {
      return {
        element: <RangePicker allowClear />,
        rules: [{ required: true, message: '请选择' }],
        transform: (val) => {
          const [startDate, endDate] = val || []
          return {
            queryTime: undefined,
            queryTimeFrom: startDate?.format('yyyy-MM-DD'),
            queryTimeTo: endDate?.format('yyyy-MM-DD'),
          }
        },
      }
    },
    type: 'rangePicker',
    dateFormat: 'yyyy-MM-DD',
    render: (value, record) => {
      const { queryTimeFrom, queryTimeTo } = record
      return queryTimeFrom ? `${queryTimeFrom} ~ ${queryTimeTo}` : '-'
    },
  },
  {
    title: '1.全国企业信用信息公示系统查询',
    dataIndex: 'creditInfo',
    span: 2,
    editable: (record) => {
      return {
        element: <TextArea />,
      }
    },
  },
  {
    title: '2.全国法院被执行人或被纳入失信人信息查询',
    dataIndex: 'courtInfo',
    span: 2,
    editable: (record) => {
      return {
        element: <TextArea />,
      }
    },
  },
  {
    title: '3.裁判文书网',
    dataIndex: 'refereeNetworkInfo',
    span: 2,
    editable: (record) => {
      return {
        element: <TextArea />,
      }
    },
  },
  {
    title: '4.中登网登记及抵押登记',
    dataIndex: 'zhongdengInfo',
    span: 2,
    editable: (record) => {
      return {
        element: <TextArea />,
      }
    },
  },
  {
    title: '5.信用报告(每半年查询一次)',
    dataIndex: 'creditReport',
    span: 2,
    editable: (record) => {
      return {
        element: <TextArea />,
      }
    },
  },
  {
    title: '6.其它(如有)',
    dataIndex: 'other',
    span: 2,
    editable: (record) => {
      return {
        element: <TextArea />,
      }
    },
  },
]
export default ALL_COLUMNS
