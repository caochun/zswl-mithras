import { EditDescription } from '@/components/Table'
import ALL_COLUMNS from '../Column'
import { getDescColumns } from '@/utils'
import Api from '@/api/budget/pricing/ftpInterest'
import { useEffect, useState } from 'react'

const nameColumns = ['借据编号', '合同编号', '客户名称', '项目名称', '业务部门', '项目主办']

const Index = ({ ftpInterestId }) => {
  const [detail, setDetail] = useState({})
  const baseInfo_columns = getDescColumns(ALL_COLUMNS(), nameColumns)

  const getDetail = async () => {
    const res = await Api.postBaseinfoGet({ ftpInterestId })
    setDetail(res)
  }

  useEffect(() => {
    ftpInterestId && getDetail()
  }, [ftpInterestId])

  return (
    <EditDescription
      title={'基本信息'}
      detail={detail}
      canEdit={false}
      columns={baseInfo_columns}
    />
  )
}

export default Index
