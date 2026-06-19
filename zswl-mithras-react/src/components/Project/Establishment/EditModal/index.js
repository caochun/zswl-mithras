import { observer } from '@zswl/admin'
import { App, Form, Modal, Select } from '@zswl/components'
import { Input } from 'antd'
import { useEffect, useState } from 'react'
import { debounce as _debounce } from 'lodash'
import { ClientSelect } from '@/components/Select'

const { Item } = Form
function EditModal({ modalStore }) {
  const options = App.getData().optionsType
  const [form] = Form.useForm()

  useEffect(() => {
    if (!modalStore.visible) {
      form.resetFields()
    }
  }, [modalStore.visible])

  return (
    <Modal title={'创建立项'} store={modalStore} okText={'确定'} destroyOnClose>
      <Form
        form={form}
        labelCol={{ span: 6 }}
        preserve={true}
        initialValues={{
          bizType: 'ZL',
        }}
      >
        <Item
          label={'业务类型'}
          name={'bizType'}
          rules={[{ required: true, message: '请选择业务类型！' }]}
        >
          <Select options={options.projEstablishBizType} />
        </Item>
        <Item
          label="客户名称"
          name="clientId"
          key="clientId"
          rules={[{ required: true, message: '请选择客户名称！' }]}
        >
          <ClientSelect
            canJump={false}
            functionCode="clientlist-1"
            params={{
              clientType: 'CORPORATION',
              scene: 'main',
            }}
          ></ClientSelect>
        </Item>

        <Item
          label={'项目名称'}
          name={'projName'}
          rules={[{ required: true, message: '请输入项目名称！' }]}
        >
          <Input placeholder={'请输入'} />
        </Item>
        {/* 现在默认只有简易审批，SIMPLE */}
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
