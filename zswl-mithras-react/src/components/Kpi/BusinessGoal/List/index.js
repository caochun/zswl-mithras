import { observer, history } from '@zswl/admin'
import { useMemo } from 'react'
import { Table, Page } from '@zswl/components'
import CreateModal from './CreateModal'
import { MatchOptionColumn } from '@/components/Format'
import Store from './Store'
import { saveServer } from '@/utils'

const Index = ({ path }) => {
  const store = useMemo(() => {
    return new Store()
  }, [])

  return (
    <Page>
      <Table
        columnsFilter={'kpi_businessGoal_1'}
        onFilter={(key, val) => saveServer('kpi_businessGoal_1', val)}
        title={() => <div style={{ textAlign: 'right' }}>待确认收入列表-实际利率法</div>}
        actions={[
          {
            name: '创建',
            type: 'primary',
            onClick: store.createModal.open,
          },
        ]}
        columns={[
          {
            title: '年度',
            dataIndex: 'year',
          },
          {
            title: '创建人',
            dataIndex: 'createByName',
          },
          {
            title: '创建时间',
            dataIndex: 'createTime',
          },
          {
            title: '最后更新人',
            dataIndex: 'updateByName',
          },
          {
            title: '最后更新时间',
            dataIndex: 'updateTime',
          },
          MatchOptionColumn({
            title: '状态',
            dataIndex: 'status',
            matchOption: 'earlyWarningState',
          }),
          {
            title: '操作',
            dataIndex: '状态',
            actions: (record) => {
              return [
                {
                  name: '修改',
                  onClick: () => {
                    history.push(`${path}/detail/${record.id}`)
                  },
                },
              ]
            },
          },
        ]}
        store={store.table}
        editable={false}
        scroll={{ x: true }}
      />
      <CreateModal store={store} />
    </Page>
  )
}

export default Index
