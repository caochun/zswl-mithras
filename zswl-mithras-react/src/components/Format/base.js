import { App } from '@zswl/components'
import { Space, Tag, Tooltip } from 'antd'

/**
 * 是否超时
 */
export function TimeOutFormat({ value }) {
  const { color } = App.matchOption('timeout', value)
  return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <span
        style={{
          width: 12,
          height: 12,
          backgroundColor: color,
          display: 'inline-block',
          borderRadius: '50%',
          margin: '0 5px',
        }}
      />
    </div>
  )
}
