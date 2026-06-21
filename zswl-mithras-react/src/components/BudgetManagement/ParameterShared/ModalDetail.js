import { observer } from '@zswl/admin'
import { Modal } from '@zswl/components'

const ModalDetail = ({ store, ModalEditTable }) => {
  const { typeInfo } = store
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

export default observer(ModalDetail)
