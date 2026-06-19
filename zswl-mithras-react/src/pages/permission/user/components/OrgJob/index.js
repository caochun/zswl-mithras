import { observer } from '@zswl/admin'
import { Select, TreeSelect, Row, Col, Space } from 'antd'
import { useEffect, useState } from 'react'
import Api from '@/api/permission/user'
function filterTreeNode(value, node) {
  return node.name.toLowerCase().includes(value.toLowerCase())
}
function Index({ orgList = [], value = {}, extra, onChange }) {
  const [jobOptions, setJobOptions] = useState([])

  const getJobList = async (val) => {
    const res = await Api.getJobList({ dictKey: 'job', orgId: val })
    setJobOptions(res || [])
  }
  const changeOrg = async (val) => {
    onChange({ org: val })
  }
  const changeJob = (val) => {
    onChange({ ...value, jobs: val })
  }
  useEffect(() => {
    if (value?.org) {
      getJobList(value?.org)
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
          options={jobOptions}
          onChange={changeJob}
          value={value.jobs}
          mode={'multiple'}
          placeholder={'选择岗位'}
          fieldNames={{ value: 'code', label: 'display' }}
          filterOption={(input, option) => {
            return option?.display?.indexOf(input) >= 0
          }}
        />
      </Col>
      <Col>{extra}</Col>
    </Row>
  )
}

export default observer(Index)
