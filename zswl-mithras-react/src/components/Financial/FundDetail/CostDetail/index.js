import { useMemo } from 'react'
import { Button, Table } from '@zswl/components'
import { Space, Checkbox } from 'antd'
import { observer } from '@zswl/admin'
import IconFont from '@/components/Icon'
import CreateModal from './CreateModal'
import Store from './store'
import styles from './index.less'
import { AmountColumn, MatchOptionColumn } from '@/components/Format'
import { saveServer } from '@/utils'

const FinancialFundCostDetail = ({
  financingId,
  isFormApproval,
  businessVersion,
  canEdit = true,
  baseInfoData: detail,
  baseStore,
}) => {
  const store = useMemo(() => {
    return new Store({ businessVersion, isFormApproval, financingId, detail, baseStore })
  }, [businessVersion, isFormApproval, financingId, detail, baseStore])
  const isYT = detail.businessType === 'SYNDICATIONS'
  const columns = useMemo(() => {
    return [
      isYT && { title: '融资机构', dataIndex: 'organizationName' },
      MatchOptionColumn({
        title: '费用类型',
        width: 200,
        dataIndex: 'expenseType',
        matchOption: 'financingFeeType',
      }),
      AmountColumn({ title: '金额（元）', dataIndex: 'amount' }),
      { title: '支付方式', dataIndex: 'paymentMethod' },
      { title: '支付时间', dataIndex: 'payDate' },
      { title: '备注', dataIndex: 'remark' },
      MatchOptionColumn({
        title: '核销状态',
        dataIndex: 'writeOffStatus',
        matchOption: 'fundReceiptRepayCashFlowState',
        editable: false,
      }),
      canEdit && {
        // {
        title: '操作',
        dataIndex: 'id',
        width: 180,
        fixed: 'right',
        actions(record) {
          return [
            {
              name: '编辑',
              onClick: () => store.$createModal.open(record),
              disabled: !canEdit,
            },
            { name: '删除', onClick: () => store.remove(record), disabled: !canEdit },
          ]
        },
      },
    ]
  }, [canEdit, store])

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>费用明细</div>
        <Space>
          <Button
            onClick={store.$createModal.open}
            type="primary"
            icon={<IconFont type="icon-icon_add" />}
            disabled={!canEdit}
          >
            新增
          </Button>
        </Space>
      </div>
      <Table         columnsFilter={'detail_CostDetail_1'}
        onFilter={(key,val) => saveServer('detail_CostDetail_1',val)} store={store.$table} columns={columns} columnWidth={120} resizable></Table>
      <CreateModal store={store} financingId={financingId} detail={detail}></CreateModal>
    </div>
  )
}

export default observer(FinancialFundCostDetail)
