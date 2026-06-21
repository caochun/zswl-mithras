import { observer } from '@zswl/admin'
import { DatePicker, Form, Input, Modal } from '@zswl/components'

const OcrCarCardBatchModal = ({ store }) => {
  const { modalType } = store?.getInitialValues() ?? {}
  const isBatch = modalType === 'batch'
  return (
    <Modal title={isBatch ? '批量修改' : '修改'} store={store} destroyOnClose>
      <Form>
        <Form.Item label="车牌号" name="vehicleRegistrationNumber">
          <Input />
        </Form.Item>
        <Form.Item label="机动车所有人" name="vehicleRegistrationOwner">
          <Input />
        </Form.Item>
        <Form.Item label="车架号" name="vehicleVin">
          <Input />
        </Form.Item>
        <Form.Item label="制造商" name="vehicleManufacturer">
          <Input />
        </Form.Item>

        {isBatch && <p>温馨提示：批量修改后所选行该字段下信息全部统一为修改内容，请谨慎操作！</p>}
      </Form>
    </Modal>
  )
}
export default observer(OcrCarCardBatchModal)
