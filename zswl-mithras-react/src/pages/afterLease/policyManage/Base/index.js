import { Button } from '@zswl/components'
import BaseInfo from './BaseInfo'
import { history, getQuery, observer } from '@zswl/admin'
import Policy from './Policy'
import DetailLayout from '@/components/DetailLayout'

const anchorList = [
  { label: '合同信息', href: 'baseInfo' },
  { label: '保单信息', href: 'policy' },
]

const Base = ({ canEditFlag, id, store }) => {
  const { paymentCode, clientId, projName } = store.page.getData()
  const isFormApproval = getQuery('typeId') == 'approval'

  const goProcess = () => {
    const search = JSON.stringify({
      clientId,
      projName,
    })
    if (isFormApproval) {
      window.open(`/process/query?search=${search}`)
    } else {
      history.push(`/process/query?search=${search}`)
    }
  }

  return (
    <DetailLayout
      anchorList={anchorList}
      title={`新增保单`}
      extra={[
        <Button onClick={goProcess} type="link">
          查询历史流程
        </Button>,
        <Button type="primary" onClick={store.submit}>
          确认
        </Button>,
      ]}
    >
      <BaseInfo id={id} store={store} />
      <Policy canEditFlag={canEditFlag} mainId={id} baseDetailData={store.page.getData()} />
    </DetailLayout>
  )
}
export default observer(Base)
