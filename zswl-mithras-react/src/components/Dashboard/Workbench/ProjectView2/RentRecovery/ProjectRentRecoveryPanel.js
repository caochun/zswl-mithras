import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import CardPanelFieldsFilter from '../../../CardPanelFieldsFilter'
import StagePanel from '../../../StagePanel'
import { columnsFilterKey, getItemConfigByGroupCode } from './Config'
import ProjectRentRecoveryListDrawer from './ListDrawer/ProjectRentRecoveryListDrawer'
import Store from './Store'
import styles from './index.less'

const ProjectRentRecoveryPanel = () => {
  const store = useMemo(() => {
    return new Store()
  }, [])

  return (
    <>
      <CardPanelFieldsFilter
        title={'项目情况'}
        getFieldsApi={store.getFieldsApi}
        columnsFilterKey={columnsFilterKey}
      >
        {(data) => {
          return (
            <div className={styles.content}>
              {data.map((item, index) => {
                return (
                  <div
                    className={styles.row}
                    key={index}
                    style={{
                      width: [
                        'PROJECT_VIEW_FINANCE_RENT_IN_MONTH',
                        'PROJECT_VIEW_FINANCE_PLEDGE',
                      ].includes(item.groupCode)
                        ? `calc(50% - 15px)`
                        : `calc(50% - 15px)`,
                    }}
                  >
                    <StagePanel
                      data={item}
                      onClick={(data) => {
                        store.setCurCardData(data)
                        store.customerListDrawer.open()
                      }}
                      contentStyle={{ justifyContent: 'space-around' }}
                      rowStyle={{ width: `calc(${100 / 3}% - 10px)` }}
                      fieldsConfig={({ groupCode }) => {
                        return getItemConfigByGroupCode(groupCode)?.fields ?? []
                      }}
                    />
                  </div>
                )
              })}
            </div>
          )
        }}
      </CardPanelFieldsFilter>
      <ProjectRentRecoveryListDrawer store={store} extraQueryParams={store.queryParams} />
    </>
  )
}

export default observer(ProjectRentRecoveryPanel)
