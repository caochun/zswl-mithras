import { observer } from '@zswl/admin'
import { Page } from '@zswl/components'
import { useMemo } from 'react'
import DetailLayout from '@/components/DetailLayout'
import ContractInfo from '@/pages/afterLease/policyManage/detail/ContractInfo'
import PolicyInfo from '@/pages/afterLease/policyManage/detail/PolicyInfo'
import PolicyContract from '@/pages/afterLease/policyManage/detail/PolicyContract'
import PolicyMaterial from '@/pages/afterLease/policyManage/detail/PolicyMaterial'
import MaterialList from '@/pages/afterLease/policyManage/detail/MaterialList'
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
