import { observer } from '@zswl/admin'
import { App, Form, Modal } from '@zswl/components'
import store from './store'
import { Input } from 'antd'
import { rules } from '@/utils'

const { Item } = Form
function EditModal() {
  const [form] = Form.useForm()

  return (
    <Modal
      title={'新增机构'}
      store={store.createModal}
      okText={'确定'}
      onOk={form.submit}
      destroyOnClose
    >
      <Form form={form} labelCol={{ span: 10 }} preserve={true}>
        <Item
          label={'担保机构名称'}
          name={'guaranteeAgencyName'}
          rules={[{ required: true, message: '请输入担保机构名称！' }]}
        >
          <Input placeholder={'请输入担保机构名称'} />
        </Item>
        <Item
          label={'统一社会信用代码'}
          name={'uscCode'}
          rules={[{ required: true, message: '请输入统一社会信用代码！' }, rules.creditCode()]}
        >
          <Input placeholder={'请输入统一社会信用代码'} />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(EditModal)
