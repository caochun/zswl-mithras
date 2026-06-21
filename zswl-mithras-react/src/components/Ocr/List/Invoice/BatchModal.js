import { observer } from '@zswl/admin'
import { DatePicker, Form, Input, Modal } from '@zswl/components'

const OcrInvoiceBatchModal = ({ store }) => {
  const { modalType } = store?.getInitialValues() ?? {}
  const isBatch = modalType === 'batch'
  return (
    <Modal title={isBatch ? '批量修改' : '修改'} store={store} destroyOnClose>
      <Form>
        <Form.Item label="发票号码" name="invoiceNo">
          <Input />
        </Form.Item>
        <Form.Item
          label="开票日期"
          name="invoiceIssueDate"
          transform={(val) => ({
            invoiceIssueDate: val && moment(val).format('yyyy-MM-DD'),
          })}
        >
          <DatePicker />
        </Form.Item>

        <Form.Item label="购买方" name="invoicePayerName">
          <Input />
        </Form.Item>
        <Form.Item label="销售方" name="invoiceSellerName">
          <Input />
        </Form.Item>
        <Form.Item label="开票内容" name="invoiceGoods">
          <Input />
        </Form.Item>
        <Form.Item label="规格型号" name="invoicePlateSpecific">
          <Input />
        </Form.Item>
        <Form.Item label="单位" name="invoiceElectransUnit">
          <Input />
        </Form.Item>
        <Form.Item label="单位" name="vatInvoiceIds" hidden>
          <Input />
        </Form.Item>

        {isBatch && <p>温馨提示：批量修改后所选行该字段下信息全部统一为修改内容，请谨慎操作！</p>}
      </Form>
    </Modal>
  )
}
export default observer(OcrInvoiceBatchModal)
