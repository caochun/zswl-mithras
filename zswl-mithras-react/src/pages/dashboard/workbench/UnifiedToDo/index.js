import { Tabs } from 'antd'
import { observer } from '@zswl/admin'
import { Title } from '@/components/Dashboard'
import ApprovalToDo from './ApprovalToDo'
import ApprovalFormMe from './ApprovalFormMe'
import ApprovalInTransaction from './ApprovalInTransaction'
import ApprovalOutTransaction from './ApprovalOutTransaction'
import ApprovalDuplicate from './ApprovalDuplicate'
import News from './News'
import MonitorList from './MonitorList'
import MyPolicy from './MyPolicy'
import styles from './index.less'
import { useEffect, useMemo } from 'react'
import { isRiskManager, isAssetJon, isOperationDept, hasValue, hasPermission } from '@/utils'
import Store from './Store'

const CountRender = ({ count, label }) => {
  return hasValue(count) ? `${label}(${count})` : `${label}`
}

const Index = ({ title, iconType }) => {
  const store = useMemo(() => {
    return new Store()
  }, [])

  const { countObj, getCount, publicMonitorCount } = store

  const items = [
    {
      label: <CountRender label={'待办'} count={countObj.todoCount} />,
      key: '1',
      children: <ApprovalToDo />,
    },
    {
      label: <CountRender label={'我发起的'} count={countObj.myInitiateCount} />,
      key: '2',
      children: <ApprovalFormMe />,
    },
    {
      label: <CountRender label={'在办'} count={countObj.doingCount} />,
      key: '3',
      children: <ApprovalInTransaction />,
    },
    {
      label: <CountRender label={'已办'} count={countObj.doneCount} />,
      key: '4',
      children: <ApprovalOutTransaction />,
    },
    {
      label: <CountRender label={'流程抄送'} count={countObj.ccCount} />,
      key: '5',
      children: <ApprovalDuplicate />,
    },
    {
      label: <CountRender label={'消息'} count={countObj.msgCount} />,
      key: '6',
      children: <News store={store} />,
    },
    // 仅风控经理/资产管理岗位展示该模块
    hasPermission('riskcontrolopinionmonitorunresolved-dashboard') && {
      label: <CountRender label={'待处理舆情'} count={publicMonitorCount} />,
      key: '7',
      children: <MonitorList />,
    },
    // 运营部能查看
    isOperationDept() && {
      label: `待维护保单`,
      key: '8',
      children: <MyPolicy />,
    },
  ]

  useEffect(() => {
    getCount()
  }, [])

  return (
    <div>
      <Title title={title} iconType={iconType}></Title>
      <div className={styles.container}>
        <Tabs className={styles.tabs} items={items} onChange={getCount} destroyInactiveTabPane />
      </div>
    </div>
  )
}

export default observer(Index)
