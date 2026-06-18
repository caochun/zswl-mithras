import { useMemo, useEffect } from 'react'
import { Table, App } from '@zswl/components'
import { observer, getSessionStorage } from '@zswl/admin'
import { rules, getFormColumns } from '@/utils'
import { Switch } from 'antd'
import { DateColumn, InputColumn, MatchOptionColumn, AmountColumn } from '@/components/Format'
import { COMMON_COLUMNS } from '../../../CreditTableColumns'
import { CREATETABLE_PARAMS, AmountFormat } from '../../../CreditTableConfig'
import Store from './store'
import EditButton from '../../Components/EditButton'
import _ from 'lodash'
import statusRender from '../../../CreditTableStatusRender'
import { saveServer } from '@/utils'

function Index(props = {}) {
  const { canEdit, showActionColumn, componentKey, curTab, baseStore, showSearch } = props
  const formColumns = getFormColumns(
    COMMON_COLUMNS,
    ['编号', '客户名称', showActionColumn && '是否报送', showActionColumn && '审批状态'].filter(
      Boolean
    )
  )
  const store = useMemo(() => {
    return new Store({ baseParams: props.baseParams, baseStore })
  }, [props])

  useEffect(() => {
    if (componentKey === curTab) {
      store.$table.setParams(getSessionStorage(CREATETABLE_PARAMS))
      store.$table.search()
    }
  }, [componentKey, curTab])

  const columns = [
    InputColumn({
      title: '编号',
      dataIndex: 'paymentApplyCode',
      width: 280,
      editable: false,
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
      width: 260,
      dataIndex: 'clientName',
    }),
    MatchOptionColumn({
      title: '业务类型',
      width: 160,
      dataIndex: 'bizType',
      matchOption: 'crAccountBizType',
      editable: true,
      requiredMark: true,
    }),
    MatchOptionColumn({
      title: '租金计算方式',
      width: 200,
      dataIndex: 'rentalCalcType',
      matchOption: 'crRepayCalcType',
      requiredMark: true,
    }),
    MatchOptionColumn({
      title: '还款频率',
      width: 160,
      dataIndex: 'repayRate',
      matchOption: 'crAccountRepayRate',
      requiredMark: true,
    }),
    AmountColumn({
      title: '借款金额(元)',
      dataIndex: 'paymentAmount',
      editable: true,
      requiredMark: true,
      wrapItemProps: {
        required: true,
      },
    }),
    AmountColumn({
      title: '保证金(元)',
      dataIndex: 'earnestMoney',
      requiredMark: true,
      wrapItemProps: {
        required: true,
      },
      editable: true,
    }),
    InputColumn({
      title: '借款期限(月)',
      width: 160,
      dataIndex: 'projLeaseMonthCount',
      requiredMark: true,
      editable: {
        rules: [{ required: true, message: '请输入借款期限' }],
      },
    }),

    DateColumn({
      title: '放款日期',
      width: 160,
      dataIndex: 'lendingDate',
      editable: true,
      requiredMark: true,
      rules: [{ required: true, message: '请选择放款日期' }],
    }),
    DateColumn({ title: '结清日期', width: 160, dataIndex: 'closedDate', editable: true }),

    DateColumn({ title: '到期日期', width: 160, dataIndex: 'expirationDate', editable: false }),
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
        columnsFilter={'Tab_ZhangHu_1'}
        onFilter={(key, val) => saveServer('Tab_ZhangHu_1', val)}
        autoRequest={false}
        store={store.$table}
        resizable
        columnWidth={180}
        editable={false}
        searchbar={
          showSearch
            ? {
                limit: 2,
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
