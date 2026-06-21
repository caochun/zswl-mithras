import { Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import { saveServer } from '@/utils'

function CreditTableFinishBatch({ store }) {
  return (
    <div>
      <Table
        columnsFilter={'View_Batch_1'}
        onFilter={(key,val) => saveServer('View_Batch_1',val)}
        resizable
        columnWidth={180}
        onRow={(record) => {
          return {
            onClick: () => store.onRowClick({ record }),
          }
        }}
        store={store.$batchTable}
        searchbar={{
          labelCol: { span: 6 },
          items: [
            {
              label: '报送批次',
              name: 'batchNo',
            },
            { label: '报送时间', name: 'reportTime', type: 'rangePicker', showTime: true },
          ],
        }}
        columns={[
          {
            title: '报送批次',
            dataIndex: 'batchNo',
            render: (value) => {
              return <a>{value}</a>
            },
          },
          {
            title: '报送时间',
            width: 260,
            dataIndex: 'reportTime',
            render: (val) => {
              return val
            },
          },
          {
            title: '报送员',
            width: 260,
            dataIndex: 'reporterName',
            render: (val) => {
              return val
            },
          },
        ]}
      />
    </div>
  )
}

export default observer(CreditTableFinishBatch)
