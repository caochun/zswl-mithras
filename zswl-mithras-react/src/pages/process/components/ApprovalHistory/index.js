import { observer } from '@zswl/admin'
import { useEffect } from 'react'
import { Table } from '@zswl/components'
import store from './store'
import { saveServer } from '@/utils'

function Index({ processInstanceId }) {
  useEffect(() => {
    if (processInstanceId) {
      store.table.search({ processInstanceId })
    }
  }, [processInstanceId])

  return (
    <div>
      <Table
        columnsFilter={'components_ApprovalHistory_1'}
        onFilter={(key, val) => saveServer('components_ApprovalHistory_1', val)}
        pagination={false}
        scroll={{ x: 1000 }}
        dataSource={store.table.list}
        autoRequest={false}
        columns={[
          {
            title: '操作时间',
            dataIndex: 'operateTime',
            dateFormat: 'yyyy-MM-DD HH:mm:ss',
            width: 180,
            tooltip: true,
          },
          {
            title: '节点',
            dataIndex: 'taskNodeName',
            width: 150,
            tooltip: true,
          },
          {
            title: '操作人',
            dataIndex: 'operatorName',
            width: 140,
          },
          {
            title: '操作',
            dataIndex: 'typeName',
            width: 140,
          },
          {
            title: '备注',
            dataIndex: 'message',
            render: (val) => {
              return (
                <div
                  dangerouslySetInnerHTML={{ __html: val }}
                  style={{ maxWidth: 350, overflow: 'hidden', textOverflow: 'ellipsis' }}
                ></div>
              )
            },
          },
        ]}
      />
    </div>
  )
}

export default observer(Index)
