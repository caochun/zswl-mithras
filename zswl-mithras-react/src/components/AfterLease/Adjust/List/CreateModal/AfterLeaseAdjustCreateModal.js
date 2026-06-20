import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { Input } from 'antd'

const { Item } = Form

function CreateModal({ store }) {
  const { projectList, getProject, curAction } = store
  const [form] = Form.useForm()

  const onSearch = (value) => {
    getProject(value)
  }

  const onChange = (record) => {
    if (!record) {
      form.setFieldsValue({
        clientName: undefined,
      })
      getProject()
      return
    }
    const curProj = projectList.filter((item) => item.id === record.key)[0]

    form.setFieldsValue({
      clientName: curProj.clientNames[0],
      projId: curProj.id,
    })
  }

  return (
    <Modal title={curAction.title} store={store.createModal} okText={'确定'} destroyOnClose>
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        <Item
          label={'项目名称'}
          name={'projName'}
          rules={[{ required: true, message: '请选择项目！' }]}
        >
          <Select
            placeholder="请输入"
            allowClear
            options={projectList}
            labelInValue
            filterOption={false}
            style={{ maxWidth: '100%' }}
            fieldNames={{ value: 'id', label: 'projName' }}
            onSearch={onSearch}
            onChange={onChange}
          />
        </Item>
        <Item dependencies={['projName']} noStyle>
          {({ getFieldValue }) => {
            const val = getFieldValue('projName')
            if (val) {
              return (
                <Item label={'客户名称'} name={'clientName'}>
                  <Input placeholder={'客户名称'} disabled />
                </Item>
              )
            }
          }}
        </Item>
        <Item name="projId" hidden>
          <Input />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(CreateModal)
