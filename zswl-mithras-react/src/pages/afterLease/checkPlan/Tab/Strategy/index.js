import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { getTableColumns, getFormColumns,saveServer } from '@/utils'
import CreateModal from './CreateModal'
import Log from './Log'
import ALL_COLUMNS from './Column'
import { PageListDown } from '@/components'
import store from './store'

function Index() {
  const columns = getTableColumns(ALL_COLUMNS(), [
    // '计划名称',
    '客户名称',
    '部门',
    '风险敞口(元)',
    '投放日期',
    '五级分类',
    '项目经理',
    '检查人员',
    '跟进频率',
    '上次跟进时间',
    '上次跟进形式',
    {
      title: '下次跟进时间',
      dataIndex: 'deadLine',
    },
    '下次跟进形式',
    '计划状态',
    '创建时间',
    '变更时间',
  ])
  const formColumns = getFormColumns(ALL_COLUMNS(), [
    // '计划名称',
    '客户名称',
    '下次跟进形式',
    {
      title: '下次跟进时间',
      style: {
        width: 300,
      },
    },
  ])

  return (
    <div>
      <Table
        rowKey={'planId'}
        editable={false}
        scroll={{ x: 1500 }}
        columnWidth={180}
        columns={[
          ...columns,
          {
            title: '操作',
            width: 100,
            fixed: 'right',
            actions(record) {
              return [
                {
                  name: '修改记录',
                  onClick: () => store.updateLog(record),
                },
              ].filter(Boolean)
            },
          },
        ]}
        columnsFilter="afterLeaseCheckPlanStrategy"
                onFilter={(key,val) => saveServer('afterLeaseCheckPlanStrategy',val)}
        
        resizable
        selectable={{
          type: 'radio',
        }}
        store={store.$table}
        searchbar={{
          items: [...formColumns],
        }}
        extra={[<PageListDown key="1" module="checkPlanStrategy" table={store.$table} />]}
        actions={[
          {
            name: '计划编辑',
            key: 'edit',
            type: 'primary',
            access: 'afterleaseCheckplanAssetStrategyModify',
            onClick: store.editPlan,
          },
        ]}
      />
      <CreateModal store={store}></CreateModal>
      <Log store={store}></Log>
    </div>
  )
}

export default observer(Index)
