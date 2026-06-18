import { TreeSelect } from 'antd'
import { http } from '@zswl/admin'
import { useEffect, useState } from 'react'

function filterTreeNode(value, node) {
  return node.name.toLowerCase().includes(value.toLowerCase())
}
function Index(props) {
  const [options, setOptions] = useState([])
  useEffect(() => {
    http.get('/org').then((res) => {
      setOptions(res || [])
    })
  }, [])
  return (
    <TreeSelect
      treeData={options}
      treeDefaultExpandAll
      allowClear
      showSearch
      {...props}
      placeholder={'选择机构'}
      fieldNames={{ value: 'id', label: 'name' }}
      filterTreeNode={filterTreeNode}
    />
  )
}

export default Index
