import { Modal } from '@zswl/components'
import { BudgetIncomeShareTableDetail as IncomeShareTableDetail } from '@/components/Budget/IncomeShareTableEntries'
import { observer } from '@zswl/admin'

const Index = ({ store }) => {
  const { receiptId, yearAndMonth, receiptCode } = store.getInitialValues() ?? {}

  return (
    <Modal store={store} title="借据详情" width={800} footer={null}>
      <IncomeShareTableDetail params={{ id: receiptId, yearAndMonth }} query={{ receiptCode }} />
    </Modal>
  )
}

export default observer(Index)
