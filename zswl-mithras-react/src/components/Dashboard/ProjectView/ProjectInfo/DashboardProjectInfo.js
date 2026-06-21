import { useMemo } from 'react'
import { getQuery, observer } from '@zswl/admin'
import CardPanelFieldsFilter from '../../CardPanelFieldsFilter'
import StagePanel from '../../StagePanel'
import { columnsFilterKey } from './Config'
import ProjectInfoDrawer from './InfoDrawer/ProjectInfoDrawer'
import Store from './Store'
import styles from './index.less'
import ProjectViewDataRangeSelect from '../SelectDataRange'

const DashboardProjectInfo = () => {
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
          <ProjectViewDataRangeSelect
            onChange={store.setQueryParams}
            defaultValue={store.queryParams.permissionType}
          ></ProjectViewDataRangeSelect>
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
      <ProjectInfoDrawer store={store} extraQueryParams={store.queryParams} />
    </>
  )
}

export default observer(DashboardProjectInfo)
