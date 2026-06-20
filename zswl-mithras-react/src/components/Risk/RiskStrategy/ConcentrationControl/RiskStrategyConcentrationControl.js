import { observer } from '@zswl/admin'
import { Tabs, Space } from 'antd'
import Client from '../../ConcentrationControl/Client'
import Connect from '../../ConcentrationControl/Connect'
import Group from '../../ConcentrationControl/Group'
import { useEffect, useState } from 'react'
import Api from '@/api/risk/concentrationControl'

function Index() {
  const [friInfo, setFriInfo] = useState({})
  const getFriInfo = async () => {
    const res = await Api.postConcentrationFri()
    setFriInfo(res)
  }
  useEffect(() => {
    getFriInfo()
  }, [])
  return (
    <Tabs
      defaultActiveKey="1"
      tabBarExtraContent={
        <Space>
          <span>资产负债表报告期：{friInfo?.friDate || '-'}</span>
        </Space>
      }
      items={[
        {
          label: `单一客户集中度`,
          key: '1',
          children: <Client friInfo={friInfo}></Client>,
        },
        {
          label: `单一集团集中度`,
          key: '2',
          children: <Group friInfo={friInfo}></Group>,
        },
        {
          label: `关联方集中度`,
          key: '3',
          children: <Connect friInfo={friInfo}></Connect>,
        },
      ]}
    ></Tabs>
  )
}

export default observer(Index)
