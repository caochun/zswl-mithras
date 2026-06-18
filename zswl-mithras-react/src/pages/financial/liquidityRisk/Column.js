import { dateRangeTransform } from '@/utils/transform'
import { hasValue, rangePresets, rules } from '@/utils'
import {
  AmountColumn,
  DateColumn,
  DatePickerWrap,
  FiledFormat,
  TextAreaColumn,
} from '@/components/Format'
import { history } from '@zswl/admin'
import { FounderSelect } from '@/components/Select'
import { FormAmount } from '@/components/Form'
import { create, all, re } from 'mathjs'

const INIT_FORMAT = 10000 * 10000
const ALL_COLUMNS = [
  { title: '机构名称', dataIndex: 'organizationName', fixed: 'left', search: true },
  {
    title: '创建人',
    dataIndex: 'createByName',
    editable: {
      element: <FounderSelect params={{ job: 'moneymanager' }} />,
    },
    render: (val, record) => <FiledFormat title={record.createByName} />,
  },
  {
    title: '创建日期',
    width: 200,
    dataIndex: 'createTime',
    type: 'rangePicker',
    dateFormat: 'yyyy-MM-DD HH:mm:ss',
    ranges: rangePresets,
    itemProps: {
      transform: (val) => dateRangeTransform(val, 'createDateFrom', 'createDateTo'),
    },
  },
  { title: '更新日期', width: 200, dataIndex: 'updateTime', dateFormat: 'yyyy-MM-DD HH:mm:ss' },
  // 项目名称	合同编号	合同总金额（万元）	现金流入时间	租金（万元）	利息（万元）	首期租金（万元）	保证金（万元）	服务费/咨询费/手续费/其他（万元）	合计金额（万元）	预计流入现金流合计（万元）

  {
    title: '项目名称',
    dataIndex: 'projectName',
    fixed: 'left',
    actions({ projName: name, establishId, reviewId, projId }) {
      return [
        {
          name,
          to: `/lifeCycle/projectLifeCycle/detail/${projId}?establishId=${establishId}&reviewId=${reviewId}`,
        },
      ]
    },
    width: 300,
  },
  {
    title: '合同编号',
    dataIndex: 'contractCode',
    width: 290,
    actions({ contractCode: name, contractId: id }) {
      return [{ name, to: `/contract/list/detail/${id}` }]
    },
  },
  //flowInDate
  AmountColumn({
    title: '合同总金额（万元）',
    dataIndex: 'applyCreditAmount',
    width: 160,
    initFormat: INIT_FORMAT,
  }),
  DateColumn({ title: '现金流入时间', dataIndex: 'flowInDate', align: 'right' }),
  DateColumn({ title: '现金流出时间', dataIndex: 'cashOutflowTime', align: 'right' }),
  AmountColumn({
    title: '本金（万元）',
    dataIndex: 'principal',
    initFormat: INIT_FORMAT,
  }),
  AmountColumn({
    title: '利息（万元）',
    dataIndex: 'interest',
    initFormat: INIT_FORMAT,
  }),
  AmountColumn({
    title: '首期租金（万元）',
    dataIndex: 'downPayment',
    width: 150,
    initFormat: INIT_FORMAT,
  }),
  AmountColumn({
    title: '保证金（万元）',
    dataIndex: 'earnestMoney',
    width: 140,
    initFormat: INIT_FORMAT,
  }),
  AmountColumn({
    title: '服务费/咨询费/手续费/其他（万元）',
    dataIndex: 'consultingFee',
    width: 260,
    initFormat: INIT_FORMAT,
  }),
  AmountColumn({
    title: '合计金额（万元）',
    dataIndex: 'totalAmount',
    width: 160,
    initFormat: INIT_FORMAT,
  }),
  AmountColumn({
    title: '预计流入现金流合计（万元）',
    dataIndex: 'estimatedCashInFlowTotal',
    width: 220,
    initFormat: INIT_FORMAT,
  }),
  AmountColumn({
    title: '预计流出现金流合计（万元）',
    dataIndex: 'estimateCashOutflowAmount',
    width: 220,
    initFormat: INIT_FORMAT,
  }),

  // 融资金额  融资日期	备注
  AmountColumn({
    title: '融资金额',
    dataIndex: 'amount',
    width: 150,
    editable: (dataSource) => {
      const data = dataSource?.amount
      return {
        element: <FormAmount initFormat={INIT_FORMAT} min={-Infinity} />,
      }
    },
    initFormat: INIT_FORMAT,
  }),
  DateColumn({
    title: '融资日期',
    dataIndex: 'date',
    width: 100,
    editable: (val) => {
      return {
        initialValue: (val?.date && moment(val.date)) || undefined,
        type: 'datePicker',
        element: <DatePickerWrap />,
        transform: (date) => ({ date: date && moment(date).format('YYYY-MM-DD') }),
      }
    },
  }),
  TextAreaColumn({ title: '备注', dataIndex: 'remark', width: 100, editable: true }),
  //'融资机构',、'融资编码',、'融资总额（万元）',、'还本日',、'应还本金（万元）',、'应还利息（万元）',、'合计还款总金额（万元）',
  {
    title: '融资机构',
    dataIndex: 'financingOrgs',
    fixed: 'left',
    actions({ financingOrgs: name, financingOrgId: id }) {
      return [{ name, to: id && `/financial/guarantee/detail/${id}` }]
    },
  },
  {
    title: '融资渠道',
    dataIndex: 'financingOrgs',
    width: 300,
    render: (val) => <FiledFormat value={(val ?? []).join('、')} />,
  },
  {
    title: '融资编码',
    dataIndex: 'financingCode',
    width: 200,
    actions: ({ financingCode: name, financingOrgId: id, financingType, financingId }) => [
      {
        name,
        onClick: () => {
          if (financingType === 'DIRECT') {
            history.push(`/financial/direct/detail/${financingId}`)
          } else {
            history.push(`/financial/fund/detail/${financingId}`)
          }
        },
      },
    ],
  },
  AmountColumn({
    title: '融资总额（万元）',
    dataIndex: 'financingAmount',
    width: 160,
    initFormat: INIT_FORMAT,
  }),
  DateColumn({ title: '还本日', dataIndex: 'cashOutflowTime', width: 100, align: 'right' }),
  AmountColumn({
    title: '应还本金（万元）',
    dataIndex: 'principleAmount',
    width: 160,
    initFormat: INIT_FORMAT,
  }),
  AmountColumn({
    title: '应还利息（万元）',
    dataIndex: 'interestAmount',
    width: 160,
    initFormat: INIT_FORMAT,
  }),
  AmountColumn({
    title: '合计还款总金额（万元）',
    dataIndex: 'totalAmount',
    width: 180,
    initFormat: INIT_FORMAT,
  }),
]
export default ALL_COLUMNS
