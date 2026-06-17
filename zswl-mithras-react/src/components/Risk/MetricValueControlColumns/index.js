import { AmountEditable, InputNumberEditable, formatAmountWan } from '@/components/Format'
import { Input, DatePicker } from 'antd'
import { hasValue } from '@/utils'
import { Select } from '@zswl/components'
import moment from 'moment'
import { disabledDate } from './util'

const ALL_COLUMNS = [
  {
    title: '数据时点',
    dataIndex: 'dataMonth',
    editable: (record) => {
      return {
        element: <DatePicker picker={'month'} allowClear={false} disabledDate={disabledDate} />,
      }
    },
    itemProps: {
      transform: (val) => {
        return {
          dataMonth: val ? moment(val).format('yyyy-MM') + '-01' : undefined,
        }
      },
    },
    render: (val) => val,
  },
  {
    title: '业务类型',
    dataIndex: 'bizType',
    requiredMark: true,
    matchOption: 'jzdReportBizType',
    editable: (record) => {
      return {
        element: <Select options="jzdReportBizType" />,
        rules: [{ required: true, message: '请输入' }],
      }
    },
  },
  {
    title: '创建类型',
    dataIndex: 'createType',
    requiredMark: true,
    matchOption: 'jzdReportCreateType',
    editable: (record) => {
      return {
        element: <Select options="jzdReportCreateType" />,
        rules: [{ required: true, message: '请输入' }],
      }
    },
  },
  {
    title: '标的物名称',
    dataIndex: 'targetSubject',
    editable: (record) => {
      return {
        element: <Input />,
        rules: [{ required: true, message: '请输入' }],
      }
    },
  },
  {
    title: '业务总额(万元)',
    dataIndex: 'bizAmountTotal',
    align: 'right',
    editable: (record, rowIndex) => {
      return AmountEditable(record, 'bizAmountTotal', {
        required: true,
        disabled: false,
      })
    },
    render: (value) => {
      return hasValue(value) ? formatAmountWan(value) : '-'
    },
  },
  {
    title: '业务余额(万元)',
    dataIndex: 'bizAmountLeft',
    align: 'right',
    editable: (record, rowIndex) => {
      return AmountEditable(record, 'bizAmountLeft', {
        required: true,
        disabled: false,
      })
    },
    render: (value) => {
      return hasValue(value) ? formatAmountWan(value) : '-'
    },
  },
  {
    title: '客户名称',
    dataIndex: 'clientName',
    editable: (record) => {
      return {
        element: <Input />,
        rules: [{ required: true, message: '请输入' }],
      }
    },
    width: 120,
  },
  {
    title: '是否同业客户',
    dataIndex: 'clientSameTrade',
    width: 120,
    matchOption: 'yesOrNo',
    editable: (record) => {
      return {
        element: <Select options="yesOrNo" />,
        rules: [{ required: true, message: '请选择' }],
      }
    },
  },
  {
    title: '企业经济成分',
    dataIndex: 'economicComposition',
    width: 120,
    matchOption: 'jzdReportEconomyComposition',
    editable: (record) => {
      return {
        element: <Select options="jzdReportEconomyComposition" />,
        rules: [{ required: true, message: '请选择' }],
      }
    },
  },
  {
    title: '主办业务部门',
    dataIndex: 'sponsorOrgName',
    width: 120,
    editable: (record) => {
      return {
        element: <Input />,
        rules: [{ required: true, message: '请选择' }],
      }
    },
  },
  {
    title: '业务起始日期',
    dataIndex: 'bizStartDate',
    width: 120,
    dateFormat: 'yyyy-MM-DD',
    editable: (record) => {
      return {
        element: <DatePicker picker={'month'} allowClear={false} />,
      }
    },
    itemProps: {
      transform: (val) => {
        return {
          bizStartDate: val ? moment(val).format('yyyy-MM-DD') : undefined,
        }
      },
    },
  },
  {
    title: '业务到期日',
    dateFormat: 'yyyy-MM-DD',
    dataIndex: 'bizEndDate',
    width: 120,
    editable: (record) => {
      return {
        element: <DatePicker picker={'month'} allowClear={false} />,
      }
    },
    itemProps: {
      transform: (val) => {
        return {
          bizEndDate: val ? moment(val).format('yyyy-MM-DD') : undefined,
        }
      },
    },
  },
  {
    title: '合同保证价值(万元)',
    dataIndex: 'ensureValue',
    align: 'right',
    editable: (record, rowIndex) => {
      return AmountEditable(record, 'ensureValue', {
        required: true,
        disabled: false,
      })
    },
    render: (value) => {
      return hasValue(value) ? formatAmountWan(value) : '-'
    },
  },
  {
    title: '担保人名称',
    dataIndex: 'guaranteeName',
    width: 120,
    editable: (record) => {
      return {
        element: <Input />,
        rules: [{ required: true, message: '请输入' }],
      }
    },
  },
  {
    title: '已计提减值(万元)',
    dataIndex: 'yjtjzValue',
    align: 'right',
    editable: (record, rowIndex) => {
      return AmountEditable(record, 'ensureValue', {
        required: true,
        disabled: false,
      })
    },
    render: (value) => {
      return hasValue(value) ? formatAmountWan(value) : '-'
    },
  },
  {
    title: '逾期天数',
    dataIndex: 'overdueDays',
    editable: (val) => InputNumberEditable(),
    width: 120,
  },
  {
    title: '逾期金额',
    dataIndex: 'overdueValue',
    align: 'right',
    editable: (record, rowIndex) => {
      return AmountEditable(record, 'ensureValue', {
        required: true,
        disabled: false,
      })
    },
    render: (value) => {
      return hasValue(value) ? formatAmountWan(value) : '-'
    },
  },
  {
    title: '资产质量分类',
    dataIndex: 'assetsCategory',
    width: 120,
    matchOption: 'jzdReportAssetsCategory',
    editable: (record) => {
      return {
        element: <Select options="jzdReportAssetsCategory" />,
        rules: [{ required: true, message: '请选择' }],
      }
    },
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 120,
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
    width: 120,
  },
]
export default ALL_COLUMNS
