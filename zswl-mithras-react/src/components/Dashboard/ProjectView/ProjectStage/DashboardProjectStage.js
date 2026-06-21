import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import CardPanelFieldsFilter from '../../CardPanelFieldsFilter'
import StagePanel from '../../StagePanel'
import ProjectStageDrawer from './StageDrawer/ProjectStageDrawer'
import { columnsFilterKey } from './Config'
import Store from './Store'
import styles from './index.less'
import ProjectViewDataRangeSelect from '../SelectDataRange'

const DashboardProjectStage = ({ title, innerModule }) => {
  const store = useMemo(() => {
    return new Store()
  }, [])

  return (
    <>
      <CardPanelFieldsFilter
        extra={
          <ProjectViewDataRangeSelect
            onChange={store.setQueryParams}
            defaultValue={store.queryParams.permissionType}
          ></ProjectViewDataRangeSelect>
        }
        innerModule={innerModule}
        title={title || '项目阶段'}
        getFieldsApi={store.getFieldsApi}
        columnsFilterKey={columnsFilterKey}
        queryParams={store.queryParams}
      >
        {(data) => {
          return (
            <div className={styles.content}>
              {data.map((item) => {
                return (
                  <div className={styles.row}>
                    <StagePanel
                      data={item}
                      onClick={(data) => {
                        store.setStageData(data)
                        store.stageDrawer.open()
                      }}
                      contentStyle={{ justifyContent: 'space-around' }}
                      fieldsConfig={[
                        { name: '合计数', dataIndex: 'quantity' },
                        {
                          name:
                            item.groupCode === 'PROJECT_VIEW_STAGE_REPAYMENT'
                              ? '未收租金总额'
                              : '金额',
                          dataIndex: 'amount',
                        },
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
      <ProjectStageDrawer store={store} extraQueryParams={store.queryParams}></ProjectStageDrawer>
    </>
  )
}

export default observer(DashboardProjectStage)
