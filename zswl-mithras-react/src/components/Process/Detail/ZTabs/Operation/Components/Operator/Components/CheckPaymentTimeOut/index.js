import { observer } from '@zswl/admin'
import { Space, Radio, message } from 'antd'
import { Modal, Button, Form } from '@zswl/components'
import { rules } from '@/utils'

// 放款审核:付款申请距离最新评审流程（如有变更取评审变更）审批通过日期超 6 个月
const Index = (props) => {
  const [form] = Form.useForm()
  const { store } = props

  const submit = async () => {
    form.validateFields().then(async (data) => {
      if (data.isUpload === 2) {
        message.info('请上传补充报告后再提交流程！')
        return
      }
      const { submit: submitFn } = store.checkPaymentTimeOutMethods
      await submitFn?.()
      store.checkPaymentTimeOut.close()
    })
  }
  return (
    <div>
      <Modal
        title="提示"
        store={store.checkPaymentTimeOut}
        footer={
          <Space>
            <Button onClick={store?.checkPaymentTimeOut.close}>取消</Button>
            <Button onClick={submit} type="primary">
              确定
            </Button>
          </Space>
        }
      >
        <div style={{ marginBottom: 20 }}>
          距离最新项目评审流程审批通过日期已超过6 个月，是否已上传补充报告？
        </div>
        <Form form={form}>
          <Form.Item name="isUpload" rules={[rules.required('请选择')]}>
            <Radio.Group>
              <Radio value={1}>是</Radio>
              <Radio value={2}>否</Radio>
            </Radio.Group>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
Index.methods = {}

export default observer(Index)
