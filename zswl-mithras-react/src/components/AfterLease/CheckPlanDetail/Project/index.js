import { useEffect, useMemo, useState } from 'react'
import { observer } from '@zswl/admin'
import { Tabs } from 'antd'
import TableList from './TableList'
import Store from './store'
import styles from './index.less'

const Index = ({ planId, canEditFlag, detail, businessVersion, canEditFlags }) => {
  const store = useMemo(() => {
    return new Store({ detail, planId })
  }, [])

  const isQuarter = detail.planType === 'QUARTER'

  const [curTab, setCurTab] = useState('0')
  const { quarterProjList, notQuarterProjList } = store

  const totalCount = useMemo(() => {
    if (isQuarter) {
      return quarterProjList?.reduce((prev, cur) => {
        return prev + Number(cur.toCheckCount)
      }, 0)
    }
    return notQuarterProjList.length
  }, [quarterProjList, notQuarterProjList, isQuarter])

  const getList = () => {
    if (isQuarter) {
      store.getQuarterProjList({ id: planId, businessVersion })
    } else {
      store.getNotQuarterProjList({ id: planId, businessVersion })
    }
  }
  useEffect(() => {
    getList()
  }, [planId, businessVersion])

  return (
    <div className={styles.projectWrap}>
      <div className={styles.title}>本次租后检查客户({totalCount})</div>
      <>
        {isQuarter ? (
          <Tabs
            destroyInactiveTabPane
            activeKey={curTab}
            onChange={(val) => setCurTab(val)}
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
                      getList={getList}
                      canEditFlags={canEditFlags}
                      detail={detail}
                    ></TableList>
                  ),
                }
              }
            )}
          ></Tabs>
        ) : (
          <TableList
            store={store}
            canEditFlag={canEditFlag}
            dataSource={notQuarterProjList}
            toCheckCount={notQuarterProjList.length}
            detail={detail}
            canEditFlags={canEditFlags}
            getList={getList}
          ></TableList>
        )}
      </>
    </div>
  )
}

export default observer(Index)
