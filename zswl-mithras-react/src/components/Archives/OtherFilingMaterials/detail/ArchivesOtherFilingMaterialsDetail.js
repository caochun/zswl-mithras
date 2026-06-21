import { getQuery, observer } from '@zswl/admin'
import { Button, Page } from '@zswl/components'
import { useMemo } from 'react'
import Data from './Data'
import Store from './store'

const ArchivesOtherFilingMaterialsDetail = ({ params: { id }, query: { businessVersion, modelKey } }) => {
  const store = useMemo(() => {
    return new Store({ id })
  }, [id])
  const approveStatus = getQuery('approveStatus')
  const materialsDesc = getQuery('materialsDesc')
  const { OTHER_FILING: enumType = [] } = store.page.getData()

  // 取消操作
  const handleCancel = () => {
    store.cancel(id)
  }
  const handleSubmit = () => {
    store.submit(id)
  }
  const ActionBtn = approveStatus === 'UN_SUBMIT' && (
    <>
      <Button onClick={handleCancel}>取消操作</Button>
      <Button type="primary" onClick={handleSubmit}>
        提交审批
      </Button>
    </>
  )

  return (
    <Page store={store} header={null} extra={ActionBtn} params={{ id, modelKey, businessVersion }}>
      <Data
        id={id}
        canEdit={approveStatus === 'UN_SUBMIT'}
        enumType={enumType}
        materialsDesc={materialsDesc}
      />
    </Page>
  )
}

export default observer(ArchivesOtherFilingMaterialsDetail)
