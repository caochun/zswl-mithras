import { FiledFormat, PureAmountFormat } from '@/components/Format'
import { ContractBizTypeMapText as bizTypeMapText } from '@/components/Contract/DetailEntries'

const filedRender = (val, { isAmountFormat, transformResult = (v) => v || '-' } = {}) => {
  const value = val?.value !== undefined ? val?.value : val
  let formatVal = isAmountFormat ? PureAmountFormat(value) : value
  formatVal = transformResult(formatVal)
  return <FiledFormat title={formatVal} isChange={val?.isChange} needBar={false} />
}

export const useColumn = (bizType) => {
  const column = [
    {
      title: '现金流编号',
      width: 200,
      dataIndex: 'cashFlowCode',
      render: (val) => filedRender(val),
    },
    {
      title: '日期',
      width: 120,
      dataIndex: 'date',
      render: (val) => filedRender(val),
    },
    {
      title: '期项',
      dataIndex: 'phase',
      render: (val) => val.value ?? val,
    },
    {
      title: `${bizTypeMapText[bizType]?.rentText}(元)`,
      dataIndex: 'rent',
      align: 'right',
      render: (val) => filedRender(val, { isAmountFormat: true }),
    },
    {
      title: '本金(元)',
      align: 'right',
      dataIndex: 'principal',
      render: (val) => filedRender(val, { isAmountFormat: true }),
    },
    {
      title: '利息(元)',
      align: 'right',
      dataIndex: 'interest',
      render: (val) => filedRender(val, { isAmountFormat: true }),
    },
    {
      title: '剩余本金(元)',
      align: 'right',
      dataIndex: 'remainingPrincipal',
      render: (val) => filedRender(val, { isAmountFormat: true }),
    },
    {
      title: '收款时间',
      dataIndex: 'receivedDate',
      width: 120,
      render: (val) => filedRender(val, {}),
    },
    {
      title: '收款金额(元)',
      dataIndex: 'receivedAmount',
      render: (val) =>
        filedRender(val, {
          isAmountFormat: true,
        }),
    },
    {
      title: '是否已收款',
      dataIndex: 'received',
      width: 180,
      render: (val) =>
        filedRender(val, {
          transformResult: (v) => {
            if (v == null) return '-'
            return v ? '是' : '否'
          },
        }),
    },
  ]
  return column
}
