import { ClientSelect, FounderSelect } from '@/components'
import {
  FiledFormat,
  DatePickerEditable,
  AmountEditable,
  TextAreaEditable,
  MatchFormat,
  AmountColumn,
} from '@/components/Format'
import { Input } from 'antd'
import { Select } from '@zswl/components'
import { formatPercent, amountFormat } from '@/utils'

const ALL_COLUMNS = [
  {
    title: '保险单号',
    requiredMark: true,
    width: 200,
    dataIndex: 'policyCode',
    editable: {
      element: <Input />,
      rules: [{ required: true, message: '请输入' }],
    },
    actions({ policyCode, id, dataSource }) {
      return [
        {
          name: policyCode,
          to: `/afterLease/policyManage/detail/${id}?dataSource=${dataSource}`,
        },
      ]
    },
  },
  {
    title: '合同编号',
    dataIndex: 'contractCode',
    width: 280,
  },
  {
    title: '项目名称',
    dataIndex: 'projName',
    width: 300,
  },
  {
    title: '是否续保',
    dataIndex: 'renewInsuranceFlag',
    requiredMark: true,
    matchOption: 'policyRenewInsuranceEnum',
    editable: {
      element: <Select options="policyRenewInsuranceEnum" />,
      rules: [{ required: true, message: '请选择' }],
    },
    render: (val) => <MatchFormat value={val} matchOption="policyRenewInsuranceEnum" />,
  },
  {
    title: '保险机构',
    requiredMark: true,
    dataIndex: 'insuranceCompany',
    width: 250,
    editable: {
      element: <Input />,
      rules: [{ required: true, message: '请输入' }],
    },
    // render: (val) => <FiledFormat value={val} />,
    render: (v) => v,
  },
  {
    title: '保险起始日',
    width: 180,
    dataIndex: 'insuranceStartDate',
    type: 'rangePicker',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          insuranceStartDate: undefined,
          insuranceStartDateFrom: startDataTime?.format('yyyy-MM-DD'),
          insuranceStartDateTo: endDataTime?.format('yyyy-MM-DD'),
        }
      },
    },
    render: (val) => <FiledFormat value={val} />,
  },
  {
    title: '保险到期日',
    width: 180,
    dataIndex: 'insuranceEndDate',
    type: 'rangePicker',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          insuranceEndDate: undefined,
          insuranceEndDateFrom: startDataTime?.format('yyyy-MM-DD'),
          insuranceEndDateTo: endDataTime?.format('yyyy-MM-DD'),
        }
      },
    },
    render: (val) => <FiledFormat value={val} />,
  },

  {
    title: '项目主办',
    dataIndex: 'projSponsorUserId',
    width: 130,
    render: (val, { projSponsorUserName }) => <FiledFormat title={projSponsorUserName} />,
    editable: {
      element: <FounderSelect />,
      functionCode: 'policyManageFounderList',
    },
  },
  {
    title: '项目协办',
    dataIndex: 'projCosponsorUserId',
    width: 130,
    editable: (value) => ({
      element: <FounderSelect />,
      functionCode: 'policyManageFounderList',
      initialValue: (value?.projCosponsorUserNames || [])?.join('、'),
    }),
    render: (val, { projCosponsorUserNames }) => (
      <FiledFormat title={projCosponsorUserNames?.join(',')} />
    ),
  },
  {
    title: '客户名称',
    dataIndex: 'clientId',
    width: 300,
    editable: {
      element: <ClientSelect canJump={false} />,
      functionCode: 'policyClientList',
    },
    render: (val, { clientName }) => <FiledFormat title={clientName} />,
  },
  {
    title: '状态',
    dataIndex: 'policyStatus',
    matchOption: 'policyStatusEnum',
    width: 160,
  },
  {
    title: '创建时间',
    width: 180,
    dataIndex: 'createTime',
    type: 'rangePicker',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          createTime: undefined,
          createTimeFrom: startDataTime?.format('yyyy-MM-DD 00:00:00'),
          createTimeTo: endDataTime?.format('yyyy-MM-DD 23:59:59'),
        }
      },
    },
  },
  {
    title: '更新时间',
    width: 200,
    dataIndex: 'updateTime',
    type: 'rangePicker',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          updateTime: undefined,
          updateTimeFrom: startDataTime?.format('yyyy-MM-DD 00:00:00'),
          updateTimeTo: endDataTime?.format('yyyy-MM-DD 23:59:59'),
        }
      },
    },
  },
  { title: '操作', dataIndex: 'operation', width: 80 },

  // 基本信息
  {
    title: '合同金额(元)',
    dataIndex: 'applyCreditAmount',
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '合同约定起租日',
    dataIndex: 'actualLeaseDate',
  },
  {
    title: '合同约定终止日',
    dataIndex: 'actualFinishDate',
  },

  // 保单信息
  {
    title: '保单金额(元)',
    dataIndex: 'policyAmount',
    requiredMark: true,
    editable: (val) => AmountEditable(val, 'policyAmount', { required: true, disabled: false }),
    render: (val) => amountFormat(formatPercent(val)) ?? '-',
  },
  {
    title: '险种',
    requiredMark: true,
    dataIndex: 'policyType',
    matchOption: 'policyTypeEnum',
    editable: {
      element: <Select options="policyTypeEnum" />,
      rules: [{ required: true, message: '请选择' }],
    },
    render: (val) => <MatchFormat matchOption="policyTypeEnum" value={val} />,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    requiredMark: true,
    span: 2,
    editable: TextAreaEditable,
    render: (val) => <FiledFormat title={val} />,
  },
  {
    title: '保险起始日-详情',
    width: 180,
    dataIndex: 'insuranceStartDate',
    requiredMark: true,
    editable: (val) => DatePickerEditable(val, 'insuranceStartDate'),
    render: (val) => <FiledFormat value={val} />,
  },
  {
    title: '保险到期日-详情',
    width: 180,
    dataIndex: 'insuranceEndDate',
    requiredMark: true,
    editable: (val) => DatePickerEditable(val, 'insuranceEndDate'),
    render: (val) => <FiledFormat value={val} />,
  },
  {
    title: '创建人',
    dataIndex: 'createName',
    render: (val, { createByName }) => val || createByName,
  },
  {
    title: '标识信息',
    dataIndex: 'identificationInformation',
    render: (val) => <FiledFormat value={val} />,
  },
  {
    title: '逾期天数',
    dataIndex: 'overdueDays',
    render: (val) => <FiledFormat value={val} />,
  },
  {
    title: '续保保单反馈日',
    dataIndex: 'renewalPolicyFeedbackDate',
    render: (val) => <FiledFormat value={val} />,
  },

  AmountColumn({ title: '剩余未还本金(元）', dataIndex: 'remainingUnpaidPrincipal' }),
  { title: '合同到期日', dataIndex: 'contractExpirationDate' },
]

export default ALL_COLUMNS
