import { Input, Radio, Tooltip } from 'antd'
import { dateRangeTransform } from '@/utils/transform'
import { InputEditable, FiledFormat, AmountEditable, MatchOptionColumn } from '@/components/Format'
import { formatPercent, amountFormat, rangePresets, hasValue } from '@/utils'
import AmountRange from '@/components/AmountRange'
import { Form, Select, App } from '@zswl/components'
import { OrgListSelect } from '@/components/Select'
import { FounderSelect } from '@/components'
import { DateColumn, AmountColumn } from '@/components/Format'

const ALL_COLUMNS = [
  {
    title: '融资机构',
    dataIndex: 'organizationId',
    width: 280,
    search: { element: <OrgListSelect /> },
    render: (val, { organizationName }) => {
      const title = Array.isArray(organizationName) ? organizationName.join('、') : val
      const newValue = Array.isArray(organizationName) ? organizationName?.[0] : organizationName
      return <Tooltip title={title}>{newValue}</Tooltip>
    },
  },
  AmountColumn({
    title: '融资金额',
    dataIndex: 'financingAmount',
    width: 150,
    search: true,
  }),
  MatchOptionColumn({
    title: '融资状态',
    dataIndex: 'financingStatus',
    width: 150,
    matchOption: 'fundFinancingStatusEnum',
    search: true,
  }),
  {
    title: '资金经理',
    dataIndex: 'moneyManagerId',
    search: {
      element: <FounderSelect params={{ job: 'moneymanager' }} />,
    },
  },
  {
    title: '创建日期',
    width: 180,
    dataIndex: 'createTimeFrom',
    type: 'rangePicker',
    ranges: rangePresets,
    dateFormat: 'yyyy-MM-DD',
    itemProps: {
      transform: (val) => dateRangeTransform(val, 'createTimeFrom', 'createTimeTo'),
    },
  },
  {
    title: '变更日期',
    width: 180,
    dataIndex: 'updateTimeFrom',
    type: 'rangePicker',
    ranges: rangePresets,
    dateFormat: 'yyyy-MM-DD',
    itemProps: {
      transform: (val) => dateRangeTransform(val, 'updateTimeFrom', 'updateTimeTo'),
    },
  },
  {
    title: '借款日期',
    width: 180,
    dataIndex: 'actualLoanDateFrom',
    type: 'rangePicker',
    ranges: rangePresets,
    dateFormat: 'yyyy-MM-DD',
    itemProps: {
      transform: (val) => dateRangeTransform(val, 'actualLoanDateFrom', 'actualLoanDateTo'),
    },
  },
  {
    title: '质押资产',
    width: 250,
    dataIndex: 'hasPledgeInfo',
    tooltip: true,
    search: {
      element: (
        <Radio.Group
          options={[
            { label: '有', value: 1 },
            { label: '无', value: 0 },
          ]}
        ></Radio.Group>
      ),
    },
    render: (val, { contractCodeList }) => {
      return contractCodeList ? '有' : '无'
    },
  },
  {
    title: '融资编号',
    dataIndex: 'financingCode',
    width: 200,
    fixed: 'left',
    editable: InputEditable({ required: false }),
  },
  AmountColumn({
    title: '融资金额(元)',
    dataIndex: 'financingAmount',
    requiredMark: true,
    align: 'right',
    editable: (val) => AmountEditable(val, 'financingAmount', { required: true, disabled: false }),
  }),
  AmountColumn({
    title: '剩余本金(元)',
    dataIndex: 'lastPrincipal',
  }),
  AmountColumn({
    title: '担保融资金额(元)',
    dataIndex: 'guaranteeFinancingAmount',
    editable: (val) =>
      AmountEditable(val, 'guaranteeFinancingAmount', { required: true, disabled: false }),
  }),
  AmountColumn({
    title: '信用融资金额(元)',
    dataIndex: 'creditFinancingAmount',
    align: 'right',
    editable: (val) =>
      AmountEditable(val, 'creditFinancingAmount', { required: true, disabled: false }),
  }),
  {
    title: '实际综合成本',
    align: 'right',
    dataIndex: 'actualComprehensiveCost',
    render: (val) => (hasValue(val) ? amountFormat(formatPercent(val)) + '%' : '-'),
  },
  AmountColumn({
    title: '借款年利率',
    dataIndex: 'interestRate',
    suffix: '%',
  }),
  {
    title: '利率类型',
    dataIndex: 'interestRateType',
    render: (val) => {
      return App.matchOption('rateType', val).label || '-'
    },
  },
  {
    title: '创建日期(列表)',
    width: 200,
    dataIndex: 'createTime',
    render: (val) => val || '-',
  },
  {
    title: '借款日期(列表)',
    width: 150,
    dataIndex: 'borrowDate',
    render: (val) => val || '-',
  },
  {
    title: '到期日期(列表)',
    width: 150,
    dataIndex: 'expireDate',
    render: (val) => val || '-',
  },
  {
    title: '审批状态',
    dataIndex: 'approvalStatus',
    width: 250,
    matchOption: 'fundFinancingProcessStatus',
    render: (val, { changeSubType }) => {
      const CHANGETYPE = App.matchOption('fundFinancingChangeSubTypeEnum', changeSubType).label
      return `${App.matchOption('fundFinancingProcessStatus', val).label}${
        CHANGETYPE ? '-' + CHANGETYPE : ''
      }`
    },
  },
  {
    title: '创建人',
    width: 150,
    dataIndex: 'createUserName',
    render: (val) => val || '-',
  },
  {
    title: '创建日期(列表)',
    width: 200,
    dataIndex: 'createTime',
    render: (val) => val || '-',
  },
  {
    title: '更新日期',
    width: 200,
    dataIndex: 'updateTime',
    render: (val) => val || '-',
  },
  {
    title: '总授信额度(元)',
    dataIndex: 'totalCreditLimit',
    align: 'right',
    editable: (val) => AmountEditable(val, 'totalCreditLimit', { disabled: true }),
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '剩余授信额度(元)',
    align: 'right',
    dataIndex: 'remainingCreditLimit',
    editable: (val) => AmountEditable(val, 'remainingCreditLimit', { disabled: true }),
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '融资期限类型',
    dataIndex: 'timeLimitType',
    requiredMark: true,
    matchOption: 'fundFinancingTimeLimitTypeEnum',
    editable: {
      element: <Select options={'fundFinancingTimeLimitTypeEnum'} />,
      rules: [{ required: true, message: '请选择' }],
    },
  },
  {
    title: '业务类型',
    dataIndex: 'businessType',
    requiredMark: true,
    matchOption: 'fundFinancingBizTypeEnum',
    editable: {
      element: <Select options={'fundFinancingBizTypeEnum'} />,
      rules: [{ required: true, message: '请选择' }],
    },
  },
  {
    title: '增信方式',
    dataIndex: 'guaranteeInfoList',
    span: 2,
  },
  {
    title: '资金用途',
    dataIndex: 'fundsPurpose',
    span: 2,
    requiredMark: true,
    editable: {
      element: <Input.TextArea />,
      rules: [{ required: true, message: '请输入' }],
    },
  },
  {
    title: '是否期初一次性收息',
    dataIndex: 'initialInterestReceivedOnce',
    requiredMark: true,
    // span: 2,
    matchOption: 'yesOrNo',
    editable: {
      element: <Select options={'yesOrNo'} />,
      rules: [{ required: true, message: '请选择' }],
    },
  },
  {
    title: '备注',
    dataIndex: 'remark',
    requiredMark: true,
    span: 2,
    editable: {
      element: <Input.TextArea />,
      rules: [{ required: true, message: '请输入' }],
      style: { marginBottom: 15 },
    },
    render: (val) => val,
  },
  {
    title: '资金经理(基本信息)',
    dataIndex: 'fundManagerName',
    editable: false,
    width: 150,
    render: (val) => val,
  },
  {
    title: '所属部门',
    dataIndex: 'deptName',
    editable: false,
    render: (val) => val,
  },
  {
    title: '部门负责人',
    dataIndex: 'bizHeaderName',
    editable: false,
    render: (val) => val,
  },
  {
    title: '分管领导',
    dataIndex: 'leaderName',
    editable: false,
    render: (val) => val,
  },
  AmountColumn({ title: '综合融资成本(%)', dataIndex: 'comprehensiveFinancingCost' }),
  DateColumn({ dataIndex: 'actualLoanDate', title: '起息日', search: true }),
  DateColumn({ dataIndex: 'actualExpireDate', title: '到期日', search: true }),
]
export default ALL_COLUMNS
