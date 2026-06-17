import { Tag } from 'antd'
import _ from 'lodash'

const dataShowTypeEnum = [
  { label: '', value: 'NORMAL' },
  { label: '新增', value: 'ADD', color: '#d7d9d9' },
  { label: '删除', value: 'REMOVE', color: '#db7a76' },
  { label: '修改', value: 'MODIFY', color: '#f1c8a0' },
]
export default function statusRender(val, record) {
  const label = record?.label?.value
  const tag = dataShowTypeEnum.find((item) => item.value === label) ?? {}
  const newValue = _.isObject(val) && val !== null ? val.value : val
  return (
    <>
      {newValue ?? '-'}
      {tag?.label && (
        <Tag style={{ marginLeft: 12 }} color={tag.color}>
          {tag.label}
        </Tag>
      )}
    </>
  )
}
