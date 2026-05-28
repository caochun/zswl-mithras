import { observer, getQuery, history } from '@zswl/admin'
import { Page } from '@zswl/components'
import { useEffect, useMemo } from 'react'
import Store from './store'
import DetailLayout from '@/components/DetailLayout'
import BaseInfo from './BaseInfo'
import DataList from './DataList'
import TrackingTask from './TrackingTask'
import { Button } from 'antd'

const Detail = ({ params: { id }, query: { bizType, newProject, canEditFlags = 'false' } }) => {
  const store = useMemo(() => {
    return new Store({ id })
  }, [id])
  const detail = store.page.getData()
  const { clientId, projName } = detail?.trackEventContractInfo ?? {}
  const goProcess = () => {
    const search = JSON.stringify({
      clientId,
      projName,
    })
    history.push(`/process/query?search=${search}`)
  }
  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'

  // 基本信息、付款资料、资料清单
  const anchorList = [
    { label: '基本信息', href: 'baseInfo' },
    { label: '跟踪任务', href: 'trackingTasks' },
    { label: '资料清单', href: 'dataList' },
  ]
  const canEdit = canEditFlags === 'true'
  return (
    <Page
      store={store}
      params={{ id, newProject: newProject === 'true', isFormApproval }}
      header={null}
    >
      <DetailLayout
        anchorList={anchorList}
        title="跟踪事项"
        extra={[
          <Button onClick={goProcess} type="link">
            查询历史流程
          </Button>,
        ]}
      >
        <BaseInfo dataSource={detail.trackEventContractInfo} canEdit={false} />
        <TrackingTask dataSource={detail} canEdit={false} />
        <DataList mainId={id} canEdit={canEdit} />
      </DetailLayout>
    </Page>
  )
}

export default observer(Detail)
