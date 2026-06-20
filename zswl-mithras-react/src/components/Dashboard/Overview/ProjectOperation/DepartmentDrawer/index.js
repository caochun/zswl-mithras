import { observer } from '@zswl/admin'
import { Drawer, Table } from '@zswl/components'
import MultipleFieldBlock from '../MultipleFieldBlock'
import { saveServer } from '@/utils'

const Index = ({ store }) => {
  const tableDate = store.departmentTable?.getList()

  const column = [{ title: ' ', dataIndex: 'deptName', width: 160, fixed: 'left' }]
  tableDate[0]?.detail?.map((item, index) => {
    column.push({
      title: item.stageName,
      dataIndex: 'detail',
      render: (value) => <MultipleFieldBlock data={value[index]}></MultipleFieldBlock>,
    })
  })

  return (
    <Drawer
      store={store.departmentDrawer}
      width={'90%'}
      destroyOnClose
      extra={null}
      title="部门效率分析表"
      onClose={store.departmentDrawer.close}
    >
      <Table
                    columnsFilter={'ProjectOperation_DepartmentDrawer_1'}
                    onFilter={(key,val) => saveServer('ProjectOperation_DepartmentDrawer_1',val)}
        pagination={false}
        scroll={{ x: 2000, y: `calc(100vh - 150px)` }}
        columnWidth={200}
        store={store.departmentTable}
        columns={column}
      />
    </Drawer>
  )
}

export default observer(Index)
