import { observer } from '@zswl/admin'
import DataUpload from '@/components/DataUpload'
import { Form, Modal } from '@zswl/components'
import { DatePicker } from 'antd'

const { Item } = Form

function Index({ store }) {
  const [form] = Form.useForm()

  return (
    <Modal title={`导入概算租金表`} store={store.$createModal} destroyOnClose>
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        <Item
          label={'计划起租日'}
          name={'planStartDate'}
          rules={[{ required: true, message: '请选择日期!' }]}
        >
          <DatePicker placeholder={'请选择'} style={{ width: 160 }} />
        </Item>
        <Item
          label={'概算租金表'}
          name={'file'}
          rules={[{ required: true, message: '请上传文件!' }]}
        >
          <DataUpload accept=".xlsx" maxCount={1}></DataUpload>
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
