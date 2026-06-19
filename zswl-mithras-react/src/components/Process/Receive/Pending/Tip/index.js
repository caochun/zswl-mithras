import { Tooltip } from 'antd'
import IconFont from '@/components/Icon'

const Index = () => {
  return (
    <Tooltip placement="top" title={<div>超过12小时未审批，则标红</div>}>
      <IconFont style={{ color: '#2558e6' }} type="icon-icon_info_filled" />
    </Tooltip>
  )
}
export default Index
