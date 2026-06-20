import { Input, InputNumber, message, Tooltip } from 'antd'
import {
  DatePickerEditable,
  AmountEditable,
  AmountFormat,
  FiledFormat,
  InputEditable,
  MatchFormat,
  AmountColumn,
  MatchOptionColumn,
} from '@/components/Format'
import { dateRangeTransform } from '@/utils/transform'
import AmountRange from '@/components/AmountRange'
import { rules, formatPercent, amountFormat, rangePresets } from '@/utils'
import { Select } from '@zswl/components'
import { FounderSelect } from '@/components/Select'
import DataUpload from '@/components/DataUpload'
import Api from '@/api/financial/creditManage'
import { getFinancialUrl } from '../financingUrl'
const { TextArea } = Input

const ALL_COLUMNS = [
  {
    title: '担保机构名称',
    dataIndex: 'guaranteeAgencyName',
    width: 200,
    editable: InputEditable(),
  },
  { title: '机构编号', dataIndex: 'guaranteeAgencyCode', width: 200, editable: InputEditable() },
  {
    title: '担保额度',
    dataIndex: 'totalGuaranteeLimit',
    itemProps: {
      transform: (val) => {
        const [start, end] = val || []
        return {
          totalGuaranteeLimit: undefined,
          guaranteeLimitFrom: start && start * 10000,
          guaranteeLimitTo: end && end * 10000,
        }
      },
    },
    render: (val) => amountFormat(formatPercent(val)),
    editable: {
      element: <AmountRange />,
    },
  },

  {
    title: '总担保额度（元）',
    dataIndex: 'totalGuaranteeLimit',
    width: 160,
    align: 'right',
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '已使用担保额度（元）',
    dataIndex: 'usedGuaranteeLimit',
    width: 200,
    align: 'right',
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '剩余担保额度（元）',
    dataIndex: 'remainingGuaranteeLimit',
    width: 200,
    align: 'right',
    render: (val) => amountFormat(formatPercent(val)),
  },
  { title: '担保期限', dataIndex: 'guaranteePeriod', width: 200 },
  { title: '担保状态', dataIndex: 'effective', matchOption: 'effective', width: 100 },
  {
    title: '创建人',
    dataIndex: 'createBy',
    width: 150,
    editable: {
      element: (
        <FounderSelect
          params={{
            job: 'moneymanager',
          }}
        />
      ),
    },
    render: (val, record) => <FiledFormat title={record.createByName} />,
  },

  {
    title: '是否集团内关联方',
    dataIndex: 'relatedParty',
    requiredMark: true,
    matchOption: 'yesOrNo',
    editable: {
      element: <Select options="yesOrNo" />,
      rules: [rules.required('请选择是否集团内关联方')],
    },
  },
  { title: '统一社会信用码', dataIndex: 'uscCode', editable: InputEditable({ required: false }) },
  {
    title: '成立日期',
    dataIndex: 'establishDate',
    requiredMark: true,
    editable: (val) => DatePickerEditable(val, 'establishDate'),
  },
  {
    title: '核准日期',
    dataIndex: 'approvalDate',
    requiredMark: true,
    editable: (val) => DatePickerEditable(val, 'approvalDate'),
  },
  {
    title: '营业许可证是否为长期',
    dataIndex: 'longTimeLicense',
    requiredMark: true,
    matchOption: 'trueOrFalse',
    editable: {
      element: <Select options="trueOrFalse" />,
      rules: [rules.required('请选择')],
    },
  },
  {
    title: '营业许可证到期日',
    dataIndex: 'bizLicenseEndDate',
    requiredMark: true,
    editable: (val) => DatePickerEditable(val, 'bizLicenseEndDate'),
  },
  {
    title: '经济类型',
    dataIndex: 'economyType',
    requiredMark: true,
    matchOption: 'economyType',
    editable: {
      element: <Select options="economyType" />,
      rules: [rules.required('请选择')],
    },
    render: (val) => <MatchFormat value={val} matchOption="economyType" />,
  },
  {
    title: '组织机构类型',
    dataIndex: 'orgType',
    requiredMark: true,
    matchOption: 'orgType',
    editable: {
      element: <Select options="orgType" />,
      rules: [rules.required('请选择')],
    },
    render: (val) => <MatchFormat value={val} matchOption="orgType" />,
  },
  {
    title: '注册资本（元）',
    dataIndex: 'registerCapital',
    requiredMark: true,
    align: 'right',
    render: (value) => <AmountFormat value={value} />,
    editable: (val) => AmountEditable(val, 'registerCapital', { required: true, disabled: false }),
  },
  {
    title: '注册资本币种',
    dataIndex: 'registerCurrencyType',
    requiredMark: true,
    matchOption: 'currencyType',
    editable: {
      element: <Select options="currencyType" />,
      rules: [rules.required('请选择')],
    },
    render: (val) => <MatchFormat value={val} matchOption="currencyType" />,
  },
  {
    title: '实收资本（元）',
    dataIndex: 'realCapital',
    requiredMark: true,
    align: 'right',
    render: (value) => <AmountFormat value={value} />,
    editable: (val) => AmountEditable(val, 'realCapital', { required: true, disabled: false }),
  },
  {
    title: '实收资本币种',
    dataIndex: 'realCurrencyType',
    matchOption: 'currencyType',
    editable: {
      element: <Select options="currencyType" />,
    },
    render: (val) => <MatchFormat value={val} matchOption="currencyType" />,
  },
  {
    title: '经营范围',
    requiredMark: true,
    dataIndex: 'bizScope',
    span: 2,
    editable: {
      element: (
        <Input.TextArea
          autoSize={{ minRows: 4, maxRows: 20 }}
          style={{ padding: '20px 0' }}
          placeholder="请输入!"
        />
      ),
      rules: [rules.required('请选择')],
    },
    render: (val) => <FiledFormat title={val} />,
  },
  {
    title: '法人代表',
    dataIndex: 'corpRepresent',
    span: 2,
    requiredMark: true,
    editable: InputEditable({ disabled: false, required: true }),
  },
  { title: '资金经理', dataIndex: 'fundManagerName', editable: InputEditable({ required: false }) },
  {
    title: '业务部门',
    dataIndex: 'deptName',
    editable: InputEditable({ required: false }),
  },
  {
    title: '业务分管领导',
    dataIndex: 'divisionLeaderName',
    editable: InputEditable({ required: false }),
  },
  { title: '担保编号', dataIndex: 'guaranteeCode' },
  { title: '额度是否可循环', dataIndex: 'recyclable', matchOption: 'yesOrNo' },
  {
    title: '资料名称',
    dataIndex: 'fileList',
    width: 200,
    render: (value, record) => {
      return (
        <DataUpload.List
          value={value}
          params={{
            moduleType: 'FUND_GUARANTEE_AGENCY',
            mainId: record.agencyId,
          }}
        />
      )
    },
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 200,
    span: 2,
    editable: {
      element: <Input.TextArea minRows={4} />,
    },
    render: (val) => <FiledFormat title={val} />,
  },
  {
    title: '担保生效时间',
    width: 200,
    dataIndex: 'guaranteePeriod',
    type: 'rangePicker',
    dateFormat: 'yyyy-MM-DD',
    ranges: rangePresets,
    itemProps: {
      transform: (val) => dateRangeTransform(val, 'effectiveTimeFrom', 'effectiveTimeTo'),
    },
  },
  {
    title: '创建日期',
    width: 180,
    dataIndex: 'createTime',
    type: 'rangePicker',
    dateFormat: 'yyyy-MM-DD HH:mm:ss',
    ranges: rangePresets,
    itemProps: {
      transform: (val) => dateRangeTransform(val, 'guaranteeDateFrom', 'guaranteeDateTo'),
    },
  },
  {
    title: '更新日期',
    width: 180,
    dataIndex: 'updateTime',
    dateFormat: 'yyyy-MM-DD  HH:mm:ss',
  },

  {
    title: '融资编号',
    dataIndex: 'financingCode',
    width: 200,
    actions: ({ financingCode: name, financingId }) => [
      { name, to: getFinancialUrl(name, financingId) },
    ],
  },
  { title: '融资机构', dataIndex: 'organizationName' },
  AmountColumn({ title: '融资金额（元）', dataIndex: 'financingAmount' }),
  AmountColumn({ title: '担保融资额（元）', dataIndex: 'guaranteeFinancingAmount' }),
  AmountColumn({ title: '信用融资额（元）', dataIndex: 'creditFinancingAmount' }),
  AmountColumn({ title: '剩余本金（元）', dataIndex: 'remainingAmount' }),
  AmountColumn({ title: '剩余担保本金（元）', dataIndex: 'remainingGuaranteeAmount' }),
  AmountColumn({ title: '剩余信用本金（元）', dataIndex: 'remainingCreditAmount' }),
  { title: '贷款日', dataIndex: 'borrowDate' },
  { title: '到期日', dataIndex: 'expireDate' },
  MatchOptionColumn({
    title: '融资状态',
    dataIndex: 'financingStatus',
    matchOption: 'fundFinancingStatusEnum',
  }),
  { title: '创建人', dataIndex: 'createByName', editable: false },
]
export default ALL_COLUMNS
