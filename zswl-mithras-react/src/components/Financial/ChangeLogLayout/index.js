import { useEffect, useState } from 'react'
import { Collapse, Divider, Skeleton, Badge } from 'antd'
import { observer } from '@zswl/admin'
import styles from './index.less'
import { compareTableData } from '@/utils'

const { Panel } = Collapse

function FinancialChangeLogLayout({ compareApi, changeList }) {
  const [compareData, setCompareData] = useState({})
  const getCompareDetail = () => {
    compareApi?.().then((res) => {
      setCompareData(res)
    })
  }
  useEffect(() => {
    getCompareDetail?.()
  }, [compareApi])

  const { newData, oldData, moduleChanged } = compareData
  const getComponentData = (key, componentType = 'table') => {
    const newKeyData = newData[key]
    const oldKeyData = oldData[key]
    if (componentType === 'desc') {
      const { newDetail, detail, isLog } = compareTableData(newKeyData)
      return { newDetail: newDetail?.[0], detail: detail?.[0], isLog: isLog?.[0] }
    }
    if (componentType === 'table') {
      return { newDetail: newKeyData, detail: oldKeyData }
    }
  }
  const defaultActiveKey = []
  // const defaultActiveKey = changeList.map((item, index) => item.key)
  return (
    <>
      {Object.keys(compareData).length > 0 ? (
        <div className={styles.diffLog}>
          <h3>变更日志版本对比</h3>

          <Collapse defaultActiveKey={defaultActiveKey} accordion className={styles.collapse}>
            {changeList.map(({ label, key, Component, forceRender, componentType, render }, i) => {
              if (render) {
                return (
                  <Panel forceRender={forceRender} key={key} header={label}>
                    {render()}
                  </Panel>
                )
              }
              const { newDetail, detail, isLog } = getComponentData(key, componentType)
              return (
                <Panel
                  header={
                    <div>
                      <span>{label}变更日志</span>
                      {moduleChanged[key] && (
                        <>
                          &nbsp;&nbsp;
                          <Badge color={'red'} />
                        </>
                      )}
                    </div>
                  }
                  key={key}
                >
                  <Component detail={detail} canEdit={false} allModuleData={oldData} />
                  <Divider orientation="left" plain>
                    变更之后
                  </Divider>
                  <Component
                    detail={newDetail}
                    isLog={isLog}
                    allModuleData={newData}
                    canEdit={false}
                    isFormApproval
                  />
                </Panel>
              )
            })}
          </Collapse>
        </div>
      ) : (
        <Skeleton></Skeleton>
      )}
    </>
  )
}

export default observer(FinancialChangeLogLayout)
