import { useState } from 'react'
import { Descriptions } from '@zswl/components'
import { observer } from '@zswl/admin'
import fundReceiptRepayBaseInfoApi from '@/api/financial/fundReceiptRepayBaseInfoApi'
import { message } from 'antd'
import { AmountColumn, InputColumn, MatchOptionColumn, TextAreaColumn } from '@/components/Format'
import { getCompareValue } from '@/utils'

function FinancialPaymentDirectBaseInfo({ id, isFormApproval, canEdit }) {
  const [editable, setEditable] = useState(false)
  const store = Descriptions.useStore({
    request: async () => {
      const api = isFormApproval
        ? fundReceiptRepayBaseInfoApi.directDetailBaseInfoCompare
        : fundReceiptRepayBaseInfoApi.directDetailBaseInfo
      return await api({ id })
    },
  })
  // 保存
  const save = async () => {
    const values = await store.submit()
    await fundReceiptRepayBaseInfoApi.postInfoModify({
      id,
      ...values,
    })
    setEditable(false)
    message.success('保存成功')
    store.init()
  }
  return (
    <Descriptions
      title="基本信息"
      store={store}
      editable={editable}
      labelStyle={{ width: 220 }}
      extra={
        canEdit && [
          {
            name: '取消',
            hidden: !editable,
            onClick: () => setEditable(false),
          },
          { name: '保存', hidden: !editable, type: 'primary', onClick: save },
          {
            name: '编辑',
            type: 'primary',
            hidden: editable,
            onClick: () => setEditable(true),
          },
        ]
      }
      items={[
        InputColumn({ title: '产品名称', dataIndex: 'productName', editable: false }),
        InputColumn({ title: '融资编号', dataIndex: 'financingCode', editable: false }),
        AmountColumn({
          title: '发行规模（万元）',
          dataIndex: 'issuingScale',
          requiredMark: true,
          editable: false,
          initFormat: 10000 * 10000,
        }),
        MatchOptionColumn({
          title: '项目类别',
          dataIndex: 'directFinancingType',
          editable: false,
          requiredMark: true,
          matchOption: 'directFinancingType',
        }),
        InputColumn({
          title: '交易流通场所',
          dataIndex: 'tradingVenues',
          requiredMark: true,
          editable: false,
        }),
        MatchOptionColumn({
          title: '发行方式',
          dataIndex: 'issuanceMethod',
          requiredMark: true,
          editable: false,
        }),
        InputColumn({
          title: '存续时间',
          dataIndex: 'duration',
          requiredMark: true,
          editable: false,
          render: (val, { durationFrom, durationTo }) =>
            `${getCompareValue(durationFrom) ?? ''}~${getCompareValue(durationTo) ?? ''}`,
        }),
        InputColumn({
          title: '首个兑付日',
          dataIndex: 'firstPaymentDate',
          editable: false,
          requiredMark: true,
          dateFormat: 'YYYY-MM-DD',
        }),
        TextAreaColumn({
          title: '备注',
          dataIndex: 'remark',
          editable: true,
        }),
        InputColumn({ title: '资金经理', dataIndex: 'fundManagerName', editable: false }),
        InputColumn({ title: '所属部门', dataIndex: 'deptName', editable: false }),
        InputColumn({ title: '部门负责人', dataIndex: 'bizHeaderName', editable: false }),
        InputColumn({ title: '分管领导', dataIndex: 'leaderName', editable: false }),
      ]}
    />
  )
}

export default observer(FinancialPaymentDirectBaseInfo)
