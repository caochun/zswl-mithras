import { observer } from '@zswl/admin'
import { DashboardTitle as Title } from '@/components/Dashboard/DashboardEntries'
import AfterLease from './AfterLease'

const Index = ({ title, iconType }) => {
  return (
    <div>
      <Title title={title} iconType={iconType}></Title>
      <AfterLease />
    </div>
  )
}

export default observer(Index)
