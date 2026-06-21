import { observer } from '@zswl/admin'
import Store from './store'
import { Table, App, Page } from '@zswl/components'
import styles from './index.less'
import { useEffect, useMemo } from 'react'
import { saveServer } from '@/utils'

function ProcessDesign() {
  const store = useMemo(() => new Store(), [])
  useEffect(() => {
    return () => {
      App.resetStore(store)
    }
  }, [])

  return (
    <>
      <Page>
        <Table
          columnsFilter={'process_design_1'}
          onFilter={(key, val) => saveServer('process_design_1', val)}
          rowKey={'modelId'}
          scroll={{
            x: 1200,
          }}
          rowClassName={(record) => {
            const { deployFlag } = record
            return !deployFlag ? styles.tableError : ''
          }}
          store={store.table}
          searchbar={{
            labelCol: { span: 6 },
            items: [
              {
                label: '模型名称',
                name: 'modelName',
                allowClear: true,
              },
              {
                label: '模型key',
                allowClear: true,
                name: 'modelKey',
              },
            ],
          }}
          columns={[
            {
              title: '模型ID',
              dataIndex: 'modelId',
              width: 80,
              actions(value) {
                return [
                  {
                    name: value.modelId,
                    to: `/process/design/detail/${value.modelId}`,
                  },
                ]
              },
            },
            {
              title: '模型名称',
              dataIndex: 'modelName',
              width: 220,
              render: (item) => {
                if (item) {
                  return (
                    // <Tooltip title={item}>
                    <div className={styles.processName}>{item}</div>
                    // </Tooltip>
                  )
                }
                return '-'
              },
            },
            {
              title: '版本号',
              dataIndex: 'version',
              width: 60,
              // tooltip: true,
            },
            {
              title: '修改时间',
              dataIndex: 'updateTime',
              width: 140,
              render: (item) => {
                if (item) {
                  return <>{item}</>
                }
                return '-'
              },
            },
            {
              title: '是否发布',
              dataIndex: 'deployFlag',
              width: 60,
              render: (item) => {
                return <>{item ? '是' : '否'}</>
              },
            },
            {
              title: '操作',
              width: 60,
              fixed: 'right',
              dataIndex: 'updateTime',
              actions(value) {
                return [
                  {
                    name: '发布',
                    onClick: () => {
                      store.publish(value.modelId)
                    },
                  },
                  {
                    name: '删除',
                    onClick: () => {
                      store.del(value.modelId)
                    },
                  },
                ]
              },
            },
          ]}
        />
      </Page>
    </>
  )
}

export default observer(ProcessDesign)
