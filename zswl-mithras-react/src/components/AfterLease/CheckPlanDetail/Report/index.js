import { useEffect, useMemo } from 'react'
import { Space } from 'antd'
import { Button, App, Table } from '@zswl/components'
import DataUpload from '@/components/DataUpload'
import styles from '../index.less'
import Store from './store'
import { saveServer } from '@/utils'

const Index = ({ planId, canEditFlag, businessVersion }) => {
  const store = useMemo(() => {
    return new Store({ planId })
  }, [planId])

  useEffect(() => {
    if (planId) {
      store.$table.search({ id: planId, businessVersion })
    }
  }, [planId, businessVersion])

  useEffect(() => {
    return () => {
      App.resetStore(store)
    }
  }, [])

  return (
    <div>
      <div className={styles.moduleHeader}>
        <div className={styles.moduleTitle}>租后检查总结报告</div>
        <div className={styles.moduleAction}>
          {canEditFlag && (
            <DataUpload maxCount={1} onChange={store.upload} accept="*">
              <Button type="primary">上传报告</Button>
            </DataUpload>
          )}
        </div>
      </div>
      <Table
        rowKey={'fileId'}
        store={store.$table}
        columnsFilter={'planDetail_Report_1'}
                onFilter={(key,val) => saveServer('planDetail_Report_1',val)}

        style={{ background: '#fff', padding: '0 10px' }}
        autoRequest={false}
        columns={[
          {
            title: '资料名称',
            dataIndex: 'fileName',
            render: (val, record) => {
              return <a onClick={() => store.preview(record.fileId, 1)}>{val}</a>
            },
          },
          {
            title: '上传人',
            dataIndex: 'creator',
          },
          {
            title: '上传时间',
            dataIndex: 'createTime',
          },
          {
            title: '操作',
            width: 220,
            render: (_, record) => {
              return (
                <Space>
                  <a onClick={() => store.download([record.fileId], record.fileName)}>下载</a>
                  {canEditFlag && <a onClick={() => store.remove(record)}>删除</a>}
                </Space>
              )
            },
          },
        ]}
      />
    </div>
  )
}

export default Index
