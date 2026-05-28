import { Table, TableStore } from '@zswl/components'
import { useEffect, useMemo } from 'react'
import { saveServer } from '@/utils'

const init_columns = [
  { title: '版本号', dataIndex: 'version' },
  { title: '项目名称', dataIndex: 'projName' },
  { title: '变更时间', dataIndex: 'gmtModify', dateFormat: 'yyyy-MM-DD HH:mm:ss' },
  { title: '变更创建人', dataIndex: 'operatorName' },
]
function Index({ getListApi, params, toDifferentInfo, columns = init_columns }) {
  const tableLog = useMemo(() => {
    return new TableStore({
      request: async () => {
        try {
          return (await getListApi(params)) ?? []
        } catch (err) {
          return []
        }
      },
    })
  }, [])
  useEffect(() => {
    tableLog.search()
  }, [params?.id])
  return (
    <div>
      <h3>版本日志</h3>
      <Table
        resizable
        autoRequest={false}
        columnsFilter={'Table_VersionTable_1'}
                onFilter={(key,val) => saveServer('Table_VersionTable_1',val)}
        
        store={tableLog}
        columns={[
          ...columns,
          {
            title: '操作',
            actions(value) {
              if (value.canCompare) {
                return [
                  {
                    name: '与上一版本对比',
                    onClick: () => toDifferentInfo(value.id),
                  },
                ]
              }
              return '-'
            },
          },
        ]}
      />
    </div>
  )
}

export default Index
