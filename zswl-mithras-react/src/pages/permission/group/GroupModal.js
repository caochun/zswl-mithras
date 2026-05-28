import OrgsSelect from '@/pages/project/Components/OrgsSelect'
import FounderSelect from '@/pages/project/Components/FounderSelect'
import { Form, Modal, Select } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import { Checkbox, Input } from 'antd'
import { useEffect, useMemo } from 'react'
const HandoverModal = () => {
  const { menuId, functionList, modalTitle } = store
  // useEffect(async () => {
  //   store.getFunctionList(menuId)
  // }, [menuId])
  return (
    <Modal title={modalTitle} store={store.groupModal} destroyOnClose>
      <Form labelCol={{ span: 8 }} preserve={false} layout="horizontal">
        <Form.Item label={'分组名称'} name={'name'} rules={[{ required: true }]}>
          <Input></Input>
        </Form.Item>
        <Form.Item label={'分组Code'} name={'code'} rules={[{ required: true }]}>
          <Input></Input>
        </Form.Item>
        <Form.Item label={'分组描述'} name={'describe'} rules={[{ required: true }]}>
          <Input></Input>
        </Form.Item>
        <Form.Item label={'功能'} name={'functionIds'} rules={[{ required: true, type: 'array' }]}>
          <Checkbox.Group options={functionList} />
        </Form.Item>
      </Form>
    </Modal>
  )
}

export default observer(HandoverModal)
