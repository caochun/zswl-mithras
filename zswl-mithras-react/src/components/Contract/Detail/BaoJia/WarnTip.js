import { Tooltip } from 'antd'
import IconFont from '@/components/Icon'

const Index = ({ title }) => {
  return (
    <span style={{ marginLeft: 5 }}>
      <Tooltip title={title}>
        <IconFont
          type="icon-icon_error_circle"
          style={{ color: '#eb2222', fontSize: 14 }}
        ></IconFont>
      </Tooltip>
    </span>
  )
}

export default Index
