import { Table, Page } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import { useEffect } from 'react'
import { saveServer } from '@/utils'

function Index({ params: { id } }) {
  useEffect(() => {
    store.setProjectId(id)
  }, [id])

  return (
    <Page header={null}>
      <h3>版本日志</h3>
      <Table
        columnsFilter="review_detail_log_idjs"
        onFilter={(key, val) => saveServer('review_detail_log_idjs', val)}
        autoRequest={false}
        store={store.tableLog}
        columns={[
          { title: '版本号', dataIndex: 'version' },
          { title: '项目名称', dataIndex: 'projName' },
          { title: '变更时间', dataIndex: 'gmtModify', dateFormat: 'yyyy-MM-DD HH:mm:ss' },
          { title: '变更创建人', dataIndex: 'operatorName' },
          {
            title: '操作',
            actions(value) {
              if (value.canCompare) {
                return [
                  {
                    name: '与上一版本对比',
                    onClick: () => store.toDifferentInfo(value.id),
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

export default observer(Index)
