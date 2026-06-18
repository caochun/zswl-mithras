import { observer } from '@zswl/admin'
import { Button } from 'antd'
import { Modal, Table } from '@zswl/components'
import ALL_COLUMNS from './Column'
import { useMemo } from 'react'

function DupModal({ modalStore, refresh }) {
  const columns = useMemo(() => ALL_COLUMNS(modalStore.createModal.close), [])
  const refreshTable = () => {
    modalStore.$table.search()
    modalStore.createModal.close()
  }
  return (
    <Modal
      title='查重清单'
      store={modalStore.createModal}
      destroyOnClose
      width='60%'
      footer={<Button onClick={refreshTable} type='primary'>关闭</Button>}
    >
        <Table columns={columns} dataSource={modalStore.createModal.initialValues}/>
    </Modal>
  )
}

export default observer(DupModal)
