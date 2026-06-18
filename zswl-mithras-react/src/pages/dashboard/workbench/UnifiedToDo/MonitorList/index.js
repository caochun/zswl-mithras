import { Table } from '@zswl/components'
import { observer, history } from '@zswl/admin'
import { getTableColumns } from '@/utils'
import { RiskPublicMonitorColumns as ALL_COLUMNS } from '@/components/Risk/PublicMonitorColumnsEntries'
import Api from '../api'
import { saveServer } from '@/utils'

const Index = () => {
  const $monitorTable = Table.useStore({
    pagination: {
      pageSize: 5,
    },
    request: async (params) => {
      const res = await Api.postMonitorUnresolved({
        ...params,
      })
      return res
    },
  })
  const columns = getTableColumns(ALL_COLUMNS(), [
    '标题',
    '客户名称',
    '信息发布日期',
    '预警星级',
    '预警信号',
  ])

  return (
    <div>
      <div style={{ color: 'red' }}>
        2024-12-26日期后的舆情系统已自动推送至对应风控经理/资管经理待办，请直接通过【统一待办-待办】处理
      </div>
      <Table
        scroll={{ x: 1200 }}
        store={$monitorTable}
        editable={false}
        actions={[]}
        columnsFilter={'工作台_统一视图_待处理舆情'}
        onFilter={(key, val) => saveServer('工作台_统一视图_待处理舆情', val)}
        columns={[
          ...columns,
          {
            title: '操作',
            width: 120,
            actions(record) {
              return [
                {
                  name: '查看',
                  onClick: () => {
                    history.push(`/risk/publicMonitor?recordId=${record.id}`)
                  },
                },
              ]
            },
          },
        ]}
      />
    </div>
  )
}

export default observer(Index)
