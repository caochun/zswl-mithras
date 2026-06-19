import { AmountColumn, AmountFormat } from '@/components/Format'
import { observer } from '@zswl/admin'
import { Form, Input, Modal, Table } from '@zswl/components'
import DataUpload from '@/components/DataUpload'
import { saveServer } from '@/utils'

const Index = ({ modal, table }) => {
  const columns = [
    /*
    客户名称	合同编号	借据编号	期项	合同金额	应收日期	应收金额	实收金额	罚息计算截止日期	应收罚息	申请减免罚息金额	减免后应收罚息 */
    { title: '客户名称', dataIndex: 'clientName', editable: false, width: 200 },
    { title: '合同编号', dataIndex: 'contractCode', editable: false, width: 200 },
    { title: '借据编号', dataIndex: 'receiptCode', editable: false, width: 200 },
    { title: '期项', dataIndex: 'phase', editable: false },
    AmountColumn({ title: '合同金额', dataIndex: 'applyCreditAmount', editable: false }),
    { title: '应收日期', dataIndex: 'planCollectionDate', editable: false },
    AmountColumn({ title: '应收金额', dataIndex: 'planCollectionAmount', editable: false }),
    AmountColumn({ title: '实收金额', dataIndex: 'collectionAmount', editable: false }),
    { title: '罚息计算截止日期', dataIndex: 'penaltyCloseDate', editable: false },
    AmountColumn({ title: '应收罚息', dataIndex: 'penaltyInterest', editable: false }),
    AmountColumn({
      title: '申请减免罚息金额',
      dataIndex: 'reducePenaltyInterest',
      editable: true,
      wrapItemProps: {
        inputConfig: {
          onChange: (e, { dataIndex, index, dataSource }) => {
            const { penaltyInterest } = dataSource
            const reducePenaltyInterest = Math.round(e.replace(/\$\s?|(,*)/g, '') * 10000)
            const arrearsAmount = penaltyInterest - reducePenaltyInterest
            table?.setRowByIndex(index, { ...dataSource, arrearsAmount })
          },
        },
      },
    }),
    AmountColumn({ title: '减免后应收罚息', dataIndex: 'arrearsAmount' }),
    {
      title: '操作',
      dataIndex: 'action',
      actions: (record) => [
        { name: '删除', onClick: () => table.deleteRow(record.uuid), confirm: true },
      ],
    },
  ]
  return (
    <Modal title="罚息减免申请" store={modal} width={900} destroyOnClose>
      <Form>
        <div style={{ fontSize: 16, padding: '8px 0', fontWeight: 500 }}>申请减免罚息金额</div>
        <Table
          columnsFilter={'afterLease_rentCollection_InterestModal'}
                  onFilter={(key,val) => saveServer('afterLease_rentCollection_InterestModal',val)}

          columns={columns}
          store={table}
          resizable
          columnWidth={120}
          editable
          scroll={{ x: 'auto' }}
          rowKey={'uuid'}
        ></Table>
        <div style={{ fontSize: 16, padding: '8px 0', fontWeight: 500 }}>申请原因</div>
        <Form.Item name="notes" rules={[{ required: true, message: '请输入申请原因' }]}>
          <Input.TextArea />
        </Form.Item>
        <Form.Item name="files" label="附件资料">
          <DataUpload accept={'*'}></DataUpload>
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
