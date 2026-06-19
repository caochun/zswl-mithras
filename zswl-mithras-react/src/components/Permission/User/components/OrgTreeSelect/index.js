import { TreeSelect } from 'antd'
import { useEffect, useState } from 'react'
import Api from '@/api/permission/user'

function filterTreeNode(value, node) {
  return node.name.toLowerCase().includes(value.toLowerCase())
}
function Index(props) {
  const [options, setOptions] = useState([])
  useEffect(() => {
    Api.getOrg().then((res) => {
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
