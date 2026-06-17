import { useMemo, useEffect } from 'react'
import { Table, App } from '@zswl/components'
import { observer, getSessionStorage } from '@zswl/admin'
import { getFormColumns } from '@/utils'
import { AmountColumn, DateColumn, InputColumn, MatchOptionColumn } from '@/components/Format'
import { CREATETABLE_PARAMS, AmountFormat } from '@/components/CreditManage/CreditTableConfig'
import { COMMON_COLUMNS } from '@/components/CreditManage/CreditTableColumns'
import Store from './store'
import EditButton from '../../Components/EditButton'
import statusRender from '@/components/CreditManage/CreditTableStatusRender'
import DeleteModal from '../Level5/DeleteModal'
import _ from 'lodash'
import { Switch } from 'antd'
import { saveServer } from '@/utils'

const defaultAmount = {
  requiredMark: true,
  wrapItemProps: {
    required: true,
  },
  editable: true,
}
function Index(props = {}) {
  const { canEdit, showActionColumn, componentKey, curTab, showSearch, channel } = props
  const formColumns = getFormColumns(
    COMMON_COLUMNS,
    ['编号', showActionColumn && '审批状态'].filter(Boolean)
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
    AmountColumn({
      ...defaultAmount,
      title: '逾期本金(元)',
      dataIndex: 'overduePrincipal',
      width: 200,
    }),
    AmountColumn({
      title: '逾期天数(天)',
      dataIndex: 'overdueDay',
      editable: true,
      initFormat: 1,
    }),
    AmountColumn({
      ...defaultAmount,
      title: '逾期总额(元)',
      dataIndex: 'overdueTotal',
    }),
    DateColumn({
      title: '逾期改变日期',
      dataIndex: 'overdueChangeDate',
      editable: true,
      requiredMark: true,
      rules: [{ required: true, message: '请选择逾期改变日期' }],
    }),
    showActionColumn &&
      MatchOptionColumn({
        title: '审批状态',
        width: 160,
        dataIndex: 'approvalStatus',
        matchOption: 'crApprovalStatus',
        editable: false,
      }),
  ]
  return (
    <div>
      <Table
        columnsFilter={'Tab_YuQi_1'}
        onFilter={(key, val) => saveServer('Tab_YuQi_1', val)}
        scroll={{ x: 1400 }}
        resizable
        columnWidth={150}
        autoRequest={false}
        store={store.$table}
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
        columns={[
          ...columns,
          showActionColumn && {
            title: '是否报送',
            width: 100,
            fixed: 'right',
            dataIndex: 'reportFlag',
            editable: false,
            render: (value, record) => {
              const newValue = _.isObject(value) ? value.value : value
              return (
                <Switch
                  disabled={!canEdit}
                  onChange={(checked) => store.onReportFlagChange(checked, record)}
                  checked={newValue === 1}
                ></Switch>
              )
            },
          },
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
      <DeleteModal store={store} />
    </div>
  )
}

export default observer(Index)
