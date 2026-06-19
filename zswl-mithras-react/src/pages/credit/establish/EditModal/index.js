import { observer } from '@zswl/admin'
import { App, Form, Modal, Select } from '@zswl/components'
import store from '../store'
import { Input } from 'antd'
import { useEffect, useState } from 'react'
import { debounce as _debounce } from 'lodash'
import groupCreditEstablishApi from '@/api/credit/groupCreditEstablishApi'

const { Item } = Form
function EditModal() {
  const options = App.getData().optionsType
  const [form] = Form.useForm()
  const [clientList, setClientList] = useState([])
  const searchClient = _debounce(async (e, clientType) => {
    const { list } = await groupCreditEstablishApi.getClientList({
      clientName: e,
      clientType,
      effected: true,
    })
    setClientList(list)
  }, 500)

  useEffect(() => {
    searchClient('', 'CORPORATION')
  }, [])
  // useEffect(() => {
  //   form.setFieldsValue({
  //     approvalType: 'NORMAL',
  //   })
  // }, [form])

  return (
    <Modal title={'授信立项创建'} store={store.createModal} okText={'确定'} destroyOnClose>
      <Form form={form} labelCol={{ span: 6 }} preserve={true}>
        <Item
          label={'客户名称'}
          name={'clientId'}
          rules={[{ required: true, message: '请选择客户名称！' }]}
        >
          <Select
            options={clientList}
            onSearch={(e) => searchClient(e, 'CORPORATION')}
            filterOption={false}
            fieldNames={{ label: 'clientName', value: 'id' }}
          />
        </Item>
        <Item
          label={'授信名称'}
          name={'projName'}
          rules={[{ required: true, message: '请输入授信名称！' }]}
        >
          <Input placeholder={'请输入'} />
        </Item>
        {/* <Item
          label={'审批类型'}
          name={'approvalType'}
          rules={[{ required: true, message: '请选择审批类型！' }]}
        >
          <Select options={options.projEstablishApprovalType} />
        </Item> */}
      </Form>
    </Modal>
  )
}

export default observer(EditModal)
