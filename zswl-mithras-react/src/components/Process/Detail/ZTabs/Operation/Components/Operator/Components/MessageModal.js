import { observer } from '@zswl/admin'
import { Input } from 'antd'
import { Modal, Form } from '@zswl/components'

// 风控经理:评审流程提交时检验客户管理模块财务报表录入是否完整
const ProcessMessageModal = ({ store }) => {
  return (
    <Modal title="提示" store={store.messageModal}>
      <Form>
        <Form.Item name={'message'} label="审批意见">
          <Input.TextArea />
        </Form.Item>
      </Form>
    </Modal>
  )
}
ProcessMessageModal.methods = {}

export default observer(ProcessMessageModal)
