import { observer } from '@zswl/admin'
import Api from '@/api/afterLease/level5Classify'
import { amountFormat, getKeyOptionsLabelMapPlus, formatPercent, hasValue } from '@/utils'
import { EditTable } from '@/components/Table'

const Index = ({ id }) => {
  const getList = async (params) => {
    const list = await Api.postWithdrawalRatio({
      ...params,
      id,
      isAuxiliary: true,
    })
    return { list }
  }

  return (
    <EditTable
      columnWidth={160}
      scroll={{ x: 1000 }}
      rowKey="receiptId"
      tableStoreConfig={{
        pagination: false,
      }}
      tableApi={getList}
      canEdit={false}
      title=" "
      columns={[
        {
          title: '借据编号',
          dataIndex: 'receiptCode',
        },
        {
          title: '合同编号',
          dataIndex: 'contractCode',
          width: 280,
        },
        {
          title: '业务类型',
          dataIndex: 'bizType',
          render: (item) => {
            return getKeyOptionsLabelMapPlus('projEstablishBizType')[item]
          },
        },
        {
          title: '剩余租期',
          dataIndex: 'remainingPhase',
          align: 'right',
        },
        {
          title: '投放金额(元)',
          dataIndex: 'deliveryAmount',
          width: 140,
          align: 'right',

          render: (val) => {
            return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
          },
        },
        {
          title: '存量风险敞口(元)',
          dataIndex: 'stockExposure',
          width: 160,
          align: 'right',
          render: (val) => {
            return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
          },
        },
        {
          title: '计提比例',
          dataIndex: 'withdrawalRatio',
          render: (value) => {
            return hasValue(value) ? formatPercent(value) + '%' : '-'
          },
        },
      ]}
    ></EditTable>
  )
}

export default observer(Index)
