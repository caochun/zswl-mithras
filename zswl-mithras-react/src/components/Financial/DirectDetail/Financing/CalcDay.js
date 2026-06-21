import { FormAmount } from '@/components/Form'
import { observer } from '@zswl/admin'
import { Space } from 'antd'

const FinancialDirectFinancingCalcDay = ({ value, onChange }) => {
  return (
    <Space>
      转付日 - <FormAmount value={value} onChange={onChange} initFormat={1} /> 日
    </Space>
  )
}

FinancialDirectFinancingCalcDay.Detail = ({ value }) => {
  return (
    <Space>
      转付日 - <FormAmount.Format value={value} initFormat={1} /> 日
    </Space>
  )
}
export default observer(FinancialDirectFinancingCalcDay)
