import { observer } from '@zswl/admin'
import Title from '../../Title'
import AfterLease from './AfterLease/CustomerAfterLeasePanel'

const Index = ({ title, iconType }) => {
  return (
    <div>
      <Title title={title} iconType={iconType}></Title>
      <AfterLease />
    </div>
  )
}

export default observer(Index)
