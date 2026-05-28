import { Tooltip } from 'antd'
import IconFont from '@/components/Icon'

const Index = ({ versible = '' }) => {
  return (
    <Tooltip
      placement="top"
      title={
        <div>
          <div>1. 可输入数值、公式、及数值区间</div>
          <div>2. 以'='开始，表示为公式或数值区间</div>
          {versible && <div>3. 变量值为'{versible}'</div>}
        </div>
      }
    >
      <IconFont style={{ color: '#2558e6' }} type="icon-icon_info_filled" />
    </Tooltip>
  )
}
export default Index
