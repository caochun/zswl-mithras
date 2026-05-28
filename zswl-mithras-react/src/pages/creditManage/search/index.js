import { Button, Page, Table } from '@zswl/components'
import { getQuery, observer } from '@zswl/admin'
import { useEffect, useMemo } from 'react'
import ALL_COLUMNS from './Column'
import Store from './store'
import { saveServer } from '@/utils'
import AddModal from './AddModal'
import { DownOutlined } from '@ant-design/icons'
import { Dropdown, Menu } from 'antd'

const Index = ({ params = {}, onClose }) => {
  const { reload } = getQuery()
  const store = useMemo(() => new Store(params), [params])

  useEffect(() => {
    if (reload === 'true') {
      store.table.search()
    }
  }, [reload])
  return (
    <Page store={store.page}>
      <Table
        columnsFilter={'View_Account_1'}
        onFilter={(key, val) => saveServer('View_Account_1', val)}
        store={store.table}
        resizable
        selectable
        actions={[
          <AddModal store={store} />,
          <Button onClick={store.handleSync} disabled>
            打开后台查询状态
          </Button>,
        ]}
        extra={[
          <Dropdown
            overlay={
              <Menu
                onClick={(e) => store.batchDownload(e)}
                items={[
                  {
                    label: `批量导出列表数据`,
                    key: '1',
                  },
                  { label: '批量导出征信报告', key: '2' },
                ]}
              />
            }
          >
            <Button loading={store.loading}>
              批量导出
              <DownOutlined />
            </Button>
          </Dropdown>,
        ]}
        columnWidth={160}
        scroll={{ x: 1400 }}
        columns={[
          ...ALL_COLUMNS({ onClose }),
          {
            title: '操作',
            dataIndex: 'operation',
            width: 200,
            fixed: 'right',
            actions: (record) => [
              {
                name: '删除',
                onClick: () => store.handleDelete(record.id),
                disabled: record.applyStatus !== 'UN_SUBMIT',
                confirm: true,
              },
              {
                name: '报告预览',
                onClick: () => store.handlePreview(record.reportFileIds),
                disabled: record.reportFileIds?.length === 0,
              },

              {
                name: '报告下载',
                onClick: () => store.handleDownload(record),
                disabled: record.reportFileIds?.length === 0,
              },
            ],
          },
        ]}
      />
    </Page>
  )
}

export default observer(Index)
