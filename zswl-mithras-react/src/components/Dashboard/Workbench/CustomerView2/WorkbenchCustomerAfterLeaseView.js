import { observer } from '@zswl/admin'
import Title from '../../Title'
import CustomerAfterLeasePanel from './AfterLease/CustomerAfterLeasePanel'

const WorkbenchCustomerAfterLeaseView = ({ title, iconType }) => {
  return (
    <div>
      <Title title={title} iconType={iconType}></Title>
      <CustomerAfterLeasePanel />
    </div>
  )
}

export default observer(WorkbenchCustomerAfterLeaseView)
