import { getQuery, observer } from '@zswl/admin'
import BaseInfo from './BaseInfo'
import { useEffect, useMemo } from 'react'
import { DetailLayout, NoEnumFileTable } from '@/components'
import HistoryRat from './HistoryRat'
import QuantitativeReport from './QuantitativeReport'
import QualitativeReport from './QualitativeReport'
import AdjustmentReport from './AdjustmentReport'
import { Space } from 'antd'
import styles from './styles.less'
import { getUserInfo } from '@/utils'

const RatResult = observer(({ ratingScoreRSP }) => {
  return (
    <div>
      <div style={{ fontWeight: 800, fontSize: 16, padding: '12px 0' }}>评级结果</div>
      <Space>
        <div className={styles.cardBox}>
          <div>系统评级结果</div>
          <div className={styles.index}>{ratingScoreRSP?.score ?? '-'}</div>
        </div>
        <div className={styles.cardBox}>
          <div>业务调整结果</div>
          <div className={styles.index}>{ratingScoreRSP?.adjustScore ?? '-'}</div>
        </div>
        <div className={styles.cardBox}>
          <div>审查结果</div>
          <div className={styles.index}>{ratingScoreRSP?.finalScore ?? '-'}</div>
        </div>
      </Space>
    </div>
  )
})
const Index = ({ store, canApproval, isZX, auth, model }) => {
  const { customerDetail, baseInfoDetail } = store.page.getData() ?? {}
  const { id: mainId } = store.page.getParams()
  const { reportData, renderCount } = store

  useEffect(() => {
    store.getReport()
  }, [])

  const {
    ratingScoreRSP = {},
    historyInfo,
    custList = [],
    subjectVesselScoreList = [],
    quantitativeList = [],
    qualitativeList = [],
    adjustEventList = [],
  } = reportData
  // 基本信息、付款资料、资料清单
  const hymx = model && model === 'client_hymx'
  const anchorList = [
    { label: '评级结果', href: 'ratResult' },
    { label: '补充说明资料', href: 'fileList' },
    { label: '客户信息', href: 'baseInfo' },
    { label: '历史评级信息', href: 'history' },
    { label: hymx ? '客户综合得分' : isZX ? '区域得分' : '定量得分', href: 'quantitativeScore' },
    { label: hymx ? '标的物得分' : isZX ? '主体得分' : '定性得分', href: 'qualitativeScore' },
    { label: '调整事项', href: 'adjustEvent' },
  ]
  const quantitativeData = useMemo(() => {
    if(hymx){
      return custList
    }
    if (!isZX) return quantitativeList ?? []
    return (quantitativeList ?? []).filter((item) => item.isAreaModelIndex)
  }, [quantitativeList, isZX])

  const qualitativeData = useMemo(() => {
    if(hymx){
      return subjectVesselScoreList
    }
    if (!isZX) return qualitativeList ?? []
    const data = (quantitativeList ?? []).filter((item) => !item.isAreaModelIndex)
    return [...data, ...(qualitativeList ?? [])]
  }, [quantitativeList, qualitativeList, isZX])
  return (
    <DetailLayout anchorList={anchorList} offsetTop={120}>
      <RatResult ratingScoreRSP={ratingScoreRSP} />
      <NoEnumFileTable
        title="补充说明资料"
        canEdit={auth}
        canDelete={(record) => {
          const createBy = record?.createBy?.value ?? record.createBy
          return createBy == getUserInfo().id
        }}
        params={{ mainId, moduleType: 'RATING_CLIENT', renderCount }}
        columns={[
          { title: '资料名称', dataIndex: 'filename' },
          { title: '上传人', dataIndex: 'createByName' },
          { title: '上传时间', dataIndex: 'createTime' },
        ]}
      />
      <BaseInfo detail={{ ...baseInfoDetail, ...customerDetail }} />
      <HistoryRat detail={historyInfo} />
      <QuantitativeReport
        list={quantitativeData}
        canApproval={canApproval}
        ratingScoreRSP={ratingScoreRSP}
        store={store}
        isZX={isZX}
        hymx={hymx}
      />
      <QualitativeReport
        list={qualitativeData}
        canApproval={canApproval}
        ratingScoreRSP={ratingScoreRSP}
        store={store}
        isZX={isZX}
        hymx={hymx}
      />
      <AdjustmentReport list={adjustEventList} store={store} canApproval={canApproval} />
    </DetailLayout>
  )
}

export default observer(Index)
