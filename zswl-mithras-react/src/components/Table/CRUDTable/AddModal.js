import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'

//新增联系人弹窗
function CRUDTableAddModal({ store, columns }) {
  return (
    <Modal title={'新增'} store={store} okText={'确定'} width={480} destroyOnClose>
      <Form
        layout={'horizontal'}
        labelCol={{ span: 6 }}
        preserve={false}
        /* eslint-disable react/no-children-prop */
        column={1}
        items={columns}
        // config={{ items: columns, chunk: 1 }}
      />
    </Modal>
  )
}

export default observer(CRUDTableAddModal)
