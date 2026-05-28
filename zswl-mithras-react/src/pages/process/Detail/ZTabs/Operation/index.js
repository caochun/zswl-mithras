import React, { useEffect, useMemo, useState } from 'react'
import { Tag, Card } from 'antd'
import { Table } from '@zswl/components'
import styles from './index.less'
import { observer, ErrorBoundary } from '@zswl/admin'
import Operator from '@/pages/process/Detail/ZTabs/Operation/Components/Operator'
import CompleteOperation from './Components/CompleteOperation'
import ApprovalHistory from '@/pages/process/components/ApprovalHistory'
import FlowChart from '@/pages/process/Detail/ZTabs/FlowChart'
import DetailLayout from '@/components/DetailLayout'
import {
  useFlowData,
  votingResultsList,
  fileKeyEnum,
  complementListExtra,
} from '@/pages/process/Detail/Context'
import AbstractDesc from './Components/AbstractDesc'
import { saveServer } from '@/utils'

const CustomTitle = ({ title }) => {
  return (
    <div className={styles.customWrap}>
      <span className={styles.customTitle}>{title}</span>
    </div>
  )
}

const findCommonElement = (array1, array2) => {
  return array1.some((item) => array2.includes(item))
}
const cardStyle = { marginTop: 20 }

const voteRSPColumn = [
  { title: '提交时间', dataIndex: 'voteTime' },
  { title: '评委名称', dataIndex: 'handlerName' },
  { title: '审核状态', dataIndex: 'typeName' },
  { title: '审核意见', dataIndex: 'message' },
]

const Detail = () => {
  const { detailData: detail, isNewLayout, noOperate, pathname, isEditing } = useFlowData()
  const { dynamicFormData, dynamicFormKeyList, processInstanceId } = detail
  const hasAbstract = ['RATING_AMOUNT', 'RATING_CLIENT'].includes(detail.mainModule)
  const [bcxxFlag, setBcxxFlag] = useState(false)
  const [lczxFlag, setLczsFlag] = useState(false)
  const [voteRSPList, setVoteRSPList] = useState([])

  const anchorList = [
    { label: '补充信息', isHide: !bcxxFlag },
    { label: '流程展示', isHide: !lczxFlag },
    { label: '摘要信息', isHide: !hasAbstract },
    { label: '审批操作', isHide: noOperate },
    { label: '审批历史' },
    { label: '审批流程图' },
  ]

  const tableStore = Table.useStore(
    {
      pagination: false,
      request: async () => {
        return voteRSPList ?? []
      },
    },
    [voteRSPList]
  )

  const complementList = useMemo(() => {
    const keys = Object.keys(fileKeyEnum)
    return [...keys, ...complementListExtra]
  }, [fileKeyEnum])

  useEffect(() => {
    setBcxxFlag(findCommonElement(complementList, dynamicFormKeyList) || detail.collaborateFlag)
    setLczsFlag(findCommonElement(votingResultsList, dynamicFormKeyList))
    setVoteRSPList(
      dynamicFormData?.adjust_showJudgesVotingResults?.voteRSPList ||
      dynamicFormData?.projReview_showJudgesVotingResults?.voteRSPList ||
      dynamicFormData?.projReview_showDirectorsVotingResults?.voteRSPList ||
      dynamicFormData?.projReview_showMeetingVotingResults?.voteRSPList ||
      dynamicFormData?.ftp_showVotingResults?.voteRSPList ||
      dynamicFormData?.projReview_pricingShowVoteResult?.voteRSPList
    )
  }, [dynamicFormKeyList, dynamicFormData, complementList])

  useEffect(() => {
    tableStore?.search()
  }, [voteRSPList])

  if (!isNewLayout) {
    return (
      <ErrorBoundary fallback={<Tag>渲染出错了</Tag>}>
        <DetailLayout
          anchorList={anchorList}
          title=""
          moduleName={pathname?.split('/')?.join('')}
          anchorSwitch
        >
          <Card title="补充信息">
            <CompleteOperation />
          </Card>
          <Card title="流程展示" style={cardStyle}>
            <Table
              columnsFilter={'ZTabs_Operation_1'}
              onFilter={(key, val) => saveServer('ZTabs_Operation_1', val)}
              scroll={{ x: 400 }}
              resizable
              autoRequest={false}
              store={tableStore}
              columns={voteRSPColumn}
            />
          </Card>
          <Card title="摘要信息" style={cardStyle}>
            <AbstractDesc detail={detail} />
          </Card>
          <Card title="审批操作" style={cardStyle}>
            <Operator/>
          </Card>
          <Card title="审批历史">
            <ApprovalHistory processInstanceId={processInstanceId} />
          </Card>
          <Card title="审批流程图">
            <FlowChart />
          </Card>
        </DetailLayout>
      </ErrorBoundary>
    )
  }

  return (
    <ErrorBoundary fallback={<Tag>渲染出错了</Tag>}>
      {bcxxFlag && (
        <div>
          <CustomTitle title={'补充信息'}></CustomTitle>
          <CompleteOperation />
        </div>
      )}
      {lczxFlag && (
        <div>
          <CustomTitle title={'流程展示'}></CustomTitle>
          <Table
            columnsFilter={'ZTabs_Operation_2'}
            onFilter={(key, val) => saveServer('ZTabs_Operation_2', val)}
            scroll={{ x: 400 }}
            resizable
            autoRequest={false}
            store={tableStore}
            columns={voteRSPColumn}
          />
        </div>
      )}
      <CustomTitle title={'审批操作'}></CustomTitle>
      <Operator />
    </ErrorBoundary>
  )
}

export default observer(Detail)
