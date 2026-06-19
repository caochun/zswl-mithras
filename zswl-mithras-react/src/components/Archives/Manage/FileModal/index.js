import { observer } from '@zswl/admin'
import { App, Form, Modal, Select } from '@zswl/components'
import { Input, AutoComplete } from 'antd'

const { Item } = Form
function FileModal({ store }) {
  const { dataId } = store

  return (
    <Modal
      title={'发起归档'}
      store={store.fileModal}
      okText={'确认'}
      destroyOnClose
      onOk={() => store.submitFile(dataId)}
    >
      <Form store={store.form} labelCol={{ span: 6 }}>
        <Item
          label={'项目名称'}
          name='projName'
          rules={[{ required: true, message: '请选择项目名称！' }]}
        >
          <Select
            allowClear
            fieldNames={{ label: 'projName', value: 'id' }}
            // debounceSearch
            options={store.getSelectList}
            onChange={store.onChangeSelect}
          />
        </Item>
        <Item
          label={'客户名称'}
          name='clientName'
          rules={[{ required: true, message: '客户名称' }]}
        >
          <Input disabled placeholder='客户名称' />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(FileModal)