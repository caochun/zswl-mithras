import EditDescription from '@/components/Table/EditDescription'
import { observer } from '@zswl/admin'
import { Modal } from '@zswl/components'
import { useMemo } from 'react'
import { childColumns } from './RelateProject'

const BatchEditModal = ({ store }) => {
  const { batchEditModal } = store
  const initialValues = batchEditModal.getInitialValues() || {}
  const { hasProjCodeContractCode } = initialValues

  const columns = useMemo(() => {
    const allColumns = childColumns(hasProjCodeContractCode)
    const excludeDataIndex = ['projectName', 'projCode', 'contractCode', 'bizType', 'projStatus']
    return allColumns.filter((col) => !excludeDataIndex.includes(col.dataIndex))
  }, [hasProjCodeContractCode])

  return (
    <Modal
      footer={null}
      width={1000}
      destroyOnClose
      title={'编辑'}
      store={batchEditModal}
      onCancel={() => {
        batchEditModal.close()
      }}
    >
      <EditDescription
        saveData={store.handleBatchSave}
        title=" "
        detail={{}}
        canEdit={true}
        columns={columns}
      />
    </Modal>
  )
}

export default observer(BatchEditModal)
