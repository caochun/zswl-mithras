import { useEffect, useState } from 'react'
import { TreeSelect } from 'antd'
import { App } from '@zswl/components'

const ProcessTypeTree = ({ value, onChange }) => {
  const options = App.getData().optionsType
  const processModelType = options.processModelType
  const [treeData, setTreeData] = useState([])

  const convertLabelToTitle = (node) => {
    if (node.hasOwnProperty('label')) {
      node.title = node.label
      delete node.label
    }
    if (node.children && node.children.length > 0) {
      for (let i = 0; i < node.children.length; i++) {
        convertLabelToTitle(node.children[i])
      }
    }
    return node
  }

  useEffect(() => {
    const result = processModelType?.map((item) => {
      return convertLabelToTitle(item)
    })
    setTreeData(result)
  }, [processModelType])

  const filterTreeNode = (inputValue, treeNode) => {
    return treeNode.title.toLowerCase().includes(inputValue.toLowerCase())
  }

  const handleChange = (value) => {
    onChange?.(value)
  }

  return (
    <TreeSelect
      showSearch
      treeCheckable
      style={{ width: '100%' }}
      dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
      placeholder="请选择"
      allowClear
      value={value}
      treeData={treeData}
      onChange={handleChange}
      filterTreeNode={filterTreeNode}
    />
  )
}
export default ProcessTypeTree
