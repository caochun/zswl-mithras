import DetailLayout from '@/components/DetailLayout'
import { getQuery, observer } from '@zswl/admin'
import { Button, Page } from '@zswl/components'
import { useMemo, useRef } from 'react'
import BaseInfo from './BaseInfo'
import BatchEditModal from './BatchEditModal'
import DataFileList from './DataFileList'
import EditSponsorModal from './EditSponsorModal'
import RelateProject from './RelateProject'
import SuccessModal from './SuccessModal'
import Store from './store'

const Handover = ({ params: { id }, canEdit = true, query: { initData, processInstanceId } }) => {
  const newInitData = JSON.parse(initData ?? '{}')
  const baseInfoRef = useRef()
  const isFormApproval = getQuery('typeId') == 'approval'
  const store = useMemo(() => new Store(), [])

  const extra = [
    <Button onClick={store.handleCancel}>取消操作</Button>,
    <Button
      type="primary"
      onClick={async () => {
        const form = baseInfoRef.current.getEditDescForm()
        const values = await form.validateFields()
        store.handleSubmit(values)
      }}
    >
      提交申请
    </Button>,
  ]

  return (
    <Page params={{ id, isFormApproval, processInstanceId, ...newInitData }} store={store.page}>
      <DetailLayout extra={!isFormApproval && extra} title="客户移交">
        <BaseInfo store={store} ref={baseInfoRef} canEdit={canEdit}></BaseInfo>
        <div style={{ height: 10 }}></div>
        <RelateProject store={store} canEdit={canEdit}></RelateProject>
        <DataFileList store={store} canEdit={true}></DataFileList>
      </DetailLayout>
      <SuccessModal store={store} />
      <EditSponsorModal store={store} />
      <BatchEditModal store={store} />
    </Page>
  )
}
export default observer(Handover)
