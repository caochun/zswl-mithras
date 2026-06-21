import { observer } from '@zswl/admin'
import Title from '../../Title'
import CustomerOverviewPanel from './CustomerInfo/CustomerOverviewPanel'
import CustomerPublicMonitorPanel from './CustomerPublicMonitor/CustomerPublicMonitorPanel'

const WorkbenchCustomerView = ({ title, iconType }) => {
  return (
    <div>
      <Title title={title} iconType={iconType}></Title>
      <CustomerOverviewPanel />
      <div style={{ height: 20 }}></div>
      <CustomerPublicMonitorPanel />
    </div>
  )
}

export default observer(WorkbenchCustomerView)
