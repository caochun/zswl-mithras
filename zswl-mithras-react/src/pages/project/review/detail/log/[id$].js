import { Table, Page } from '@zswl/components'
import { observer, getQuery } from '@zswl/admin'
import store from './store'
import { useEffect } from 'react'
import { saveServer } from '@/utils'

function Index({ params: { id } }) {
  const bizType = getQuery('bizType')
  useEffect(() => {
    store.setProjectId(id)
  }, [id, bizType])

  return (
    <Page header={null}>
      <h3>版本日志</h3>
      <Table
        autoRequest={false}
        store={store.tableLog}
        columnsFilter={'project_review_detail_log_idjs'}
        onFilter={(key,val) => saveServer('project_review_detail_log_idjs',val)}
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
                    onClick: () => store.toDifferentInfo(value.id, bizType),
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
