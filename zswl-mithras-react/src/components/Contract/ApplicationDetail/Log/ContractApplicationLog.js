import { useEffect } from 'react'
import { Table, Page } from '@zswl/components'
import { Tooltip } from 'antd'
import { observer, history } from '@zswl/admin'
import store from './store'
import { saveServer } from '@/utils'

function ContractApplicationLog({ params: { id }, query: { bizType, leaseLog } }) {
  useEffect(() => {
    if (id) {
      store.id = id
      store.$table.search()
    }
  }, [id])

  return (
    <Page store={store} header={null}>
      <h3>版本日志</h3>
      <Table
              columnsFilter={'detail_log_idjs'}
              onFilter={(key,val) => saveServer('detail_log_idjs',val)}
        store={store.$table}
        columns={[
          { title: '版本号', dataIndex: 'version' },
          {
            title: '合同名称',
            dataIndex: 'projName',
            render(val) {
              return <Tooltip title={val}>{val}</Tooltip>
            },
          },
          { title: '变更时间', dataIndex: 'gmtModify', dateFormat: 'yyyy-MM-DD HH:mm:ss' },
          { title: '变更创建人', dataIndex: 'operatorName' },
          {
            title: '操作',
            actions(value) {
              if (value.canCompare) {
                return [
                  {
                    name: '与上一版本对比',
                    onClick: () => {
                      history.push(
                        `/contract/list/detail/log/diffInfo/${
                          value.id
                        }?bizType=${bizType}&contractId=${id}&leaseLog=${leaseLog || ''}`
                      )
                    },
                  },
                ]
              }
              return '-'
            },
          },
        ]}
      />
    </Page>
  )
}

export default observer(ContractApplicationLog)
