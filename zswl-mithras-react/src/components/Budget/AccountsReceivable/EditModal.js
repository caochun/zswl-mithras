import { history, observer } from '@zswl/admin'
import { Form, Modal, DatePicker } from '@zswl/components'
import dayjs from 'dayjs'

/**
 * 创建报送计划弹窗组件
 * @param {Object} props - 组件属性
 * @param {Object} props.store - ModalStore实例
 */
const EditModal = ({ store }) => {
  const layout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 18 },
  }

  return (
    <Modal
      store={store}
      propsBy={() => ({
        title: '创建报送计划',
        width: 400,
      })}
      destroyOnClose
      onCancel={() => {
        history?.replace('/budget/accountsReceivable')
        store.close()
      }}
    >
      <Form {...layout}>
        <Form.Item
          label="计划月份"
          name="planDate"
          rules={[{ required: true, message: '请选择计划月份！' }]}
        >
          <DatePicker
            style={{ width: '100%' }}
            picker="month"
            format="YYYY年MM月"
            placeholder="请选择月份"
            disabledDate={(current) => {
              // 禁用未来月份
              return current && current > dayjs().endOf('month')
            }}
          />
        </Form.Item>
        <div
          style={{
            fontSize: '12px',
            color: '#999',
            marginTop: '-16px',
            marginBottom: '16px',
            paddingLeft: '25%',
          }}
        >
          注：只能选择当前月份及之前的月份
        </div>
      </Form>
    </Modal>
  )
}

export default observer(EditModal)
