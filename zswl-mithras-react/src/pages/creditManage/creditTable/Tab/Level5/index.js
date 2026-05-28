import { useMemo, useEffect } from 'react'
import { Table, App } from '@zswl/components'
import { observer, getSessionStorage } from '@zswl/admin'
import { rules, getFormColumns } from '@/utils'
import { DateColumn, DatePickerEditable, InputColumn, MatchOptionColumn } from '@/components/Format'
import { COMMON_COLUMNS } from '@/pages/creditManage/creditTable/Column'
import CreateModal from '@/pages/creditManage/creditTable/Tab/Level5/CreateModal'
import { CREATETABLE_PARAMS } from '@/pages/creditManage/creditTable/Tab/config'
import Store from './store'
import EditButton from '../../Components/EditButton'
import statusRender from '../../Components/StautsRender'
import DeleteModal from './DeleteModal'
import { saveServer } from '@/utils'

function Index(props = {}) {
  const { canEdit, showActionColumn, componentKey, curTab, showSearch, channel } = props
  const formColumns = getFormColumns(
    COMMON_COLUMNS,
    ['编号', '五级分类', showActionColumn && '审批状态'].filter(Boolean)
  )
  const store = useMemo(() => {
    return new Store({ baseParams: props.baseParams })
  }, [props])

  useEffect(() => {
    if (componentKey === curTab) {
      if (channel !== 'EFFECT') store.$table.setParams(getSessionStorage(CREATETABLE_PARAMS))
      store.$table.search()
    }
  }, [componentKey, curTab])

  const columns = [
    InputColumn({
      title: '编号',
      dataIndex: 'paymentApplyCode',
      width: 280,
      fixed: 'left',
      render: statusRender,
    }),
    InputColumn({
      title: '合同编号',
      dataIndex: 'contractCode',
      width: 280,
      editable: false,
    }),
    InputColumn({
      title: '客户名称',
      dataIndex: 'clientName',
      width: 280,
      editable: false,
    }),
    MatchOptionColumn({
      title: '五级分类',
      dataIndex: 'fiveClass',
      matchOption: 'crFiveClass',
      editable: true,
    }),
    DateColumn({
      title: '五级分类认定日期',
      dataIndex: 'identificationDate',
      editable: true,
    }),
    showActionColumn &&
      MatchOptionColumn({
        title: '审批状态',
        width: 160,
        dataIndex: 'approvalStatus',
        editable: false,
      }),
  ]
  return (
    <div>
      <Table
        columnsFilter={'Tab_Level5_1'}
        onFilter={(key, val) => saveServer('Tab_Level5_1', val)}
        autoRequest={false}
        store={store.$table}
        columnWidth={160}
        resizable
        editable={false}
        rowClassName={(record, rowIndex) => {
          return record?.label?.value === 'REMOVE' ? 'table-row-remove' : ''
        }}
        searchbar={
          showSearch
            ? {
                labelCol: { span: 6 },
                items: formColumns,
              }
            : null
        }
        actions={[
          showActionColumn &&
            canEdit && {
              name: '新增',
              type: 'primary',
              onClick: store.createModal.open,
            },
        ].filter(Boolean)}
        columns={[
          ...columns,
          showActionColumn && {
            title: '操作',
            fixed: 'right',
            width: 200,
            actions(record, rowIndex) {
              return [
                <EditButton
                  record={record}
                  disabled={!canEdit}
                  columns={columns}
                  saveData={store.confirmEdit}
                />,
                {
                  name: record.label.value !== 'REMOVE' ? '删除' : '取消删除',
                  key: 'delete',
                  disabled: !canEdit,
                  onClick: () => store.deleteItem({ record, rowIndex }),
                },
              ].filter(Boolean)
            },
          },
        ].filter(Boolean)}
      />
      <CreateModal store={store}></CreateModal>
      <DeleteModal store={store} />
    </div>
  )
}

export default observer(Index)
