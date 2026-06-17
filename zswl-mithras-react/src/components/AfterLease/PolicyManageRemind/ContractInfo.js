import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from '@/components/AfterLease/PolicyColumns'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'
import { useState, useEffect } from 'react'
import Api from '@/api/afterLease/policyLedgerApi'

const nameColumns = [
  '合同编号',
  '合同金额(元)',
  '合同约定起租日',
  '合同约定终止日',
  '项目名称',
  '客户名称',
  '项目主办',
  '项目协办',
]

function Index({ id, dataSource }) {
  const [detail, setDetail] = useState({})
  const columns = getDescColumns(ALL_COLUMNS, nameColumns)

  const getDetail = async () => {
    const res = await Api.postContractDetail({ id, dataSource })
    setDetail(res)
  }

  useEffect(() => {
    id && getDetail()
  }, [id])

  return <EditDescription detail={detail} canEdit={false} columns={columns} title="合同信息" />
}

export default observer(Index)
