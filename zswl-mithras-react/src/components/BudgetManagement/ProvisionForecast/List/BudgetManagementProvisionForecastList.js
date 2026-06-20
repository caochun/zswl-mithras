import { observer } from '@zswl/admin'
import store from './store'
import IconFont from '@/components/Icon'
import { Page, Table } from '@zswl/components'
import ALL_COLUMNS from '../Column'
import { getTableColumns, saveServer } from '@/utils'
import EditModal from '../EditModal'

/**
 * 拨备预测计划页面组件
 * 用于展示和管理拨备预测计划列表
 */
const Index = () => {
  // 定义表格显示的列名
  const nameColumns = ['计划名称', '预测时间', '创建时间']
  // 获取表格列配置
  const columns = getTableColumns(ALL_COLUMNS, nameColumns)

  return (
    <Page>
      <Table
        columnWidth={140}
        resizable
        store={store.table}
        actions={[
          {
            name: (
              <span>
                <IconFont type="icon-icon_add" />
                创建拨备预测
              </span>
            ),
            onClick: store.createModal.open,
            type: 'primary',
          },
        ]}
        columnsFilter={'budgetManagement_provisionForecast_1'}
        onFilter={(key, val) => saveServer('budgetManagement_provisionForecast_1', val)}
        columns={[
          ...columns,
          {
            title: '操作',
            dataIndex: 'actions',
            width: 120,
            fixed: 'right',
            actions: (record) => [
              record.source && {
                name: '删除',
                confirm: '确认删除该拨备预测计划吗？',
                onClick: () => store.remove(record),
              },
            ],
          },
        ]}
      />
      <EditModal />
    </Page>
  )
}

export default observer(Index)
