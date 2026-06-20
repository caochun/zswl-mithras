import { Input } from 'antd'
import { dateRangeTransform } from '@/utils/transform'
import { InputEditable, RangePickerEditable, FiledFormat, AmountEditable, AmountColumn, MatchOptionColumn, Amount, AmountRange } from '@/components/Format'
import { formatPercent, amountFormat, rangePresets } from '@/utils'
import { App, Form, Select } from '@zswl/components'
import FormGuarantee from './FormGuarantee'
import moment from 'moment'
import { FounderSelect } from '@/components/Select'
import { CreditOrgSelect } from '../Select'
import { getFinancialUrl } from '../financingUrl'

const ALL_COLUMNS = [
  {
    title: '授信项目',
    dataIndex: 'creditProject',
    width: 200,
    editable: InputEditable({ required: false }),
  },
  {
    title: '授信产品',
    dataIndex: 'creditType',
    matchOption: 'fundFinancingBizTypeEnum',
  },
  {
    title: '授信机构',
    dataIndex: 'organizationId',
    width: 280,
    editable: {
      element: <CreditOrgSelect disabled={true} />,
    },
    render: (val, { organizationName }) => organizationName,
  },
  AmountColumn({
    title: '总授信额度（元）',
    dataIndex: 'totalCreditLimit',
    requiredMark: true,
    align: 'right',
    wrapItemProps: {
      required: true,
    },
  }),
  {
    title: '授信编号',
    dataIndex: 'creditCode',
    width: 160,
    editable: InputEditable({ required: false }),
  },
  { title: '剩余授信时间（天）', dataIndex: 'remainingDays' },
  {
    title: '管理人',
    dataIndex: 'createBy',
    width: 150,
    editable: {
      element: <FounderSelect params={{ job: 'moneymanager' }} />,
    },
    render: (val, record) => <FiledFormat title={record.createByName} />,
  },
  {
    title: '授信金额',
    dataIndex: 'creditLimit',
    width: 150,
    itemProps: {
      transform: (val) => {
        const [start, end] = val || []
        return {
          creditLimit: undefined,
          creditLimitFrom: start && start * 10000,
          creditLimitTo: end && end * 10000,
        }
      },
    },
    render: (val) => amountFormat(formatPercent(val)),
    editable: {
      element: <AmountRange />,
    },
  },
  {
    title: '授信日期',
    width: 150,
    dataIndex: 'creditDateTo',
    ranges: rangePresets,
    type: 'rangePicker',
    dateFormat: 'yyyy-MM-DD',
    itemProps: {
      transform: (val) => dateRangeTransform(val, 'creditDateFrom', 'creditDateTo'),
    },
  },
  {
    title: '机构类型',
    dataIndex: 'organizationType',
    width: 150,
    matchOption: 'organizationType',
  },
  { title: '银行联行号', dataIndex: 'interBankNo', editable: InputEditable({ required: false }) },
  { title: '统一社会信用代码', dataIndex: 'uscCode', editable: InputEditable({ required: false }) },
  {
    title: '增信方式',
    dataIndex: 'enhanceCreditMethod',
    matchOption: 'enhanceCreditMethod',
    requiredMark: true,
    editable: {
      element: <Select options={'enhanceCreditMethod'} mode="multiple" allowClear />,
      rules: [{ required: true, message: '请选择增信方式' }],
    },
    render: (val) => {
      return val?.map((v) => App.matchOption('enhanceCreditMethod', v).label).join('、')
    },
  },
  {
    title: '担保方',
    dataIndex: 'guaranteeDetail',
    span: 2,
    editable: ({ guaranteeDetail, usedTotalCreditAmount }) => {
      return (
        <Form.Item dependencies={['enhanceCreditMethod']} noStyle>
          {({ getFieldValue }) => {
            const val = getFieldValue('enhanceCreditMethod')
            return (
              <FormGuarantee
                listName="guaranteeDetail"
                method={val}
                value={guaranteeDetail}
                disabled={usedTotalCreditAmount > 0}
              />
            )
          }}
        </Form.Item>
      )
    },
    render: (val) => <FormGuarantee.Detail value={val} />,
  },
  {
    title: '额度是否可循环',
    dataIndex: 'recyclable',
    requiredMark: true,
    matchOption: 'yesOrNo',
    editable: {
      options: 'yesOrNo',
      rules: [{ required: true, message: '请选择额度是否可循环' }],
    },
  },
  {
    title: '资金用途',
    dataIndex: 'fundUsage',
    requiredMark: true,
    editable: InputEditable({ disabled: false }),
  },
  {
    title: '授信生效时间',
    dataIndex: 'effectiveDate',
    type: 'rangePicker',
    ranges: rangePresets,
    dateFormat: 'yyyy-MM-DD',
    span: 2,
    requiredMark: true,
    itemProps: {
      transform: (val) => dateRangeTransform(val, 'effectiveDateFrom', 'effectiveDateTo'),
    },
    editable: (val) =>
      RangePickerEditable(val, ['effectiveDateFrom', 'effectiveDateTo', 'effectiveDate'], {
        style: { width: 240 },
      }),
    render: (val, dataSource) => {
      return [dataSource?.effectiveDateFrom, dataSource?.effectiveDateTo]
        .map((v) => v && moment(v).format('YYYY-MM-DD'))
        .join(' ~ ')
    },
  },
  { title: '金控担保', dataIndex: 'guaranteeAmount', width: 150 },
  { title: '中拓担保', dataIndex: 'remainingGuaranteeAmount', width: 150 },
  {
    title: '备注',
    dataIndex: 'remark',
    span: 2,
    requiredMark: true,
    editable: {
      element: <Input.TextArea />,
      style: { marginBottom: 15 },
      rules: [{ required: true, message: '请输入备注' }],
    },
    render: (val) => val,
  },
  { title: '资金经理', dataIndex: 'fundManagerName', editable: InputEditable({ required: false }) },
  { title: '部门', dataIndex: 'fundManagerDept', editable: InputEditable({ required: false }) },
  {
    title: '部门负责人',
    dataIndex: 'fundManagerDeptLeader',
    editable: InputEditable({ required: false }),
  },
  {
    title: '分管领导',
    dataIndex: 'fundManagerDivisionLeader',
    editable: InputEditable({ required: false }),
  },
  {
    title: '创建日期',
    width: 180,
    dataIndex: 'createTime',
    type: 'rangePicker',
    ranges: rangePresets,
    dateFormat: 'yyyy-MM-DD',
    itemProps: {
      transform: (val) => dateRangeTransform(val, 'createFrom', 'createTo'),
    },
  },
  { title: '更新日期', width: 180, dataIndex: 'updateTime', dateFormat: 'yyyy-MM-DD' },
  // 授信总额
  {
    title: '授信总额-总额度(元)',
    dataIndex: 'totalCreditLimit',
    align: 'right',
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '授信总额-担保额度(元)',
    dataIndex: 'guaranteeAmount',
    align: 'right',
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '授信总额-信用额度(元)',
    dataIndex: 'creditLimit',
    align: 'right',
    render: (val) => amountFormat(formatPercent(val)),
  },
  // 已使用额度
  {
    title: '已使用额度-总额度(元)',
    dataIndex: 'usedTotalCreditAmount',
    align: 'right',
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '已使用额度-担保额度(元)',
    dataIndex: 'usedGuaranteeAmount',
    render: (val) => amountFormat(formatPercent(val)),
    align: 'right',
  },
  {
    title: '已使用额度-信用额度(元)',
    dataIndex: 'usedCreditAmount',
    render: (val) => amountFormat(formatPercent(val)),
    align: 'right',
  },
  // 剩余授信额度
  {
    title: '剩余授信额度-总额度(元)',
    dataIndex: 'remainingLimit',
    align: 'right',
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '剩余授信额度-担保额度(元)',
    dataIndex: 'remainingGuaranteeAmount',
    align: 'right',
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '剩余授信额度-信用额度(元)',
    dataIndex: 'remainingCreditAmount',
    align: 'right',
    render: (val) => amountFormat(formatPercent(val)),
  },

  {
    title: '剩余总授信额度(元)',
    dataIndex: 'remainingTotalLimit',
    align: 'right',
    render: (val) => amountFormat(formatPercent(val)),
  },
  {
    title: '授信到期日',
    dataIndex: 'effectiveDateTo',
  },

  {
    title: '融资编号',
    dataIndex: 'financingCode',
    width: 200,
    actions: ({ financingCode: name, financingId }) => [
      { name, to: getFinancialUrl(name, financingId) },
    ],
  },
  AmountColumn({ title: '融资金额（元）', dataIndex: 'financingAmount' }),
  AmountColumn({ title: '担保融资额（元）', dataIndex: 'guaranteeFinancingAmount' }),
  AmountColumn({ title: '信用融资额（元）', dataIndex: 'creditFinancingAmount' }),
  AmountColumn({ title: '占用额度（元）', dataIndex: 'usedTotalCreditAmount' }),
  AmountColumn({ title: '占用担保额度（元）', dataIndex: 'usedGuaranteeAmount' }),
  AmountColumn({ title: '占用信用额度（元）', dataIndex: 'usedCreditAmount' }),
  AmountColumn({ title: '剩余本金（元）', dataIndex: 'remainingAmount' }),
  AmountColumn({ title: '剩余担保本金（元）', dataIndex: 'remainingGuaranteeAmount' }),
  AmountColumn({ title: '剩余信用本金（元）', dataIndex: 'remainingCreditAmount' }),
  AmountColumn({ title: '合同利率', dataIndex: 'contractRate' }),
  { title: '贷款日', dataIndex: 'borrowDate' },
  { title: '到期日', dataIndex: 'expireDate' },
  MatchOptionColumn({
    title: '融资状态',
    dataIndex: 'financingStatus',
    matchOption: 'fundFinancingStatusEnum',
  }),
  { title: '创建人', dataIndex: 'createByName' },
  MatchOptionColumn({
    title: '生效状态',
    dataIndex: 'effective',
    matchOption: 'effectiveEnum',
  }),
]
export default ALL_COLUMNS
