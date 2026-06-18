import { Input } from 'antd'

const { TextArea } = Input

const ALL_COLUMNS = [
  {
    title: '风险信号及重大事项、风险防范措施',
    dataIndex: 'preventiveMeasures',
    span: 2,
    editable: (record) => {
      return {
        element: <TextArea />,
      }
    },
  },
  {
    title: '查询分析及查询结论',
    dataIndex: 'queryConclusion',
    span: 2,
    editable: (record) => {
      return {
        element: <TextArea />,
      }
    },
  },
]
export default ALL_COLUMNS
