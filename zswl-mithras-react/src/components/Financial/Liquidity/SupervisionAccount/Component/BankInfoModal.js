import { Modal, Flex, ModalStore } from '@zswl/components'
import { observer } from '@zswl/admin'
import SupervisePie from './SupervisePie'
import SuperviseTable from './SuperviseTable'
import LineCharts from './LineCharts'
import { useMemo } from 'react'
import fundTransferApi from '@/api/financial/fundTransfer'
import { Space } from 'antd'
import { amountFormat } from '@/utils'

const FinancialLiquidityBankInfoModal = ({ store }) => {
  const { data = {}, record = {} } = store.bankInfoModal.getInitialValues() ?? {}
  const { accountBank, accountNumber, accountId } = record ?? {}

  const list = data.list?.map((item) => {
    const currentDate = item.pendingBalanceAmount / 10000

    return {
      ...item,
      pendingBalanceAmount: currentDate < 0.01 ? 0 : currentDate,
    }
  })

  const modal = useMemo(
    () =>
      new ModalStore({
        onOpen: async (params) => {
          const result = await fundTransferApi.postAccountDaily({
            ...store.getCommonQueryParams(),
            accountId,
            currentDate: params.name,
          })
          return {
            result,
            record,
            params,
          }
        },
      }),
    [accountId]
  )

  const { result = {}, params = {} } = modal.getInitialValues() ?? {}
  const tableData = result?.list ?? []

  return (
    <div>
      <Modal
        store={store.bankInfoModal}
        title={
          <div className="z-sub-title">
            {accountBank} {accountNumber}
          </div>
        }
        footer={null}
        width={1100}
      >
        <LineCharts
          dataSource={list}
          store={store}
          indicators={[{ key: 'pendingBalanceAmount', name: '' }]}
          handleClick={(params) => modal.open(params)}
        ></LineCharts>
      </Modal>

      <Modal
        store={modal}
        footer={null}
        width={1100}
        title={
          <Space>
            <span>时间：{params.name}</span>
            <span>总额：{amountFormat(result.sum / 10000)} (元)</span>
          </Space>
        }
      >
        <Flex style={{ display: 'flex', justifyContent: 'space-between' }}>
          <div style={{ flex: 1, marginRight: 20, textAlign: 'center' }}>
            <SupervisePie pieData={tableData}></SupervisePie>
          </div>
          <div style={{ width: 600 }}>
            <SuperviseTable pieData={tableData}></SuperviseTable>
          </div>
        </Flex>
      </Modal>
    </div>
  )
}

export default observer(FinancialLiquidityBankInfoModal)
