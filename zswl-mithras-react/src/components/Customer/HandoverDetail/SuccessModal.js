import { history, observer } from '@zswl/admin'
import { Modal } from '@zswl/components'
import { useMemo } from 'react'

const SuccessModal = ({ store }) => {
  const { rows: list } = store.table.getSelected()
  const projectCount = useMemo(() => {
    let projCodeList = []
    list.forEach((item) =>
      item.clientProjRSPList?.forEach((i) => {
        if (i.projCode) {
          projCodeList.push(i.projCode)
        }
      })
    )
    return Array.from(new Set(projCodeList)).length
  }, [list])
  return (
    <Modal
      okText={'前往审批流程查看'}
      cancelText="关闭并返回客户管理"
      title="客户移交审批已提交"
      closable={false}
      store={store.successModal}
      onCancel={() => {
        store.successModal.close()
        history.push('/customer/maintain')
      }}
      onOk={() => {
        store.successModal.close()
        history.push('/process/application')
      }}
    >
      <h2>{`本次申请移交${list.length}位客户关联${projectCount}个项目,已经提交成功`}</h2>
      <h1 style={{ color: 'red' }}>
        注意：该客户移交流程涉及的客户和项目，不可进行任何系统操作，需等待移交流程结束后方可进行！
      </h1>
    </Modal>
  )
}

export default observer(SuccessModal)
