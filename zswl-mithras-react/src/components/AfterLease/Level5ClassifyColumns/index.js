import {
  AmountFormat,
  PureAmountFormat,
  TextAreaEditable,
} from '@/components/Format'
import { rules } from '@/utils'
import { Tooltip } from 'antd'
import { Select } from '@zswl/components'
import { history } from '@zswl/admin'
import { FiledFormat } from '@/components/Format'

// const toDetail = (clientId, clientType) => {
//   history.push(
//     `/customer/maintain/detail/${clientId}?clientType=${clientType}&flag=info&typeId=create`
//   )
// }

export const ALL_COLUMNS = [
  {
    title: '客户名称',
    dataIndex: 'clientName',
    render: (val, { clientName, clientId, clientType }) => (
      <Tooltip title={clientName}>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          {clientName}
          {/* <IconFont
            type="icon-icon_link"
            onClick={() => toDetail(clientId, clientType)}
            className={'z-icon'}
          /> */}
        </div>
      </Tooltip>
    ),
    width: 160,
  },
  { title: '客户编号', width: 130, dataIndex: 'clientId' },
  {
    title: '合同编号',
    width: 100,
    dataIndex: 'startRentContractCodes',
    render: (val) => {
      return val?.join('、') || '-'
    },
  },
  {
    title: '合同剩余本金(元)',
    width: 100,
    dataIndex: 'startRentContractRemainingPrincipal',
    render: (val) => {
      return (
        val
          ?.map((item) => {
            return PureAmountFormat(item)
          })
          .join('、') || '-'
      )
    },
  },
  {
    title: '剩余租期',
    width: 100,
    dataIndex: 'remainingTerm',
    render: (val) => {
      return val ?? 0
    },
  },
  {
    title: '合同到期日',
    width: 100,
    dataIndex: 'contractExpirationDate',
    render: (val) => {
      return val ?? '-'
    },
  },
  {
    title: '存量风险敞口(元)',
    width: 100,
    dataIndex: 'stockRiskExposure',
    render: (val) => <AmountFormat value={val} />,
  },
  {
    title: '资产余额(元)',
    dataIndex: 'assetBalance',
    render: (val) => <AmountFormat value={val} />,
  },
  {
    title: '还款情况',
    width: 100,
    dataIndex: 'repayment',
    render: (val) => {
      return val ?? '-'
    },
  },
  {
    title: '逾期金额(元)',
    width: 100,
    dataIndex: 'overdueAmount',
    render: (val) => <AmountFormat value={val} />,
  },
  {
    title: '逾期天数',
    width: 100,
    dataIndex: 'overdueDays',
    render: (val) => {
      return val ?? '-'
    },
  },
  {
    title: '本季度分类结果',
    dataIndex: 'classifyResult',
    matchOption: 'assetClassifySuggestEnum',
  },
]

export const RESULT_COLUMNS = ({ isAdjust, setIsAdjust } = {}) =>
  [
    {
      title: '初分结果',
      dataIndex: 'initClassifyResult',
      matchOption: 'assetClassifySuggestEnum',
      editable: false,
    },
    {
      title: '定性调整',
      dataIndex: 'suggestFlag',
      requiredMark: true,
      matchOption: 'yesOrNo',
      editable: {
        element: (
          <Select
            options={'yesOrNo'}
            onChange={(value) => {
              setIsAdjust(value === 1)
            }}
          ></Select>
        ),
        required: true,
        rules: [rules.required()],
      },
    },
    isAdjust && {
      title: '建议分类',
      requiredMark: true,
      dataIndex: 'suggestResult',
      matchOption: 'assetClassifySuggestEnum',
      editable: {
        options: 'assetClassifySuggestEnum',
        required: true,
        rules: [rules.required()],
      },
    },

    isAdjust && {
      title: '调整原因',
      dataIndex: 'suggestReason',
      requiredMark: true,
      span: 2,
      render: (val) => {
        return val ?? '-'
      },
      editable: () => TextAreaEditable({ required: true }),
    },
    // {
    //   title: '拨备计提',
    //   requiredMark: true,
    //   dataIndex: 'awardRatio',
    //   editable: (record, rowIndex) => {
    //     return AmountEditable(record, 'awardRatio', {
    //       required: true,
    //       disabled: false,
    //       inputConfig: {
    //         min: -9999,
    //         addonAfter: '%',
    //         formatter: undefined,
    //       },
    //     })
    //   },
    //   render: (value) => {
    //     return hasValue(value) ? PureAmountFormat(value) + '%' : '-'
    //   },
    // },

    {
      title: '租后检查报告',
      dataIndex: 'checkName',
      render: (val, { checkId, checkName }) => {
        if (checkId) {
          return (
            <a
              // target="_blank"
              // href={`/afterLease/checkPlan/template/${checkId}`}
              onClick={() => {
                history.push(`/afterLease/checkPlan/template/${checkId}`)
              }}
            >
              {checkName}
            </a>
          )
        }
        return '-'
      },
      editable: false,
    },
    {
      title: '备注',
      dataIndex: 'remark',
      span: 2,
      render: (val) => {
        return val ?? '-'
      },
      editable: () => TextAreaEditable(),
      render: (val) => <FiledFormat title={val} />,
    },
  ].filter(Boolean)
