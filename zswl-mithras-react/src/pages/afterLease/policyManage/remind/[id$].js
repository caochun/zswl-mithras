import { Page } from '@zswl/components'
import { observer } from '@zswl/admin'
import DetailLayout from '@/components/DetailLayout'
import PolicyInfo from './PolicyInfo'
import ContractInfo from './ContractInfo'
import Policy from '@/pages/cpm/paymentApplication/detail/Components/Policy'
import Api from '../api'

const Index = ({ id, taskActivityId }) => {
  const anchorList = [{ label: '原保单信息' }, { label: '续保保单' }, { label: '合同信息' }]
  const pageParams = { id, dataSource: 'policy' }

  const pageStore = Page.useStore({
    request: async (params) => {
      return await Api.postLengerDetail(params)
    },
  })
  const pageData = pageStore.getData()

  return (
    <Page params={pageParams} store={pageStore}>
      <DetailLayout anchorList={anchorList} title="保单详情" extra={null}>
        <PolicyInfo detail={pageData}></PolicyInfo>
        <Policy
          // 续保保单模块项目经理和运营经办可编辑。
          canEditFlag={['userTask_projectSponsor', 'userTask_operation_hand'].includes(
            taskActivityId
          )}
          paramsAsPolicy={{ id, pageSource: 'policy', type: 'policyRemind' }}
          mainId={pageData.paymentId}
        ></Policy>
        <ContractInfo {...pageParams}></ContractInfo>
      </DetailLayout>
    </Page>
  )
}

export default observer(Index)
