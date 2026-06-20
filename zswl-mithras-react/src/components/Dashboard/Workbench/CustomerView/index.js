import { observer } from '@zswl/admin'
import Title from '../../Title'
import CustomerInfo from './CustomerInfo'
import CustomerPublicMonitor from './CustomerPublicMonitor'

const Index = ({ title, iconType }) => {
  return (
    <div>
      <Title title={title} iconType={iconType}></Title>
      <CustomerInfo />
      <div style={{ height: 20 }}></div>
      <CustomerPublicMonitor />
    </div>
  )
}

export default observer(Index)
