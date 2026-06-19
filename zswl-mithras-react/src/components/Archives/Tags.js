import { Tag, Tooltip } from 'antd'
import { App } from '@zswl/components'

function Index({ value }) {
  if (value) {
    const content = value?.map((item) => (
      <Tag key={item} color={'blue'}>
        {App.matchOption('projEstablishBizType', item).label}
      </Tag>
    ))
    return (
      <Tooltip title={content} placement={'topLeft'}>
        {content}
      </Tooltip>
    )
  }
  return null
}

export default Index
