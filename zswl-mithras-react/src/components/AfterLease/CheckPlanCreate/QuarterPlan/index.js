import { useEffect, useState, useMemo } from 'react'
import { observer } from '@zswl/admin'
import { Tabs } from 'antd'
import TableList from './TableList'
import Store from './store'
import styles from './index.less'

const AfterLeaseCheckPlanCreateQuarterPlan = ({ planId, canEditFlag, businessVersion }) => {
  const store = useMemo(() => {
    return new Store({})
  }, [])
  store.planId = planId
  const [curTab, setCurTab] = useState('0')
  const { quarterProjList } = store

  const totalCount = useMemo(() => {
    return quarterProjList?.reduce((prev, cur) => {
      return prev + Number(cur.toCheckCount)
    }, 0)
  }, [quarterProjList])

  useEffect(() => {
    store.getQuarterProjList({ id: planId, businessVersion })
  }, [planId, businessVersion])

  return (
    <>
      <div className={styles.title}>本次租后检查客户({totalCount})</div>
      <Tabs
        destroyInactiveTabPane
        activeKey={curTab}
        onChange={setCurTab}
        items={quarterProjList.map(
          ({ bizDeptName, bizDeptId, clientList, toCheckCount }, index) => {
            return {
              label: `${bizDeptName}(${toCheckCount})`,
              key: `${index}`,
              children: (
                <TableList
                  store={store}
                  canEditFlag={canEditFlag}
                  toCheckCount={toCheckCount}
                  bizDeptId={bizDeptId}
                  dataSource={clientList}
                ></TableList>
              ),
            }
          }
        )}
      ></Tabs>
    </>
  )
}

export default observer(AfterLeaseCheckPlanCreateQuarterPlan)
