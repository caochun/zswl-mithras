import { observer } from '@zswl/admin'
import { Title } from '@/components/Dashboard'
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
