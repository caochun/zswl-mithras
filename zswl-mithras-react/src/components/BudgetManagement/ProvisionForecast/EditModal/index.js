import { observer } from '@zswl/admin'
import { Form, Modal } from '@zswl/components'
import store from '../List/store'
import { DatePicker, Input } from 'antd'
import moment from 'moment'

const { Item } = Form

/**
 * 创建拨备预测计划弹窗组件
 * 用于创建新的拨备预测计划
 */
function EditModal() {
  const [form] = Form.useForm()

  return (
    <Modal title={'创建拨备预测计划'} store={store.createModal} okText={'确定'} destroyOnClose>
      <Form form={form} labelCol={{ span: 8 }}>
        <Item
          label={'拨备预测计划名称'}
          name={'budgetPlanName'}
          rules={[{ required: true, message: '请输入拨备预测计划名称！' }]}
        >
          <Input placeholder="请输入拨备预测计划名称" />
        </Item>
        <Item
          label={'选择拨备预测日期'}
          name={'predictDataFrom'}
          rules={[{ required: true, message: '请选择拨备预测日期！' }]}
          transform={(value) => value && moment(value).format('YYYY-MM-DD')}
        >
          <DatePicker
            format="YYYY-MM-DD"
            valueFormat="YYYY-MM-DD"
            allowClear={false}
            placeholder="请选择拨备预测日期"
            style={{ width: '100%' }}
          />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(EditModal)
