import FormAmount from '@/components/Form/FormAmount'
import { observer } from '@zswl/admin'
import { Space } from 'antd'

const Index = ({ value, onChange }) => {
  return (
    <Space>
      转付日 - <FormAmount value={value} onChange={onChange} initFormat={1} /> 日
    </Space>
  )
}

Index.Detail = ({ value }) => {
  return (
    <Space>
      转付日 - <FormAmount.Format value={value} initFormat={1} /> 日
    </Space>
  )
}
export default observer(Index)
