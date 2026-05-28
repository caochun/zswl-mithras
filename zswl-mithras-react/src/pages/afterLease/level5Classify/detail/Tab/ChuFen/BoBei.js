import { observer, getQuery } from '@zswl/admin'
import Api from '@/pages/afterLease/level5Classify/api'
import { AmountEditable } from '@/components/Format'
import {
  amountFormat,
  getKeyOptionsLabelMapPlus,
  formatPercent,
  hasValue,
  isSecretaryjury,
  isAssetJon,
} from '@/utils'
import { EditTable } from '@/components/Table'
import { useMemo } from 'react'

const Index = ({ canEdit = true, id }) => {
  const { modelKey, curTaskActivityIds } = getQuery()

  const getList = async (params) => {
    const list = await Api.postWithdrawalRatio({
      ...params,
      id,
    })
    return { list }
  }
  const saveData = async (list, values) => {
    const configValue = []
    list.forEach((item, index) => {
      configValue.push({
        receiptId: item.receiptId,
        receiptCode: item.receiptCode,
        contractId: item.contractId,
        withdrawalRatio: +values[item?.receiptId]?.withdrawalRatio * 10000,
      })
    })
    await Api.postWithdrawalRatioModify({
      id,
      withdrawalRatios: configValue,
    })
  }

  const canEditFlag = useMemo(() => {
    // 在评审会流程秘书汇总节点可编辑详情页拨备计提模块，修改拨备计提比例；
    // 风委会流程资产管理岗调整节点可编辑详情页拨备计提模块，修改拨备计提比例。
    const canEditInFlow =
      (modelKey === 'AssetClassifyReviewMeetingFlow' &&
        curTaskActivityIds === 'userTask_secretary' &&
        isSecretaryjury()) ||
      (modelKey === 'AssetClassifyRiskMeetingFlow' &&
        curTaskActivityIds === 'userTask_assetManager' &&
        isAssetJon())
    return canEdit || canEditInFlow
  }, [canEdit, modelKey, curTaskActivityIds])

  return (
    <EditTable
      scroll={{
        x: 1300,
      }}
      rowKey="receiptId"
      tableStoreConfig={{
        pagination: false,
      }}
      tableApi={getList}
      saveData={saveData}
      canEdit={canEditFlag}
      title="拨备计提"
      columns={[
        {
          title: '借据编号',
          dataIndex: 'receiptCode',
          width: 140,
          editable: false,
        },
        {
          title: '合同编号',
          dataIndex: 'contractCode',
          width: 280,
          editable: false,
        },
        {
          title: '业务类型',
          width: 100,
          dataIndex: 'bizType',
          editable: false,
          render: (item) => {
            return getKeyOptionsLabelMapPlus('projEstablishBizType')[item]
          },
        },
        {
          title: '剩余租期',
          dataIndex: 'remainingPhase',
          align: 'right',
          editable: false,
        },
        {
          title: '投放金额(元)',
          dataIndex: 'deliveryAmount',
          width: 140,
          align: 'right',
          editable: false,
          render: (val) => {
            return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
          },
        },
        {
          title: '存量风险敞口(元)',
          dataIndex: 'stockExposure',
          width: 140,
          align: 'right',
          editable: false,
          render: (val) => {
            return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
          },
        },
        {
          title: '计提比例',
          dataIndex: 'withdrawalRatio',
          editable: (val) =>
            AmountEditable(val, 'withdrawalRatio', {
              required: true,
              disabled: false,
              inputConfig: {
                addonAfter: '%',
              },
            }),
          render: (value) => {
            return hasValue(value) ? formatPercent(value) + '%' : '-'
          },
        },
      ]}
    ></EditTable>
  )
}

export default observer(Index)
