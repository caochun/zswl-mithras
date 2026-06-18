import { hasValue, amountFormat, formatPercent } from '@/utils'
import {
  AmountColumn,
  AmountEditable,
  DateColumn,
  FiledFormat,
  InputColumn,
  MatchOptionColumn,
  TextAreaColumn,
} from '@/components/Format'
import Amount from '@/components/Amount'
import { ClientSelect, OrgSelect } from '@/components/Select'
import { Select } from '@zswl/components'
import { history } from '@zswl/admin'
import { DatePicker, Input } from 'antd'
import moment from 'moment'

const initFormat = 1

const amountCommonProps = {
  precision: 6,
  initFormat: 1,
  needSmallNumber: false,
}
const ALL_COLUMNS = ({ pathname, editIndex, type } = {}) => {
  return [
    // 列表
    {
      title: '月份',
      dataIndex: 'provisionDate',
      editable: () => {
        return {
          element: <DatePicker picker="month" style={{ width: '100%' }} />,
        }
      },
      itemProps: {
        transform: (val) => (val ? moment(val).endOf('month').format('YYYY-MM-DD') : undefined),
      },
      actions({ provisionDate, id }) {
        return [
          {
            name: provisionDate,
            onClick: () => history.push(`${pathname}/detail/${id}`),
          },
        ]
      },
    },
    InputColumn({ title: '借据编号', dataIndex: 'receiptCode' }),
    {
      title: '状态',
      dataIndex: 'provisionStatus',
      matchOption: 'kpiProvisionStatusEnum',
      editable: {
        element: <Select options={'kpiProvisionStatusEnum'} />,
      },
    },
    {
      title: '创建日期',
      dataIndex: 'createTime',
    },
    {
      title: '创建人',
      dataIndex: 'createByName',
    },
    // 详情
    {
      title: '业务类型',
      dataIndex: 'bizTypeName',
    },
    {
      title: '项目类型',
      dataIndex: 'projClassifyName',
    },
    { title: '借据编号', dataIndex: 'receiptCode' },
    {
      title: '业务部门',
      dataIndex: 'profitBelongDeptId',
      render: (val, { profitBelongDeptName }) => <FiledFormat title={profitBelongDeptName} />,
      width: 140,
      editable: type
        ? {
            element: <OrgSelect />,
            functionCode: 'budgetProvisioningOrgSelect',
          }
        : false,
    },
    {
      title: '客户名称',
      dataIndex: 'clientId',
      width: 220,
      render: (val, { clientName }) => <FiledFormat title={clientName} />,
      excelRender: (val, { clientName }) => clientName,
      editable: type
        ? {
            element: <ClientSelect />,
            canJump: false,
            functionCode: 'budgetProvisioningClientSelect',
          }
        : false,
    },
    {
      title: '合同编号',
      dataIndex: 'contractCode',
      width: 280,
    },
    {
      title: '到期日',
      dataIndex: 'endDate',
    },
    {
      title: '剩余期限(年)',
      dataIndex: 'residualMaturity',
    },
    AmountColumn({
      title: '剩余本金',
      dataIndex: 'remainingPrincipal',
      rename: '剩余本金(元）',
    }),
    AmountColumn({
      title: '保证金',
      dataIndex: 'earnestBalance',
      rename: '保证金(元）',
    }),
    {
      title: '敞口',
      dataIndex: 'exposure',
      rename: '敞口(元）',
      align: 'right',
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      },
    },
    {
      title: '风险等级',
      dataIndex: 'riskLevel',
      matchOption: 'assetClassifyResultEnum',
    },
    {
      title: '计提比例',
      dataIndex: 'withdrawalRatio',
      editable: (record, rowIndex) => {
        return editIndex === rowIndex
          ? AmountEditable(record, 'withdrawalRatio', {
              required: true,
              disabled: false,
            })
          : false
      },
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) + '%' : '-'
      },
    },
    {
      title: '本月风险金余额',
      rename: '本月风险金余额(元）',
      dataIndex: 'profitCurrent',
      align: 'right',
      editable: (record, rowIndex) => {
        return editIndex === rowIndex
          ? AmountEditable(record, 'profitCurrent', {
              required: true,
              disabled: false,
            })
          : false
      },
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      },
    },
    {
      title: '上月风险金余额',
      rename: '上月风险金余额(元）',
      dataIndex: 'profitTotal',
      align: 'right',
      editable: (record, rowIndex) => {
        return editIndex === rowIndex
          ? AmountEditable(record, 'profitTotal', {
              required: true,
              disabled: false,
            })
          : false
      },
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      },
      // render: (val) => {
      //   return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      // },
    },
    {
      title: '本月风险金计提/转回',
      rename: '本月风险金计提/转回(元）',
      dataIndex: 'bonusCurrent',
      align: 'right',
      render: (val) => {
        return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
      },
    },
    AmountColumn({
      title: '应计利息',
      dataIndex: 'accruedInterest',
      precision: 6,
      rename: '应计利息(元）',
    }),
    AmountColumn({
      title: '下期租金',
      dataIndex: 'nextRent',
      precision: 6,
      rename: '下期租金(元）',
    }),
    { title: '备注', dataIndex: 'remark' },
    // 数据查询
    InputColumn({ title: '测算时间', dataIndex: 'updateTime' }),
    InputColumn({
      title: '合同号',
      dataIndex: 'contractCode',
      width: 280,
    }),

    InputColumn({ title: '内评级别', dataIndex: 'innerMdLevel' }),
    AmountColumn({
      title: '内评违约概率',
      dataIndex: 'eclPd',
      initFormat: 1 / 100,
      suffix: '%',
      precision: 4,
      needSmallNumber: false,
    }),
    InputColumn({ title: '外评级别', dataIndex: 'outerLevel' }),
    AmountColumn({
      title: '违约概率PD',
      dataIndex: 'eclOuterPd',
      initFormat: 1 / 100,
      suffix: '%',
      precision: 4,
      needSmallNumber: false,
    }),
    InputColumn({ title: '所属分组', dataIndex: 'group' }),
    InputColumn({ title: '五级分类', dataIndex: 'classify' }),
    InputColumn({ title: '逾期天数', dataIndex: 'lateDay' }),
    InputColumn({ title: '租赁物类型', dataIndex: 'leaseType' }),
    AmountColumn({ title: 'EAD', rename: 'EAD(元）', dataIndex: 'ead', ...amountCommonProps }),
    InputColumn({ title: '债项阶段', dataIndex: 'eclStep', width: 80 }),
    InputColumn({ title: '上迁债项阶段', dataIndex: 'promotionResult', width: 120 }),
    AmountColumn({ title: '期限调整系数T', dataIndex: 'eclFactorT', ...amountCommonProps }),
    InputColumn({ title: '前瞻因子Z', dataIndex: 'eclParamZ' }),
    InputColumn({ title: '情景权重', dataIndex: 'eclParamWeight' }),
    AmountColumn({
      title: '基准PDforward',
      dataIndex: 'basePdForward',
      ...amountCommonProps,
    }),
    AmountColumn({
      title: '乐观PDforward',
      dataIndex: 'optPdForward',
      ...amountCommonProps,
    }),
    AmountColumn({
      title: '悲观PDforward',
      dataIndex: 'gloPdForward',
      ...amountCommonProps,
    }),
    AmountColumn({ title: '基准PDIFRS9', dataIndex: 'eclBaseIfrs9', ...amountCommonProps }),
    AmountColumn({ title: '乐观PDIFRS9', dataIndex: 'eclOptIfrs9', ...amountCommonProps }),
    AmountColumn({ title: '悲观PDIFRS9', dataIndex: 'eclGloIfrs9', ...amountCommonProps }),
    AmountColumn({
      title: '基准ECL',
      rename: '基准ECL(元）',
      dataIndex: 'baseEcl',
      ...amountCommonProps,
    }),
    AmountColumn({
      title: '乐观ECL',
      rename: '乐观ECL(元）',
      dataIndex: 'optEcl',
      ...amountCommonProps,
    }),
    AmountColumn({
      title: '悲观ECL',
      rename: '悲观ECL(元）',
      dataIndex: 'gloEcl',
      ...amountCommonProps,
    }),
    AmountColumn({ title: 'ECL', rename: 'ECL(元）', dataIndex: 'ecl', ...amountCommonProps }),
    DateColumn({ title: '测算日期', dataIndex: 'createTime', search: true }),
    MatchOptionColumn({ title: '是否逾期', dataIndex: 'overdueFlag', matchOption: 'yesOrNo' }),
    InputColumn({ title: '计算月份', dataIndex: 'calculationDate' }),
    DateColumn({ title: '合同到期日', dataIndex: 'contractExpirationDate' }),
    InputColumn({ title: '评估主体名称', dataIndex: 'evaluationSubjectName' }),
    TextAreaColumn({ title: '备注', dataIndex: 'remark' }),
    // 参数配置
  ]
}

export default ALL_COLUMNS
