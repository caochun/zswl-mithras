import { observer } from '@zswl/admin'
import { Select, TreeSelect, Row, Col, Space } from 'antd'
import { useEffect, useState } from 'react'
import Api from '@/api/permission/user'

function filterTreeNode(value, node) {
  return node.name.toLowerCase().includes(value.toLowerCase())
}
function Index({ orgList = [], value = {}, extra, onChange }) {
  const [roleOptions, setRoleOptions] = useState([])
  const getRoleList = async (val) => {
    const roleList = await Api.getRoleList({ orgId: val })
    setRoleOptions(roleList)
  }
  const changeOrg = async (val) => {
    onChange({ org: val })
  }
  const changeRole = (val) => {
    onChange({ ...value, roles: val })
  }
  useEffect(() => {
    if (value?.org) {
      getRoleList(value?.org)
    }
  }, [value?.org])
  return (
    <Row gutter={10} align={'middle'}>
      <Col span={10}>
        <TreeSelect
          treeData={orgList}
          treeDefaultExpandAll
          allowClear
          value={value.org}
          onChange={changeOrg}
          showSearch
          placeholder={'选择机构'}
          fieldNames={{ value: 'id', label: 'name' }}
          filterTreeNode={filterTreeNode}
        />
      </Col>
      <Col span={10}>
        <Select
          options={roleOptions}
          onChange={changeRole}
          value={value.roles}
          mode={'multiple'}
          placeholder={'选择角色'}
          fieldNames={{ value: 'id', label: 'name' }}
          filterOption={(input, option) => {
            return option?.name?.indexOf(input) >= 0
          }}
        />
      </Col>
      <Col>{extra}</Col>
    </Row>
  )
}

export default observer(Index)
