import { observer, getQuery } from '@zswl/admin'
import { Page } from '@zswl/components'
import { useEffect, useMemo } from 'react'
import Store from './store'
import DetailLayout from '@/components/DetailLayout'
import Report from './Report'
import DataList from './DataList'
import BaseInfo from './BaseInfo'

const Detail = ({ params: { id }, query: { bizType, newProject, canEditFlags = 'true' } }) => {
  const store = useMemo(() => {
    return new Store({ id })
  }, [id])
  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'
  const baseInfoDetail = store.page.getData()
  // 按钮权限控制，审批流后端控制 + 是否主办人
  const canEditFlagsFormAuth = canEditFlags === 'true'

  // 基本信息、付款资料、资料清单
  const anchorList = [
    { label: '基本信息', href: 'baseInfo' },
    { label: '付款资料', href: 'paymentData' },
    { label: '资料清单', href: 'dataList' },
  ]
  return (
    <Page
      store={store}
      params={{ id, newProject: newProject === 'true', isFormApproval }}
      header={null}
    >
      <DetailLayout anchorList={anchorList} title="收付款核销">
        <BaseInfo dataSource={baseInfoDetail} canEdit={canEditFlagsFormAuth} />
        <Report canEdit={canEditFlagsFormAuth} mainId={id} />
        <DataList canEdit={canEditFlagsFormAuth} mainId={id} />
      </DetailLayout>
    </Page>
  )
}

export default observer(Detail)
