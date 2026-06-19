import { observer } from '@zswl/admin'
import { Form, Modal, Select, App } from '@zswl/components'
import store from '../store'

const { Item } = Form

function ChangeModal() {
  const [form] = Form.useForm()
  const { optionsType } = App.getData()

  return (
    <Modal title={'合同变更'} store={store.$changeModal} okText={'确定'} destroyOnClose>
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        <Item
          label={'变更类型'}
          name={'changeType'}
          rules={[{ required: true, message: '请选择变更类型！' }]}
        >
          <Select
            options={optionsType.contractChangeTypeEnum.filter((item) => item.label !== '提前还款')}
            debounceSearch
          />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(ChangeModal)
