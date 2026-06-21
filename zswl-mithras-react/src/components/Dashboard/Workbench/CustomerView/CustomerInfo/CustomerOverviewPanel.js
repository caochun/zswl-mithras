import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import CardPanelFieldsFilter from '../../../CardPanelFieldsFilter'
import StagePanel from '../../../StagePanel'
import { columnsFilterKey } from './Config'
import CustomerOverviewListDrawer from './CustomListDrawer/CustomerOverviewListDrawer'
import Store from './Store'
import styles from './index.less'

const CustomerOverviewPanel = () => {
  const store = useMemo(() => {
    return new Store()
  }, [])

  return (
    <>
      <CardPanelFieldsFilter
        title={'客户一览'}
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
                        store.customerListDrawer.open()
                      }}
                      rowStyle={{ width: `calc(${100 / 2}% - 10px)` }}
                      fieldsConfig={[
                        { name: '合计数', dataIndex: 'quantity' },
                        { name: '本月新增', dataIndex: 'incrementThisMonth', symbolIcon: true },
                      ]}
                    />
                  </div>
                )
              })}
            </div>
          )
        }}
      </CardPanelFieldsFilter>
      <CustomerOverviewListDrawer store={store} />
    </>
  )
}

export default observer(CustomerOverviewPanel)
