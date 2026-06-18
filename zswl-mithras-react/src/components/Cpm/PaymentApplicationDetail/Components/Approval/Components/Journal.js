import { Table } from '@zswl/components'
import store from '../../../store'
import styles from '../index.less'
import { saveServer } from '@/utils'
const Journal = () => {
  return (
    <>
      <div className={styles.nav} style={{ marginBottom: 20 }}>
        <div className={styles.title}>审批日志</div>
      </div>
      <Table
              columnsFilter={'Approval_Components_Journal'}
              onFilter={(key,val) => saveServer('Approval_Components_Journal',val)}
        store={store.journalTable}
        autoRequest={false}
        columns={[
          {
            title: '操作时间',
            dataIndex: 'operateTime',
            dateFormat: 'yyyy-MM-DD HH:mm:ss',
            width: 190,
          },
          {
            title: '节点',
            dataIndex: 'taskNodeName',
            width: 100,
          },
          {
            title: '操作人',
            dataIndex: 'operatorName',
          },
          {
            title: '操作',
            dataIndex: 'typeName',
          },
          {
            title: '备注',
            dataIndex: 'message',

            render: (item) => {
              if (item) {
                return <>{item}</>
              }
              return '-'
            },
          },
        ]}
      />
    </>
  )
}

export default Journal
