import { observer } from '@zswl/admin'
import { Table, Page, Button, SearchBar } from '@zswl/components'
import { getTableColumns, getFormColumns, monthFormat } from '@/utils'
import ALL_COLUMNS from './Column'
import { DatePicker } from 'antd'
import moment from 'moment'
import Api from '@/api/budget/incomeShareTable'
import { saveServer } from '@/utils'

const { Item } = SearchBar
const date = new Date()
const year = date.getFullYear()
const mouth = date.getMonth()

const Index = ({ path }) => {
  const columns = getTableColumns(ALL_COLUMNS(path))
  const formColumns = getFormColumns(ALL_COLUMNS(path), [
    '项目名称',
    '合同编号',
    '借据编号',
    '客户名称',
    '合同状态',
    '是否逾期',
  ])

  const table = Table.useStore({
    request: async (params) => {
      return await Api.postList(params)
    },
  })

  const downloadExcel = async () => {
    const params = table.getParams()
    await Api.postDownloadList({
      ...params,
      pageSize: 5000,
    })
  }

  return (
    <Page>
      <Table
        columnsFilter="财务管理_收入分摊表列表"
        onFilter={(key, val) => saveServer('财务管理_收入分摊表列表', val)}
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
        searchbar={{
          initialValues: {
            yearAndMonth: moment().year(year).month(mouth),
          },
          items: [
            <Item label="月份" name="yearAndMonth" transform={(val) => val && monthFormat(val)}>
              <DatePicker picker={'month'} allowClear={false} />
            </Item>,
            ...formColumns,
          ],
        }}
      />
    </Page>
  )
}

export default observer(Index)
