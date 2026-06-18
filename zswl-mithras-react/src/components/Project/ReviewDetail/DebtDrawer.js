import { CustomerDebtRat as DebtRat } from '@/components/Customer/DebtRatingEntries'
import { observer } from '@zswl/admin'
import { Drawer } from '@zswl/components'

const Index = ({ store, params, detail }) => {
  return (
    <Drawer store={store} width={1000} extra={null}>
      <DebtRat tableParams={params} detail={detail} afterClose={store.close} />
    </Drawer>
  )
}

export default observer(Index)
