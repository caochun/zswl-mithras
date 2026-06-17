import { observer } from '@zswl/admin'
import { Page } from '@zswl/components'
import { useMemo } from 'react'
import DetailLayout from '@/components/DetailLayout'
import ContractInfo from '@/components/AfterLease/PolicyManageDetail/ContractInfo'
import PolicyInfo from '@/components/AfterLease/PolicyManageDetail/PolicyInfo'
import PolicyContract from '@/components/AfterLease/PolicyManageDetail/PolicyContract'
import PolicyMaterial from '@/components/AfterLease/PolicyManageDetail/PolicyMaterial'
import MaterialList from '@/components/AfterLease/PolicyManageDetail/MaterialList'
import Store from './store'

const Index = ({ params: { id }, query: { dataSource } }) => {
  const store = useMemo(() => {
    return new Store({ id })
  }, [id])

  const policyInfoDetail = store.page.getData()

  const conmonParam = { id, dataSource, canEdit: false, detail: policyInfoDetail }

  const anchorList = [
    { label: '合同信息' },
    { label: '保单信息' },
    { label: '保单资料' },
    { label: '合同保单' },
    { label: '资料清单' },
  ]

  return (
    <Page store={store} header={null} params={{ id, dataSource }}>
      <DetailLayout anchorList={anchorList} title="保单详情" extra={null} moduleName="policyManage">
        <ContractInfo {...conmonParam} />
        <PolicyInfo {...conmonParam} />
        <PolicyMaterial {...conmonParam} />
        <PolicyContract {...conmonParam} />
        <MaterialList {...conmonParam} />
      </DetailLayout>
    </Page>
  )
}

export default observer(Index)
