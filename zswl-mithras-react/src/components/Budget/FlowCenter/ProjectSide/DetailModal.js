import { getTableColumns } from '@/utils'
import { observer } from '@zswl/admin'
import { Modal, Table } from '@zswl/components'
import { useMemo, useState } from 'react'
import ALL_COLUMNS from '../Columns'
import { Checkbox } from 'antd'
import { saveServer } from '@/utils'

const BudgetFlowCenterProjectSideDetailModal = ({ store }) => {
  const [isHide, setIsHide] = useState(false)
  const isPayment = store.radioValue === 'payment'
  const columns = useMemo(() => {
    return getTableColumns(
      ALL_COLUMNS,
      [
        isPayment ? '付款方式' : '收款方式',
        isPayment && '实付日期',
        isPayment && '付款金额（元）',
        !isPayment && '实收日期',
        !isPayment && '实收金额(元)',
        !isPayment && '本⾦（元）',
        !isPayment && '利息（元）',
        !isPayment && '罚息（元）',

        isHide && '票据号',
        isHide && '票据面额（元）',
        isHide && '票据到期日',
        isHide && !isPayment && '票据买入价',
        isPayment ? isHide && '核销方式' : '核销方式',
        '交易明细编号',
        '合同编号',
        '操作日期',
        '操作人',
      ].filter(Boolean)
    )
  }, [isHide, isPayment])

  return (
    <Modal title={'结算明细'} store={store.detailModal} footer={null} width={600} destroyOnClose>
      <Table
        columnsFilter="flowCenter_ProjectSide_DetailModal"
                onFilter={(key,val) => saveServer('flowCenter_ProjectSide_DetailModal',val)}

        store={store.detailTable}
        columns={columns}
        editable={false}
        serial={{
          width: 40,
        }}
        scroll={{ x: 'auto' }}
        columnWidth={80}
        extra={[
          <Checkbox checked={isHide} onChange={(e) => setIsHide(e.target.checked)}>
            影响票据信息
          </Checkbox>,
        ]}
      />
    </Modal>
  )
}

export default observer(BudgetFlowCenterProjectSideDetailModal)
