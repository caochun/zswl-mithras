import { AmountColumn, DateColumn, InputColumn, MatchOptionColumn } from '@/components/Format'
import { observer } from '@zswl/admin'
import { Modal, Table } from '@zswl/components'
import { Checkbox } from 'antd'
import { useState } from 'react'
import { saveServer } from '@/utils'

const Index = ({ store }) => {
  const [isHide, setIsHide] = useState(false)
  const isPayment = store.flowType === 'PAY'
  const columns = [
    MatchOptionColumn({
      title: '付款方式',
      dataIndex: 'settleMethod',
      matchOption: 'paymentMethod',
      width: 100,
    }),
    { title: '实付日期', dataIndex: 'cashFlowDate' },
    AmountColumn({ title: '付款金额（元）', dataIndex: 'totalAmount' }),
    AmountColumn({ title: '本⾦（元）', dataIndex: 'principalAmount' }),
    AmountColumn({ title: '利息（元）', dataIndex: 'interestAmount' }),

    isHide && InputColumn({ title: '票据号', dataIndex: 'billCode' }),
    isHide && AmountColumn({ title: '票据面额（元）', dataIndex: 'billAmount' }),
    isHide && DateColumn({ title: '票据到期日', dataIndex: 'billExpireDate' }),
    !isPayment && AmountColumn({ title: '票据买入价', dataIndex: 'billBuyRate', suffix: '%' }),
    InputColumn({ title: '交易明细编号', dataIndex: 'bankFlowNo' }),
    { title: '核销方式', dataIndex: 'dataSource' },
    { title: '操作日期', dataIndex: 'operateDate' },
    { title: '操作人', dataIndex: 'operatorName' },
  ]

  return (
    <Modal title={'结算明细'} store={store.detailModal} footer={null} width={600} destroyOnClose>
      <Table
        columnsFilter="flowCenter_Fundamentals_DetailModal"
        onFilter={(key,val) => saveServer('flowCenter_Fundamentals_DetailModal',val)}

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

export default observer(Index)
