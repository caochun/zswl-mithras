import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { DashboardCardPanelFieldsFilter as CardPanelFieldsFilter, DashboardStagePanel as StagePanel } from '@/components/Dashboard/DashboardEntries'
import { columnsFilterKey } from './Config'
import AfterLeaseListDrawer from './AfterLeaseListDrawer'
import Store from './Store'
import styles from './index.less'

const Index = () => {
  const store = useMemo(() => {
    return new Store()
  }, [])

  return (
    <>
      <CardPanelFieldsFilter
        title={'租后管理'}
        getFieldsApi={store.getFieldsApi}
        columnsFilterKey={columnsFilterKey}
      >
        {(data) => {
          return (
            <div className={styles.content}>
              {data.map((item, index) => {
                return (
                  <div className={styles.row} key={index}>
                    <StagePanel
                      data={item}
                      onClick={(data) => {
                        store.setCurCardData(data)
                        store.listDrawer.open()
                      }}
                      rowStyle={{ width: `calc(${100 / 2}% - 10px)` }}
                      fieldsConfig={[
                        { name: '待提交', dataIndex: 'leftCount' },
                        { name: '审批中', dataIndex: 'rightCount' },
                      ]}
                    />
                  </div>
                )
              })}
            </div>
          )
        }}
      </CardPanelFieldsFilter>
      <AfterLeaseListDrawer store={store} />
    </>
  )
}

export default observer(Index)
