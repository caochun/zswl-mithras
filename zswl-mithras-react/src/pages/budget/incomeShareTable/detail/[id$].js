import { observer } from '@zswl/admin'
import { Table, Page, Button } from '@zswl/components'
import { Divider } from 'antd'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import Api from '../api'
import { saveServer } from '@/utils'

const Index = ({ params: { id, yearAndMonth }, query: { receiptCode } }) => {
  const columns = getTableColumns(ALL_COLUMNS())

  const table = Table.useStore({
    request: async (params) => { 
      return await Api.postDetailList({ ...params, receiptId: id, yearAndMonth })
    },
  })
  const downloadExcel = async () => {
    const params = table.getParams()
    await Api.postDownloadDetailList({
      yearAndMonth,
      ...params,
      receiptId: id,
      pageSize: 5000,
    })
  }
  return (
    <Page>
      <h3>收入分摊表明细-{receiptCode}</h3>
      <Divider></Divider>
      <Table
        columnsFilter="财务管理_收入分摊表列表_详情列表"
                onFilter={(key,val) => saveServer('财务管理_收入分摊表列表_详情列表',val)}
        
        actions={[
          <Button type="primary" onClick={downloadExcel}>
            下载
          </Button>,
        ]}
        title={() => <div style={{ textAlign: 'right' }}>单位：元</div>}
        columns={columns}
        store={table}
        editable={false}
        columnWidth={180}
        scroll={{ x: true }}
      />
    </Page>
  )
}

export default observer(Index)
