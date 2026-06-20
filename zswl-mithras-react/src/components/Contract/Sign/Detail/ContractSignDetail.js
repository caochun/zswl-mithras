import { Page } from '@zswl/components'
import { observer } from '@zswl/admin'
import Store from './Store'
import { useMemo } from 'react'
import { EditDescription } from '@/components/Table'
import DetailLayout from '@/components/DetailLayout'
import { getDescColumns } from '@/utils'
import { ALL_COLUMNS } from './Column'
import Signed from './Signed'
import UnSigned from './UnSigned'

const Index = ({ params: { id }, query: { contractId, contractSignStatus } }) => {
  const store = useMemo(() => new Store(), [])
  const pageData = store.page.getData()
  const { bizType } = pageData
  const isZLOrZZ = ['ZL', 'ZZ'].includes(bizType)

  const isSignedTable = contractSignStatus === 'SIGNED'

  const nameColumns = [
    '合同编号',
    '项目名称',
    '项目编号',
    '类别',
    '客户名称',
    '业务部门',
    '项目主办',
    '合同金额(元)',
    isZLOrZZ && '首期租金(元)',
    '保证金(元)',
    isZLOrZZ && {
      title: '租赁-手续费(元)',
      rename: '手续费(元)',
    },
    isZLOrZZ && '服务费/咨询费(元)',
    !isZLOrZZ && '手续费(元)',
    isZLOrZZ && '名义价款(元)',
  ]

  const columns = getDescColumns(ALL_COLUMNS, nameColumns)

  const anchorList = [{ label: '项目信息' }, { label: '合同相关材料' }].filter(Boolean)

  return (
    <Page store={store.page} params={{ id, contractId }}>
      <DetailLayout anchorList={anchorList}>
        <EditDescription canEdit={false} columns={columns} detail={pageData}></EditDescription>
        {isSignedTable ? <Signed store={store}></Signed> : <UnSigned store={store}></UnSigned>}
      </DetailLayout>
    </Page>
  )
}

export default observer(Index)
