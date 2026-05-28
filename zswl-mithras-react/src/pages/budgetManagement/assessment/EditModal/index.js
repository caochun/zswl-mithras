import { observer } from '@zswl/admin'
import { App, Form, Modal, Select } from '@zswl/components'
import store from '../store'
import { debounce as _debounce } from 'lodash'
import { DatePicker } from 'antd'

const { Item } = Form
function EditModal() {
  const [form] = Form.useForm()

  return (
    <Modal title={'创建预算考核'} store={store.createModal} okText={'确定'} destroyOnClose>
      <Form form={form} labelCol={{ span: 6 }} preserve={true}>
        <Item
          label={'考核月份'}
          name={'yearAndMonth'}
          rules={[{ required: true, message: '请选择考核月份！' }]}
        >
          <DatePicker format="YYYY-MM" valueFormat="YYYY/MM" picker={'month'} allowClear={false} />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(EditModal)
