import { observer } from '@zswl/admin'
import DataUpload from '@/components/DataUpload'
import { Form, Modal } from '@zswl/components'
import { DatePicker } from 'antd'

const { Item } = Form

function Index({ store }) {
  const [form] = Form.useForm()

  return (
    <Modal title={`基础数据导入`} store={store.uploadModal} destroyOnClose>
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        <Item label={'年份'} name={'year'} rules={[{ required: true, message: '请选择日期!' }]}>
          <DatePicker.YearPicker placeholder={'请选择'} style={{ width: 160 }} />
        </Item>
        <Item
          label={'基础数据表'}
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
