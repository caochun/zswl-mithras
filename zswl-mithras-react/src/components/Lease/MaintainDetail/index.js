import { observer, getQuery, history } from '@zswl/admin'
import { Page, Button } from '@zswl/components'
import { useMemo, useRef } from 'react'
import DetailLayout from '@/components/DetailLayout'
import Store from './store'
import BaseInfo from './BaseInfo'
import LeaseCheck from './LeaseCheck'
import LeaseInfo from './LeaseInfo'
import LeaseList from './LeaseList'
import LeaseText from './LeaseText'
import DataFileList from './DataFileList'
import EvaluationAgency from '@/components/EvaluationAgency'
import { jumpZhongDeng } from '@/utils'
import ZhongDengButton from '@/components/ZhongDengButton'
import { ProjectReviewMeetingModal as MeetingModal } from '@/components/Project/ReviewSupportEntries'

const Index = ({
  params: { id },
  query: { businessVersion, canEdit, taskActivityId, taskStatus, startUserId, modelKey, type },
}) => {
  console.log(type === 'manage', type,'====type')
  const fileListRef = useRef()
  const backRef = useRef()
  const store = useMemo(() => {
    return new Store({ id })
  }, [id])

  const isFormApproval = getQuery('typeId') == 'approval'
  const commonParams = {
    id,
    // 审批流+我收到的待审批
    canEdit: isFormApproval && canEdit,
    baseStore: store,
    isFormApproval,
    businessVersion,
    taskActivityId,
    startUserId,
    taskStatus,
    modelKey,
  }

  const baseInfoData = store.page.getData()

  const anchorList = [
    { label: '项目信息' },
    { label: '租赁物查重' },
    { label: '租赁物信息' },
    { label: '租赁物清单' },
    { label: '租赁物文本' },
    { label: '评估机构' },
    { label: '资料清单' },
  ].filter(Boolean)

  const goProcess = () => {
    const search = JSON.stringify({
      projName: baseInfoData.projectName,
    })
    if (isFormApproval) {
      window.open(`/process/query?search=${search}`)
    } else {
      history.push(`/process/query?search=${search}`)
    }
  }
  console.log(baseInfoData,'=====')
  return (
    <Page store={store} params={{ id, fileListRef }} header={null}>
      <DetailLayout
        moduleName={'lease'}
        anchorList={anchorList}
        title="项目租赁物详情"
        extra={[
          <MeetingModal id={baseInfoData?.projReviewId} />,
          <ZhongDengButton
            params={{
              userNames: store.getLessees(),
            }}
          />,

          <Button
            onClick={() => {
              window.open('https://www.qcc.com/')
            }}
            type="link"
          >
            工商信息查询
          </Button>,
          <Button onClick={goProcess} type="link">
            查询历史流程
          </Button>,
        ]}
      >
        <BaseInfo detail={baseInfoData} />
        <LeaseCheck {...commonParams} />
        <LeaseInfo {...commonParams} />
        <LeaseList {...commonParams} projCode={baseInfoData.projectCode} disable={type && type === 'manage'} />
        <LeaseText {...commonParams} fileListRef={fileListRef} />
        <EvaluationAgency {...commonParams} />
        <DataFileList {...commonParams} />
      </DetailLayout>
    </Page>
  )
}

export default observer(Index)
