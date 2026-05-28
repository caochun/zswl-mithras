import { TreeSelect } from 'antd'
import { useState, useEffect } from 'react'
import { http } from '@zswl/admin'

function OrgTree(props) {
  const { ...rest } = props

  const [orgTree, setOrgTree] = useState([])

  async function getOrgTree() {
    const res = await http.get('/auth/getSameCompanyOrgTree')
    setOrgTree(res)
  }

  useEffect(() => {
    getOrgTree()
  }, [])

  return (
    <TreeSelect
      showSearch
      placeholder="请选择机构"
      treeDefaultExpandedKeys={['10000079']}
      {...rest}
      treeData={orgTree}
      fieldNames={{
        label: 'name',
        value: 'code',
        children: 'children',
      }}
    />
  )
}

export default OrgTree
