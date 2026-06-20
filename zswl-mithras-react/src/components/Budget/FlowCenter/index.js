import { Page, Tabs } from '@zswl/components'
import { Badge, Empty } from 'antd'
import { useCallback, useEffect, useState } from 'react'
import BankFlow from './BankFlow'
import Fundamentals from './Fundamentals'
import ProjectSide from './ProjectSide'
import businessFundApi from '@/api/budget/flowCenter/businessFundApi'
import flowCenterApi from '@/api/budget/flowCenter/flowCenterApi'
import InterfaceManagementJs from './InterfaceManagement/index.js'

const Index = () => {
  const [count, setCount] = useState(0)
  const [fundCount, setFuncCount] = useState(0)
  const getData = useCallback(async () => {
    const res = await flowCenterApi.postCenterCount({})
    setCount(res.total)
  }, [])
  const getFundData = async () => {
    const { total: payTotal } = await businessFundApi.postFinanceList({
      flowType: 'PAY',
    })
    const { total: collectTotal } = await businessFundApi.postFinanceList({
      flowType: 'COLLECT',
    })

    setFuncCount(payTotal + collectTotal)
  }
  useEffect(() => {
    getData()
    getFundData()
  }, [])
  const items = [
    {
      label: '银行流水',
      key: '0',
      children: <BankFlow />,
    },
    {
      label: (
        <Badge count={count} offset={[10, -5]}>
          业务流水-项目端
        </Badge>
      ),
      key: '1',
      children: <ProjectSide getCount={getData} />,
    },
    {
      label: (
        <Badge count={fundCount} offset={[10, -5]}>
          业务流水-资金端
        </Badge>
      ),
      key: '2',
      children: <Fundamentals />,
    },
    {
      label: '接口管理',
      key: '3',
      children: <InterfaceManagementJs />,
    },
  ]
  return (
    <Page>
      <Tabs items={items} defaultActiveKey="0"></Tabs>
    </Page>
  )
}
export default Index
