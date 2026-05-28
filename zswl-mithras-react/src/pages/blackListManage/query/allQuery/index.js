import RiskIframe from '@/components/RiskIframe'
import { Page } from '@zswl/components'

const Index = ({ query }) => {
  return <RiskIframe path="blackListManage/query/allQuery" query={query} />
}
export default Index
