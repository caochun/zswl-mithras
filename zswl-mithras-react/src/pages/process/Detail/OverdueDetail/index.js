import { useEffect, useMemo, useState } from 'react'
import { observer } from '@zswl/admin'
import { CollectionModal } from '@/components/Overdue/CollectionModal'
import { ModalStore } from '@zswl/components'
import collectionManagementApi from '@/api/overdue/collectionManagementApi'

const Index = (props) => {
  const { canEditFlag, subModule, id, businessVersion, modelKey, curTaskActivityIds, taskStatus } =
    props

  const [clientId, setClientId] = useState(null)
  const [ocId, setOcId] = useState(null)

  const modal = useMemo(
    () =>
      new ModalStore({
        onOpen: async () => {
          const res = await collectionManagementApi.postActionDetail({
            id,
            version: businessVersion,
          })
          const detail = await collectionManagementApi.postOverduecollectionDetail({
            id: res.ocId,
          })
          setClientId(detail?.clientId)
          setOcId(res.ocId)
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
      <CollectionModal
        modal={modal}
        clientId={clientId}
        canEdit={canEditFlag}
        ocId={ocId}
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
export default observer(Index)
