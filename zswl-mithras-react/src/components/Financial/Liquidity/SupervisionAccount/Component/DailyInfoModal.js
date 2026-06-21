import { Modal, Flex } from '@zswl/components'
import { observer } from '@zswl/admin'
import SupervisePie from './SupervisePie'
import SuperviseTable from './SuperviseTable'

const FinancialLiquidityDailyInfoModal = ({ store }) => {
  const { date, data } = store.dailyChartsModal.getInitialValues() ?? {}
  const tableData = data?.list ?? []
  return (
    <Modal
      store={store.dailyChartsModal}
      title={<div className="z-sub-title">{date} 当日情况</div>}
      footer={null}
      width={1100}
    >
      <Flex
        style={{
          display: 'flex',
          justifyContent: 'space-between',
        }}
      >
        <div style={{ flex: 1, marginRight: 20 }}>
          <SupervisePie pieData={tableData}></SupervisePie>
        </div>
        <div style={{ width: 600 }}>
          <SuperviseTable pieData={tableData}></SuperviseTable>
        </div>
      </Flex>
    </Modal>
  )
}

export default observer(FinancialLiquidityDailyInfoModal)
