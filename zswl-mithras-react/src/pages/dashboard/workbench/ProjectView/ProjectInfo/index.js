import { useMemo } from 'react'
import { getQuery, observer } from '@zswl/admin'
import { CardPanelFieldsFilter, StagePanel } from '@/components/Dashboard'
import { columnsFilterKey } from './Config'
import InfoDrawer from './InfoDrawer'
import Store from './Store'
import styles from './index.less'
import SelectDataRange from '../Component/SelectDataRange'

const Index = () => {
  const { openModal } = getQuery()

  const openDrawer = (data) => {
    store.setInfoData(data)
    store.infoDrawer.open()
  }
  const store = useMemo(() => {
    return new Store()
  }, [])

  return (
    <>
      <CardPanelFieldsFilter
        title={'项目信息'}
        getFieldsApi={store.getFieldsApi}
        columnsFilterKey={columnsFilterKey}
        queryParams={store.queryParams}
        extra={
          <SelectDataRange
            onChange={store.setQueryParams}
            defaultValue={store.queryParams.permissionType}
          ></SelectDataRange>
        }
      >
        {(data) => {
          return (
            <div className={styles.content}>
              {data.map((item) => {
                return (
                  <div className={styles.row}>
                    <StagePanel
                      data={item}
                      onClick={openDrawer}
                      rowStyle={{ width: `calc(${100 / 3}% - 10px)` }}
                      openDrawer={item.groupCode === openModal}
                      fieldsConfig={[
                        { name: '合计数', dataIndex: 'quantity' },
                        { name: '金额', dataIndex: 'amount' },
                      ]}
                    />
                  </div>
                )
              })}
            </div>
          )
        }}
      </CardPanelFieldsFilter>
      <InfoDrawer store={store} extraQueryParams={store.queryParams} />
    </>
  )
}

export default observer(Index)
