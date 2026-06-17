import DetailLayout from '@/components/DetailLayout'
import { Page, Button, Form } from '@zswl/components'
import { observer, getQuery } from '@zswl/admin'
import BaseInfo from './BaseInfo'
import DataFileList from './DataFileList'
import Store from './Store'
import { useMemo } from 'react'
import CheckBusiness from '@/components/Customer/CheckBusiness'

const Index = ({ params: { id }, query: { processInstanceId, canEditFlag = true, batchNo } }) => {
  const canEdit = canEditFlag
  const store = useMemo(() => {
    return new Store()
  }, [])
  const anchorList = [{ label: '基本信息' }, { label: '补充资料' }].filter(Boolean)
  const isFormApproval = getQuery('typeId') == 'approval'

  const extra = [
    <Button onClick={store.handleCancel}>取消操作</Button>,
    <Button type="primary" onClick={store.handleSubmit}>
      提交申请
    </Button>,
  ]
  const extraButton = !isFormApproval && canEdit && extra

  return (
    <Page store={store.page} params={{ id, processInstanceId, batchNo }}>
      <DetailLayout anchorList={anchorList} title={'客户申办权限申请'} extra={extraButton}>
        <BaseInfo store={store} canEdit={canEdit} isFormApproval={isFormApproval}></BaseInfo>
        <DataFileList id={id} canEdit={canEdit} store={store}></DataFileList>
      </DetailLayout>
    </Page>
  )
}

export default observer(Index)
