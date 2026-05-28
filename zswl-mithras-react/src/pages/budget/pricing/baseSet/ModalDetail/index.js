import { Input, Modal } from '@zswl/components'
import { observer } from '@zswl/admin'
import ModalEditTable from './ModalEditTable'
import ValuationFluctuations from './ValuationFluctuations'
import { FormTable } from '@/components'

const Index = ({ store }) => {
  const { typeInfo } = store
  if (!typeInfo) return null
  const isJiJia = typeInfo.category === 'VALUATION_STANDARDS_FOR_FINANCIAL_MARKET_VOLATILITY'

  return (
    <Modal
      propsBy={(data) => {
        const { isEdit, title } = data
        return {
          title: isEdit ? `编辑-${title}` : `查看-${title}`,
          footer: null,
        }
      }}
      store={store.$editModal}
      destroyOnClose
      width={700}
    >
      {isJiJia ? (
        <ValuationFluctuations dataSource={typeInfo.list} typeInfo={typeInfo} baseStore={store} />
      ) : (
        <ModalEditTable
          dataSource={typeInfo.list}
          typeInfo={typeInfo}
          baseStore={store}
        ></ModalEditTable>
      )}
    </Modal>
  )
}

export default observer(Index)
