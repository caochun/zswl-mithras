import { observer } from '@zswl/admin'
import { useEffect, useState } from 'react'
import { Button } from '@zswl/components'
import { Tabs, message } from 'antd'
import DataUpload from '@/components/DataUpload'
import Collection from '../../RelateMonitor/Collection'
import Payment from '../../RelateMonitor/Payment'
import Api from '@/api/risk/relatedTransaction'

function RiskStrategyRelateMonitor() {
  const [curTab, setCurTab] = useState('1')
  const [pullSelect, setPullSelect] = useState([])

  const importSheet = async (values) => {
    const { fileList } = DataUpload.classify(values)
    await Api.postClientAdd({
      file: fileList[0],
    })
    message.success('导入成功')
    setCurTab('-1')
    setTimeout(() => {
      setCurTab('1')
    }, 0)
  }
  const getPullDown = async () => {
    const res = await Api.postClientPullDown()
    const result = {}
    Object.entries(res).forEach(([field, item]) => {
      result[field] = []
      item?.map((i) => {
        result[field].push({
          label: i,
          value: i,
        })
      })
    })
    setPullSelect(result)
  }
  useEffect(() => {
    getPullDown()
  }, [])

  return (
    <Tabs
      destroyInactiveTabPane
      onTabClick={setCurTab}
      activeKey={curTab}
      // tabBarExtraContent={{
      //   right: (
      //     <DataUpload maxCount={1} onChange={importSheet} accept=".xlsx">
      //       <Button type="primary">导入</Button>
      //     </DataUpload>
      //   ),
      // }}
      items={[
        {
          label: `付款流水列表`,
          key: '1',
          children: <Payment pullSelect={pullSelect}></Payment>,
        },
        {
          label: `收款流水列表`,
          key: '2',
          children: <Collection pullSelect={pullSelect}></Collection>,
        },
      ]}
    ></Tabs>
  )
}

export default observer(RiskStrategyRelateMonitor)
