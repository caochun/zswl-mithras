import { EditDescription } from '@/components/Table'
import ALL_COLUMNS from '../Column'
import Api from '@/api/budget/ftpYield/ftpYieldApi'
import { useEffect, useState } from 'react'
import { getTableColumns } from '@/utils'

const Index = ({ id: fundFinancingId, financingType }) => {
  const [detail, setDetail] = useState({})
  const columns = getTableColumns(ALL_COLUMNS, [
    '融资编号',
    '融资机构',
    '融资金额（元）',
    '资金主办',
  ])

  const getDetail = async () => {
    const res = await Api.postInfoDetail({ fundFinancingId, financingType })
    setDetail(res)
  }

  useEffect(() => {
    fundFinancingId && getDetail()
  }, [fundFinancingId])

  return <EditDescription title={'基本信息'} detail={detail} canEdit={false} columns={columns} />
}

export default Index
