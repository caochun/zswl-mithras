import { observer, getQuery } from '@zswl/admin'
import { Page, Button } from '@zswl/components'
import { useMemo } from 'react'
import Store from './store'
import BaseInfo from './BaseInfo'
import BaseInfoTable from './BaseInfoTable'
import CollectionRecord from './CollectionRecord'
import { CollectionModal } from '../../CollectionModal/OverdueCollectionModal'

const Detail = ({ params: { id }, query: { bizType, newProject, canEditFlags = 'true' } }) => {
  const store = useMemo(() => {
    return new Store({ id })
  }, [id])
  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'
  const baseInfoDetail = store.page.getData()
  const { clientId } = baseInfoDetail
  // 按钮权限控制，审批流后端控制 + 是否主办人
  const canEditFlagsFormAuth = canEditFlags === 'true'

  return (
    <Page
      store={store}
      params={{ id, newProject: newProject === 'true', isFormApproval }}
      header={null}
      extra={[
        <Button type="primary" onClick={() => store.collectionModal.open({ type: 'NON_LIVE' })}>
          非现场催收
        </Button>,
        <Button type="primary" onClick={() => store.collectionModal.open({ type: 'LIVE' })}>
          现场催收
        </Button>,
        <Button type="primary" onClick={() => store.collectionModal.open({ type: 'SEND_LETTER' })}>
          发函催收
        </Button>,
      ]}
    >
      <BaseInfo dataSource={baseInfoDetail} canEdit={canEditFlagsFormAuth} store={store} />
      <BaseInfoTable canEdit={canEditFlagsFormAuth} mainId={id} store={store} />
      <CollectionRecord canEdit={canEditFlagsFormAuth} mainId={id} store={store} />
      <CollectionModal
        modal={store.collectionModal}
        ocId={id}
        clientId={clientId}
        store={store}
        canEdit={canEditFlags}
      />
    </Page>
  )
}

export default observer(Detail)
