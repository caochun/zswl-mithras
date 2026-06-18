import { DatePicker } from 'antd'
import moment from 'moment'
import { PureAmountFormat } from '@/components/Format'

const ALL_COLUMNS = [
  {
    title: '租后检查部门',
    dataIndex: 'deptName',
    editable: false,
  },
  {
    title: '客户主办',
    dataIndex: 'sponsorUserName',
    editable: false,
  },
  {
    title: '客户名称',
    dataIndex: 'clientName',
    editable: false,
  },
  {
    title: '检查日期',
    dataIndex: 'inspectionDate',
    requiredMark: true,
    dateFormat: 'yyyy-MM-DD',
    editable: (record) => {
      return {
        element: <DatePicker style={{ width: '100%' }} />,
        rules: [{ required: true, message: '请选择' }],
        transform: (value) => {
          return {
            inspectionDate: value && moment(value).format('yyyy-MM-DD'),
          }
        },
      }
    },
  },
  {
    title: '行业',
    dataIndex: 'industryType',
    editable: false,
  },
  {
    title: '风险敞口余额',
    dataIndex: 'riskExposure',
    editable: false,
    render: PureAmountFormat,
  },
  {
    title: '合同金额',
    dataIndex: 'contractTotalAmount',
    editable: false,
    render: PureAmountFormat,
  },
  {
    title: '合同最终到期日',
    dataIndex: 'deadline',
    editable: false,
  },
  {
    title: '下次还款日期',
    dataIndex: 'nextRepayDate',
    editable: false,
  },
  {
    title: '下次还款金额',
    dataIndex: 'nextRepayAmount',
    editable: false,
    render: PureAmountFormat,
  },
]
export default ALL_COLUMNS
