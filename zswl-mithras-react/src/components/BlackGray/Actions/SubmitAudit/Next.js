import { Modal, Select, Form } from 'antd'

const { Item } = Form
function Index({ visible, onCancel, info, onFinish, loading }) {
  const [form] = Form.useForm()
  const { nextActivityId, nodeInfo = {} } = info || {}
  const { selectUsers, orgName, roleName } = nodeInfo[nextActivityId] || {}
  return (
    <Modal
      title={'提交审批'}
      destroyOnClose
      open={visible === 1}
      onCancel={onCancel}
      onOk={form.submit}
      okButtonProps={{ loading }}
    >
      <Form preserve={false} form={form} onFinish={onFinish}>
        <Item label={'下一位审批人'} name={'auditUser'} rules={[{ required: true }]}>
          <Select
            placeholder={'请选择下一位审批人'}
            options={selectUsers}
            fieldNames={{ label: 'userName', value: 'account' }}
          />
        </Item>
        <Item label={'所属机构'}>{orgName}</Item>
        <Item label={'所属角色'}>{roleName}</Item>
      </Form>
    </Modal>
  )
}
export default Index
