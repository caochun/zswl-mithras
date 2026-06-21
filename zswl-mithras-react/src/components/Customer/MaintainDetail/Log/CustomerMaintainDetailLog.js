import { Table, Page } from '@zswl/components'
import { useEffect, useMemo } from 'react'
import { observer } from '@zswl/admin'
import Store from './store'
import { saveServer } from '@/utils'

function CustomerMaintainDetailLog({ params: { id } }) {
  const store = useMemo(() => new Store(), [])
  store.tableLog.setParams({ cId: id })

  return (
    <Page store={store} header={null}>
      <div>
        <h3>版本日志</h3>
        <Table
                columnsFilter={'maintain_detail_log_idjs'}
                onFilter={(key,val) => saveServer('maintain_detail_log_idjs',val)}
          store={store.tableLog}
          columns={[
            { title: '版本号', dataIndex: 'version' },
            { title: '客户名称', dataIndex: 'clientName' },
            { title: '变更时间', dataIndex: 'gmtModify' },
            { title: '变更创建人', dataIndex: 'operatorName' },
            {
              title: '操作',
              actions(value) {
                if (value.canCompare) {
                  return [
                    {
                      name: '与上一版本对比',
                      onClick: () => store.toDifferentInfo({ id: value.id, clientId: id }),
                    },
                  ]
                }
                return '-'
              },
            },
          ]}
        />
      </div>
    </Page>
  )
}

export default observer(CustomerMaintainDetailLog)
