import {
  AmountEditable,
  DatePickerEditable,
  RangePickerEditable,
  AmountFormat,
  PureAmountFormat,
  AmountColumn,
  FiledFormat,
  InputColumn,
  DateColumn,
} from '@/components/Format'
import { history } from '@zswl/admin'
import { amountFormat, rules } from '@/utils'
import { Input, Space } from 'antd'
import { BlackInfo } from '@/components/BlackGray/BlackInfo'

const ALL_COLUMNS = [
  {
    title: '租后检查部门',
    dataIndex: 'bizDeptName',
    editable: false,
  },
  {
    title: '客户主办',
    dataIndex: 'sponsorName',
    editable: false,
  },
  {
    title: '协查风控经理',
    dataIndex: 'riskManagerName',
    editable: false,
  },
  DateColumn({
    title: '检查日期',
    dataIndex: 'checkTime',
    requiredMark: true,
    editable: (val) =>
      DatePickerEditable(val, 'checkTime', {
        // disabled: val.checkWay === 'SITE',
        required: val.checkWay !== 'SITE',
      }),
  }),
  {
    title: '检查时段',
    dataIndex: 'time',
    requiredMark: true,
    editable: (val) => RangePickerEditable(val, ['checkPeriodStart', 'checkPeriodEnd', 'time']),
    render: (val, { checkPeriodStart, checkPeriodEnd }) =>
      [checkPeriodStart, checkPeriodEnd].filter(Boolean).join('~'),
  },
  {
    title: '客户名称',
    dataIndex: 'clientName',
    editable: false,
    render: (val, { clientId }) => (
      <div style={{ display: 'flex', alignItems: 'center' }}>
        <FiledFormat title={val} />
        <BlackInfo params={{ clientId }} style={{ marginLeft: 4 }} />
      </div>
    ),
  },
  { title: '行业', dataIndex: 'industry', editable: false },
  {
    title: '检查形式',
    dataIndex: 'checkWay',
    matchOption: 'afterLeaseCheckWayEnum',
    editable: false,
  },

  {
    title: '风险敞口余额',
    rename: '存量风险敞口（万元)',
    requiredMark: false,
    dataIndex: 'riskExposure',
    render: (value) => <AmountFormat value={value} initFormat={10000 * 10000} />,
    editable: false,
  },
  AmountColumn({
    title: '剩余本金（万元）',
    dataIndex: 'remainingPrincipal',
    editable: false,
    initFormat: 10000 * 10000,
  }),
  {
    title: '合同合计金额',
    rename: '合同合计金额（元)',
    dataIndex: 'contractAmount',
    requiredMark: false,
    render: (value) => <AmountFormat value={value} />,
    editable: (val) => AmountEditable(val, 'contractAmount', { required: false, rules: undefined }),
  },
  {
    title: '合同到期日',
    rename: '本次租后截止时间',
    dataIndex: 'deadline',
    type: 'datePicker',
    requiredMark: false,
    editable: (val) => DatePickerEditable(val, 'deadline', { disabled: true, required: false }),
  },

  {
    title: '项目名称',
    dataIndex: 'clientName',
    editable: (value) => ({
      element: <Input disabled />,
    }),
  },
  {
    title: '业务部门',
    dataIndex: 'bizDeptName',
    editable: false,
  },
  {
    title: '项目协办',
    dataIndex: 'projectCosponsorNames',
    editable: (value) => ({
      element: <Input disabled />,
      initialValue: (value.projectCosponsorNames || [])?.join('、'),
    }),
    render: (val) => (val || [])?.join('、'),
  },
  {
    title: '主要受访人员',
    dataIndex: 'mainPerson',
    requiredMark: true,
    editable: {
      element: <Input />,
      required: true,
      rules: [rules.required('请输入主要受访人员')],
    },
  },
  {
    title: '职务',
    dataIndex: 'mainPersonJob',
    requiredMark: true,
    editable: {
      element: <Input />,
      required: true,
      rules: [rules.required('请输入职务')],
    },
  },
  {
    title: '联系方式',
    dataIndex: 'mainPersonContactWay',
    requiredMark: true,
    editable: {
      element: <Input />,
      required: true,
      rules: [rules.required('请输入联系方式')],
    },
  },
  {
    title: '计划名称',
    dataIndex: 'planName',
    editable: false,
  },
  {
    title: '检查填报时间',
    dataIndex: 'checkFillTime',
    requiredMark: true,
    editable: (val) => DatePickerEditable(val, 'checkFillTime', { required: true }),
  },
  {
    title: '本次租后截止时间',
    dataIndex: 'deadline',
    editable: false,
  },
  {
    title: '合同列表',
    dataIndex: 'contractList',
    editable: false,
    span: 2,
    render: (value) => {
      return (
        <div>
          <Space direction="vertical" style={{ margin: '10px 0' }}>
            {value?.map(({ contractCode, applyCreditAmount, startDate, endDate, id }) => {
              return (
                <Space split="," size="small">
                  <div>
                    <span>合同编号：</span>
                    <a onClick={() => history.push(`/contract/list/detail/${id}`)}>
                      {contractCode}
                    </a>
                  </div>
                  <div>
                    <span>合同金额：</span>
                    <span>{PureAmountFormat(applyCreditAmount)}元</span>
                  </div>

                  <div>
                    <span>合同期限：</span>
                    <span>
                      {startDate}~{endDate}
                    </span>
                  </div>
                </Space>
              )
            })}
          </Space>
        </div>
      )
    },
  },

  InputColumn({
    title: '担保人名称',
    dataIndex: 'guaranteeNames',
    editable: false,
    render: (val, { guaranteeNames }) => guaranteeNames?.join('、'),
  }),
]
export default ALL_COLUMNS
