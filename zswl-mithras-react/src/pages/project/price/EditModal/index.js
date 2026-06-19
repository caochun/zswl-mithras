import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { Input, Tabs } from 'antd'
import { debounce as _debounce } from 'lodash'
import Api from '@/api/project/projectPricingApi'

const { Item } = Form

function EditModal({ store }) {
  const [form] = Form.useForm()

  const onProjectChang = (val, options) => {
    if (options) {
      const { clientName, bizType } = options
      form.setFieldsValue({
        clientName,
        bizType: store.getKeyOptionsLabelMap('projEstablishBizType')[bizType],
      })
    }
  }
  return (
    <Modal title={'发起定价'} store={store.createModal} okText={'确定'} destroyOnClose>
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        <Item
          label={'选择项目'}
          name={'projName'}
          rules={[{ required: true, message: '请选择项目！' }]}
        >
          <Select
            debounceSearch
            labelInValue
            allowClear
            style={{ maxWidth: '100%' }}
            options={async (projName) => {
              const res = await Api.getEffectiveReviewList({ projName, projReviewStatus: 'TAKE_EFFECT' })
              return res.list
            }}
            onChange={onProjectChang}
            fieldNames={{ label: 'projName', value: 'id' }}
          />
        </Item>
        <Item
          label={'客户名称'}
          name={'clientName'}
          rules={[{ required: true, message: '请选择客户名称！' }]}
        >
          <Input disabled />
        </Item>
        <Item
          label={'业务类型'}
          name={'bizType'}
          disabled
          rules={[{ required: true, message: '请选择业务类型！' }]}
        >
          <Input disabled />
        </Item>
        <Item name={'curtab'} hidden>
          <Input />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(EditModal)
