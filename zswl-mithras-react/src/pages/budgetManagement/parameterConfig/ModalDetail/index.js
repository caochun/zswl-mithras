import { Modal } from '@zswl/components'
import { observer } from '@zswl/admin'
import ModalEditTable from './ModalEditTable'

const Index = ({ store }) => {
  const { typeInfo } = store
  console.log(store,'store')
  if (!typeInfo) return null
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
      <ModalEditTable
        dataSource={typeInfo.list}
        typeInfo={typeInfo}
        baseStore={store}
      ></ModalEditTable>
    </Modal>
  )
}

export default observer(Index)
