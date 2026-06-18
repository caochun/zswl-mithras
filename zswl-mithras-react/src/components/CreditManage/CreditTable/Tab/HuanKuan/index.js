import { useMemo, useEffect } from 'react'
import { Table, App } from '@zswl/components'
import { observer, getSessionStorage } from '@zswl/admin'
import { getFormColumns } from '@/utils'
import {
  InputNumberEditable,
  AmountFormat,
  InputColumn,
  AmountColumn,
  MatchOptionColumn,
} from '@/components/Format'
import { COMMON_COLUMNS } from '../../../CreditTableColumns'
import { CREATETABLE_PARAMS } from '../../../CreditTableConfig'
import Store from './store'
import EditButton from '../../Components/EditButton'
import statusRender from '../../../CreditTableStatusRender'
import { saveServer } from '@/utils'

function Index(props = {}) {
  const { canEdit, showActionColumn, componentKey, curTab, baseStore, showSearch, channel } = props
  const formColumns = getFormColumns(
    COMMON_COLUMNS,
    ['编号', showActionColumn && '审批状态'].filter(Boolean)
  )
  const store = useMemo(() => {
    return new Store({ baseParams: props.baseParams, baseStore })
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
    InputColumn({ title: '期项', width: 90, dataIndex: 'phase' }),
    InputColumn({ title: '应收日期', dataIndex: 'cashFlowDate' }),
    AmountColumn({
      title: '宽限期(天)',
      dataIndex: 'gracePeriod',
      requiredMark: true,
      wrapItemProps: {
        required: true,
      },
      initFormat: 1,
      editable: true,
    }),
    AmountColumn({ title: '应收租金(元)', dataIndex: 'rent', editable: false }),
    AmountColumn({ title: '应收本金(元)', dataIndex: 'principal', editable: false }),
    InputColumn({ title: '收款日期', dataIndex: 'payDate' }),
    AmountColumn({ title: '收款金额(元)', dataIndex: 'collectionAmount', editable: false }),
    AmountColumn({ title: '实收本金(元)', dataIndex: 'collectionPrincipal', editable: false }),
    showActionColumn &&
      MatchOptionColumn({
        title: '审批状态',
        width: 120,
        dataIndex: 'approvalStatus',
        matchOption: 'crApprovalStatus',
        editable: false,
      }),
  ]
  return (
    <div>
      <Table
        columnsFilter={'Tab_HuanKuan_1'}
        onFilter={(key, val) => saveServer('Tab_HuanKuan_1', val)}
        resizable
        columnWidth={140}
        rowKey={'idKey'}
        autoRequest={false}
        store={store.$table}
        editable={false}
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
            title: '操作',
            fixed: 'right',
            actions(record, rowIndex) {
              return [
                <EditButton
                  record={record}
                  disabled={!canEdit}
                  columns={columns}
                  saveData={store.confirmEdit}
                />,
              ]
            },
          },
        ].filter(Boolean)}
      />
    </div>
  )
}

export default observer(Index)
