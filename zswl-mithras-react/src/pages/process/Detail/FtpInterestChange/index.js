import { useEffect, useMemo } from 'react'
import { observer } from '@zswl/admin'
import PriceChangeModal from '@/pages/budget/pricing/ftpInterest/PriceChangeModal'
import { ModalStore } from '@zswl/components'
import ftpInterestChangeApi from '@/api/budget/pricing/ftpInterestChangeApi'

const Index = (props) => {
  const { canEditFlag, subModule, id, businessVersion, tab } = props
  const priceChangeModal = useMemo(() => {
    return new ModalStore({
      onOpen: async () => {
        const res = await ftpInterestChangeApi.postApplyDetail({ id })
        return res
      },
      onFinish: () => {},
    })
  }, [])

  useEffect(() => {
    setTimeout(() => {
      priceChangeModal.open()
      console.log('priceChangeModal: ', priceChangeModal)
    }, 40)
  }, [priceChangeModal])

  const renderContractType = useMemo(() => {
    return (
      <PriceChangeModal
        modalProps={{
          zIndex: 1,
          getContainer: false,
          closable: false,
          mask: false,
          centered: true,
          wrapClassName: 'z-approval-modal',
          maskClosable: false,
        }}
        modal={priceChangeModal}
        canEdit={canEditFlag}
      />
    )
  }, [subModule, canEditFlag, businessVersion])

  return renderContractType
}
export default observer(Index)
