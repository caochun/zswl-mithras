import { Page } from '@zswl/components'
import { history, observer } from '@zswl/admin'
import paymentApprovalApi from '@/api/financial/paymentApprovalApi'
import { VersionTable } from '@/components/Table'

function Index({ params: { id: mainId } }) {
  //变更日志详情
  const toDifferentInfo = (id) => {
    history.push(`/financial/payment/detail/log/diffInfo/${id}`)
  }
  return (
    <Page header={null}>
      <VersionTable
        params={{ mainId }}
        getListApi={paymentApprovalApi.postVersionList}
        toDifferentInfo={toDifferentInfo}
      />
    </Page>
  )
}

export default observer(Index)
