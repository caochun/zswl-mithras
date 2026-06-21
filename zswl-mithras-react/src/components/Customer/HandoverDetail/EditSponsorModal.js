import { observer } from '@zswl/admin'
import { Modal } from '@zswl/components'
import { EditDescription } from '@/components/Table'
import { childColumns } from './RelateProject'
import { useMemo } from 'react'

const CustomerHandoverEditSponsorModal = ({ store }) => {
  const { editStatus, editSponsorModal } = store
  const initialValues = editSponsorModal.getInitialValues() || {}
  const { projCode, contractCode } = initialValues
  const hasProjCodeContractCode = projCode

  const columns = useMemo(() => {
    return childColumns(hasProjCodeContractCode)
  }, [editStatus, hasProjCodeContractCode])

  return (
    <Modal
      footer={null}
      width={1000}
      destroyOnClose
      title={editStatus ? '编辑' : '查看'}
      store={store.editSponsorModal}
      onCancel={() => {
        store.editSponsorModal.close()
      }}
    >
      <EditDescription
        saveData={async (values) => {
          store.handelSaveSponsorInfo({
            ...initialValues,
            ...values,
          })
        }}
        title=" "
        detail={initialValues}
        canEdit={editStatus}
        columns={columns}
      />
    </Modal>
  )
}

export default observer(CustomerHandoverEditSponsorModal)
