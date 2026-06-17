import { Form, Modal, Input } from '@zswl/components'
import { observer } from '@zswl/admin'
import { ApiSelect, FounderSelect } from '@/components'
import { getUserInfo } from '@/utils'
import Api from '../api'

const HandoverModal = ({ store }) => {
  return (
    <Modal title="客户移交" store={store.handoverModal} destroyOnClose>
      <Form
        labelCol={{ span: 8 }}
        preserve={false}
        initialValues={{
          belongSponsor: {
            label: getUserInfo().userName,
            value: getUserInfo().id,
          },
        }}
      >
        <Form.Item label={'客户当前所属主办'} name={'belongSponsor'}>
          <FounderSelect disabled></FounderSelect>
        </Form.Item>
        <Form.Item label={'客户当前所属部门'} name={'belongDept'} rules={[{ required: true }]}>
          <ApiSelect api={Api.postNewOrgs} labelInValue></ApiSelect>
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default observer(HandoverModal)
