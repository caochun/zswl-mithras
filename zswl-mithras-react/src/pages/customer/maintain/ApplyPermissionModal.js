import { Form, Input, Modal, Select } from '@zswl/components'
import { observer } from '@zswl/admin'
import { ClientSelect, ApiSelect } from '@/components'
import Api from './api'

const { Item } = Form

const Index = ({ store }) => {
  const [form] = Form.useForm()
  const onClientChange = async (value) => {
    if (!value) {
      form.setFieldsValue({
        belongSponsorName: undefined,
        belongDeptName: undefined,
      })
      return
    }
    const res = await Api.postClientApplyDetail({
      clientId: value,
    })
    form.setFieldsValue({
      belongSponsorName: res?.belongSponsorName || '-',
      belongDeptName: res?.belongDeptName || '-',
    })
  }
  return (
    <Modal title="客户申办权限申请" store={store.applyPermissionModal} destroyOnClose>
      <Form labelCol={{ span: 8 }} preserve={false} form={form}>
        <Item label="权限申请客户名称" name="clientId" key="clientId" rules={[{ required: true }]}>
          <ApiSelect
            debounceSearch
            params={{ page: 1, pageSize: 20 }}
            api={Api.postClientNoauthorityList}
            searchField="clientName"
            transformResult={(res) => res.list}
            onChange={onClientChange}
            fieldNames={{ label: 'clientName', value: 'id' }}
          ></ApiSelect>
        </Item>
        <Item dependencies={['clientId']} noStyle>
          {({ getFieldValue }) => {
            const clientId = getFieldValue('clientId')
            if (!clientId) return null
            return (
              <>
                <Item label={'所属主办'} name={'belongSponsorName'}>
                  <Input disabled></Input>
                </Item>
                <Item label={'所属部门'} name={'belongDeptName'}>
                  <Input disabled></Input>
                </Item>
              </>
            )
          }}
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
