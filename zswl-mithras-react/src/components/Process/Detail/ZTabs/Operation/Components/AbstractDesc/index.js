import customerRatApi from '@/api/process/detail/customerRatingApi'
import debtRatApi from '@/api/process/detail/debtRatingApi'
import { EditDescription } from '@/components/Table'
import { observer } from '@zswl/admin'
import { DescStore, Descriptions } from '@zswl/components'
import { useEffect, useMemo, useState } from 'react'

const Index = ({ detail }) => {
  const descStore = useMemo(() => new DescStore(), [])
  const [baseInfoDetail, setBaseInfoDetail] = useState({})
  const apiFunc = {
    RATING_AMOUNT: async (params) => {
      const { businessKey } = params
      const { quota = {}, scoreRSP = {} } = await debtRatApi.postAmountReport({ id: businessKey })
      return { ...quota, ...scoreRSP }
    },
    RATING_CLIENT: async (params) => {
      const { businessKey } = params
      const baseInfoDetail = await customerRatApi.postClientDetail({ id: businessKey })
      setBaseInfoDetail(baseInfoDetail)
      const res = await customerRatApi.postClientReport({ id: businessKey })
      return res?.ratingScoreRSP ?? {}
    },
  }
  const getDataSource = async () => {
    const func = apiFunc[detail.mainModule]
    const res = await func(detail)
    descStore.setData(res)
  }
  // 是政信类的
  const isZX = useMemo(
    () => ['client_qxj_service', 'client_djs_service'].includes(baseInfoDetail?.modelCode),
    [baseInfoDetail?.modelCode]
  )

  const DescMap = {
    RATING_AMOUNT: {
      columns: [
        { title: '建议项目限额', dataIndex: 'projQuota' },
        { title: '租赁物价值', dataIndex: 'leaseItemPrice' },
        { title: '评估主体评级调整系数', dataIndex: 'ratingAdjustFactor' },
        { title: '增信措施调整价值', dataIndex: 'creditMeasurePrice' },
        { title: '审查结果', dataIndex: 'finalScore', render: (text) => text ?? '-' },
      ],
    },
    RATING_CLIENT: {
      columns: [
        isZX && { title: '区域得分', dataIndex: 'areaScore', render: (text) => text ?? '-' },
        isZX && { title: '主体得分', dataIndex: 'subjectScore', render: (text) => text ?? '-' },
        !isZX && {
          title: detail.modelCode === 'client_hymx' ? '客户综合得分' : '定量得分',
          dataIndex: detail.modelCode === 'client_hymx' ? 'subjectVesselScore' : 'quantitativeScore',
          render: (text) => text ?? '-',
        },
        !isZX && {
          title: detail.modelCode === 'client_hymx' ? '标的物得分' : '定性得分',
          dataIndex: detail.modelCode === 'client_hymx' ? 'custScore' : 'qualitativeScore',
          render: (text) => text ?? '-',
        },
        { title: '综合得分', dataIndex: 'modelScore', render: (text) => text ?? '-' },
        { title: '违约率', dataIndex: 'defaultRate', render: (text) => text ?? '-' },
        { title: '系统评级结果', dataIndex: 'score', render: (text) => text ?? '-' },
        { title: '业务调整结果', dataIndex: 'adjustScore' },
        { title: '审查结果', dataIndex: 'finalScore', render: (text) => text ?? '-' },
      ],
    },
  }
  const { columns } = DescMap[detail.mainModule]
  useEffect(() => {
    getDataSource()
  }, [])
  return (
    <EditDescription title={null} items={columns} store={descStore} hiddenButton></EditDescription>
  )
}

export default observer(Index)
