import { observer } from '@zswl/admin'
import { Table, Button, SearchBar, Drawer } from '@zswl/components'
import { getTableColumns, getSearchColumns, monthFormat } from '@/utils'
import { ALL_COLUMNS } from './Column'
import { DatePicker } from 'antd'
import moment from 'moment'
import { saveServer } from '@/utils'

const groupName = '业务月度收入表详情'

const { Item } = SearchBar
const date = new Date()
const year = date.getFullYear()
const mouth = date.getMonth()

const Index = ({ store }) => {
  const { activityKey, listDrawer, listDrawerTable: table } = store

  const columns = getTableColumns(ALL_COLUMNS)
  const formColumns = getSearchColumns(ALL_COLUMNS, [
    // '业务组类别',
    '项目名称',
    '合同编号',
    '借据编号',
    '客户名称',
    '合同状态',
    '是否逾期',
  ])

  return (
    <Drawer
      store={listDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title={groupName}
      onClose={listDrawer.close}
    >
      <Table
        columnsFilter="财务管理_收入分摊表列表"
        onFilter={(key,val) => saveServer('财务管理_收入分摊表列表',val)}

        extra={[
          <Button type="primary" onClick={store.drawerDownloadExcel}>
            导出
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
            // businessGroup: activityKey,
          },
          items: [
            <Item label="月份" name="yearAndMonth" transform={(val) => val && monthFormat(val)}>
              <DatePicker picker={'month'} allowClear={false} />
            </Item>,
            ...formColumns,
          ],
        }}
      />
    </Drawer>
  )
}

export default observer(Index)
