import { observer } from '@zswl/admin'
import { Select } from '@zswl/components'
import { isRiskManager } from '@/utils'

const Index = ({ defaultValue, onChange }) => {
  if (!isRiskManager()) return null
  return (
    <div>
      数据范围：
      <Select
        options={'workbenchProjectDataRange'}
        defaultValue={defaultValue}
        onChange={onChange}
      ></Select>
    </div>
  )
}

export default observer(Index)
