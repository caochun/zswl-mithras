import { useEffect, useMemo, useState } from 'react'
import { observer } from '@zswl/admin'
import { ModalStore } from '@zswl/components'
import { OverdueLitigationDocAddModal as AddModal } from '@/components/Overdue/LitigationDocEntries'
import sealForDocumentsApi from '@/api/process/detail/overdueSealDocumentApi'

const ProcessDetailLitigationDoc = (props) => {
  const { canEditFlag, subModule, id, businessVersion, modelKey, curTaskActivityIds, taskStatus } =
    props

  const modal = useMemo(
    () =>
      new ModalStore({
        onOpen: async () => {
          const res = await sealForDocumentsApi.postPrintingDetail({ id, version: businessVersion })
          return res
        },
      }),
    []
  )
  useEffect(() => {
    setTimeout(() => {
      modal.open()
    }, 40)
  }, [modal])
  return (
    <div>
      <AddModal
        modal={modal}
        canEdit={canEditFlag}
        modalProps={{
          zIndex: 1,
          getContainer: false,
          closable: false,
          mask: false,
          centered: true,
          wrapClassName: 'z-approval-modal',
          maskClosable: false,
        }}
        params={{
          id,
        }}
        query={{
          curTaskActivityIds,
          modelKey,
          businessVersion,
          taskStatus,
        }}
      />
    </div>
  )
}
export default observer(ProcessDetailLitigationDoc)
