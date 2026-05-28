import { getQuery, observer } from '@zswl/admin'
import BaseInfo from './BaseInfo'
import { useEffect } from 'react'
import { DetailLayout } from '@/components'
import HistoryRat from './HistoryRat'
import ScoreReport from './ScoreReport'
import { Space } from 'antd'
import styles from './styles.less'
import { Descriptions } from '@zswl/components'
import { MatchOptionColumn } from '@/components/Format'

const RatResult = ({ quota = {} }) => {
  const { modelType } = quota ?? {}
  const quotaList = [
    {
      title: '评估主体限额',
      unit: '万元',
      dataIndex: 'evaluationSubjectQuota',
      modelType: ['NORMAL'],
    },
    {
      title: '客户限额',
      unit: '万元',
      dataIndex: 'clientQuota',
      modelType: ['PROJ', 'POLITICE_CREDIT'],
    },
    {
      title: '建议项目限额',
      unit: '万元',
      dataIndex: 'projQuota',
      modelType: ['NORMAL', 'PROJ', 'POLITICE_CREDIT'],
    },
    { title: '集团限额', unit: '万元', dataIndex: 'groupQuota', modelType: ['POLITICE_CREDIT'] },
    {
      title: '集团剩余可用金额',
      unit: '万元',
      dataIndex: 'groupSurplusQuota',
      modelType: ['POLITICE_CREDIT'],
    },

    // { title: '评估主体剩余可用限额', unit: '万元', dataIndex: 'projQuota', modelType: ['NORMAL'] },
  ]
  const quotaListMap = quotaList.filter((item) => item.modelType.includes(modelType))
  return (
    <div>
      <div style={{ fontWeight: 800, fontSize: 16, padding: '12px 0' }}>评级结果</div>
      <Space>
        {quotaListMap.map(({ title, dataIndex, unit }) => (
          <div className={styles.cardBox}>
            <div>{title}</div>
            <div className={styles.index}>
              {quota[dataIndex]}
              <span className={styles.unit}> {unit}</span>
            </div>
          </div>
        ))}
      </Space>
    </div>
  )
}
const Index = ({ store, baseInfoDetail }) => {
  const { customerDetail } = store.page.getData() ?? {}
  const isApproval = getQuery('typeId') == 'approval'
  const canApprovalStatus = ['UNDER_APPROVAL'].includes(baseInfoDetail?.processStatus)
  const isFormApproval = isApproval && canApprovalStatus
  const { reportData } = store
  useEffect(() => {
    store.getReport()
  }, [])

  const { quota = {}, historyInfo, scoreRSP, evaluateBaseList, creditMeasureListMap } = reportData
  // 基本信息、付款资料、资料清单
  const anchorList = [
    { label: '项目信息信息', href: 'baseInfo' },
    { label: '历史评级信息', href: 'history' },
    { label: '评估基准', href: 'quantitativeScore' },
    { label: '增信措施', href: 'qualitativeScore' },
    { label: '评估主体评级', href: 'ratLevel' },
    { label: '评级结果', href: 'ratResult' },
  ]
  return (
    <DetailLayout anchorList={anchorList} offsetTop={120}>
      <BaseInfo detail={customerDetail} />
      <HistoryRat detail={historyInfo} />
      <ScoreReport info={evaluateBaseList} isFormApproval={isFormApproval} title="评估基准" />
      <ScoreReport info={creditMeasureListMap} isFormApproval={isFormApproval} title="征信措施" />
      <div>
        <div style={{ fontWeight: 800, fontSize: 16, padding: '12px 0' }}>评估主体</div>
        <Descriptions
          dataSource={scoreRSP}
          items={[
            { title: '评估主体名称', dataIndex: 'evaluationSubjectName' },
            { title: '评估主体信用代码', dataIndex: 'evaluationSubjectUscCode' },
            { title: '评估主体等级', dataIndex: 'finalScore' },
          ]}
        />
      </div>
      <RatResult quota={quota} />
    </DetailLayout>
  )
}

export default observer(Index)
