import { useMemo, useEffect } from 'react'
import { Table, App } from '@zswl/components'
import { observer, getSessionStorage } from '@zswl/admin'
import { rules, getFormColumns } from '@/utils'
import { InputColumn, MatchOptionColumn, DateColumn, AmountColumn } from '@/components/Format'
import { Switch } from 'antd'
import { CREATETABLE_PARAMS, AmountFormat } from '../../../CreditTableConfig/CreditTableConfig'
import { COMMON_COLUMNS } from '../../../CreditTableColumns'
import Store from './store'
import EditButton from '../../Components/EditButton'
import statusRender from '../../../CreditTableStatusRender'
import _ from 'lodash'
import { saveServer } from '@/utils'

function CreditTableJiaoYi(props = {}) {
  const { canEdit, showActionColumn, componentKey, curTab, showSearch, channel } = props
  const formColumns = getFormColumns(
    COMMON_COLUMNS,
    ['编号', showActionColumn && '是否报送', showActionColumn && '审批状态'].filter(Boolean)
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
      title: '交易类型',
      dataIndex: 'tradeType',
      matchOption: 'crSpecialTradeType',
    }),
    DateColumn({
      title: '交易日期',
      dataIndex: 'tradeDate',
      editable: true,
      requiredMark: true,
      rules: [{ required: true, message: '请选择交易日期' }],
    }),
    AmountColumn({
      title: '交易金额(元)',
      dataIndex: 'tradeAmount',
      requiredMark: true,
      wrapItemProps: {
        required: true,
      },
      editable: true,
    }),
    AmountColumn({
      title: '到期日变更月数(月)',
      dataIndex: 'changeMonthCount',
      requiredMark: true,
      wrapItemProps: {
        required: true,
      },
      initFormat: 1,
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
        columnsFilter={'Tab_JiaoYi_1'}
        onFilter={(key, val) => saveServer('Tab_JiaoYi_1', val)}
        columnWidth={180}
        resizable
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
          // showActionColumn && {
          //   title: '是否报送',
          //   width: 100,
          //   fixed: 'right',
          //   dataIndex: 'reportFlag',
          //   render: (value, record) => {
          //     const newValue = _.isObject(value) ? value.value : value
          //     return (
          //       <Switch
          //         disabled={!canEdit}
          //         onChange={(checked) => store.onReportFlagChange(checked, record)}
          //         checked={newValue === 1}
          //       ></Switch>
          //     )
          //   },
          // },
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

export default observer(CreditTableJiaoYi)
