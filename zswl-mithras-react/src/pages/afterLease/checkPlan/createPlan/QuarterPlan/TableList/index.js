import { Table } from '@zswl/components'
import { getTableColumns, saveServer } from '@/utils'
import { observer } from '@zswl/admin'
import EditModal from '../EditModal'
import { Space } from 'antd'
import ALL_COLUMNS from '@/components/AfterLease/CheckPlanColumns'

const Index = ({ dataSource = [], canEditFlag, toCheckCount, store }) => {
  const { $editModal } = store

  const nameColumns = [
    '客户编号',
    '客户名称',
    '客户类型',
    '客户主办',
    '本次是否需要检查',
    '检查形式',
    '检查报告模版',
    '协查风控经理',
  ]
  const columns = getTableColumns(ALL_COLUMNS, nameColumns)

  return (
    <>
      <Table
        resizable
        columnsFilter={'QuarterPlan_TableList_1'}
        onFilter={(key, val) => saveServer('QuarterPlan_TableList_1', val)}
        title={() => {
          return (
            <div>
              该部门租中客户数：{dataSource.length}个，本次需检查客户数：
              {toCheckCount}个
            </div>
          )
        }}
        dataSource={dataSource}
        scroll={{
          x: 1300,
        }}
        columns={[
          ...columns,
          {
            title: '操作',
            width: 80,
            fixed: 'right',
            render: (record) => {
              return (
                <Space>{canEditFlag && <a onClick={() => $editModal.open(record)}>编辑 </a>}</Space>
              )
            },
          },
        ]}
      ></Table>
      <EditModal store={$editModal}></EditModal>
    </>
  )
}

export default observer(Index)
