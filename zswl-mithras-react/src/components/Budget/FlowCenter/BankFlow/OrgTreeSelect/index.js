import { TreeSelect } from 'antd'
import Api from '@/api/budget/flowCenter/bankFlowProcessingCenterApi'
import { useEffect, useState } from 'react'

function filterTreeNode(value, node) {
  return node.name.toLowerCase().includes(value.toLowerCase())
}
function BudgetFlowCenterBankFlowOrgTreeSelect(props) {
  const [options, setOptions] = useState([])
  useEffect(() => {
    Api.getOrgTree().then((res) => {
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

export default BudgetFlowCenterBankFlowOrgTreeSelect
