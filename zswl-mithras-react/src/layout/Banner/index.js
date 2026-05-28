import { observer } from '@zswl/admin'
import { useEffect, useMemo } from 'react'
import BannerModal from './BannerModal'
import CreateBanner from './CreateBanner'

const Index = ({ store }) => {
  useEffect(() => {
    store.getAllBannerList()
  }, [store])

  return (
    <div>
      <BannerModal store={store}> </BannerModal>
      <CreateBanner store={store}></CreateBanner>
    </div>
  )
}
export default observer(Index)
