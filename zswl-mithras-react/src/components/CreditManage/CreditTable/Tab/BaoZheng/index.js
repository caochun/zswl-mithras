import { useMemo, useEffect } from 'react'
import { Table, App } from '@zswl/components'
import { observer, getSessionStorage } from '@zswl/admin'
import { getFormColumns, hasValue } from '@/utils'
import { AmountColumn, AmountFormat, InputColumn, MatchOptionColumn } from '@/components/Format'
import { COMMON_COLUMNS } from '../../../CreditTableColumns'
import { CREATETABLE_PARAMS } from '../../../CreditTableConfig/CreditTableConfig'
import Store from './store'
import { Switch } from 'antd'
import _ from 'lodash'
import { saveServer } from '@/utils'

function Index(props = {}) {
  const { componentKey, curTab, showActionColumn, showSearch, channel, canEdit } = props
  const formColumns = getFormColumns(
    COMMON_COLUMNS,
    ['编号', '客户名称', showActionColumn && '审批状态'].filter(Boolean)
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

  return (
    <div>
      <Table
        columnsFilter={'Tab_BaoZheng_1'}
        onFilter={(key, val) => saveServer('Tab_BaoZheng_1', val)}
        editable={false}
        resizable
        scroll={{ x: 2000 }}
        autoRequest={false}
        store={store.$table}
        columnWidth={160}
        searchbar={
          showSearch
            ? {
                labelCol: { span: 6 },
                items: formColumns,
              }
            : null
        }
        columns={[
          InputColumn({
            title: '编号',
            dataIndex: 'paymentApplyCode',
            width: 220,
            fixed: 'left',
          }),
          InputColumn({
            title: '合同编号',
            dataIndex: 'contractCode',
            width: 280,
            editable: false,
          }),
          InputColumn({
            title: '保证合同编号',
            width: 320,
            dataIndex: 'guaranteContractCode',
          }),
          InputColumn({
            title: '保证合同编号(简)',
            width: 200,
            dataIndex: 'guaranteContractCode2',
          }),
          InputColumn({
            title: '客户名称',
            width: 300,
            dataIndex: 'clientName',
          }),
          MatchOptionColumn({
            title: '客户分类',
            width: 140,
            dataIndex: 'clientType',
            matchOption: 'crClientType',
          }),
          {
            title: '身份标识类型',
            dataIndex: 'guarantorIdType',
            width: 160,
            render: (value, record) => {
              const val = value?.value
              const clientType = record.clientType?.value
              return hasValue(clientType)
                ? clientType == 1
                  ? App.matchOption('certType', val).label
                  : '统一社会信用代码'
                : ''
            },
          },
          InputColumn({
            title: '身份标识号码',
            width: 200,
            dataIndex: 'guarantorId',
          }),
          MatchOptionColumn({
            title: '客户类型',
            width: 120,
            dataIndex: 'clientClass',
            matchOption: 'crGuarantorClientClass',
          }),
          AmountColumn({
            title: '还款责任金额(元)',
            width: 160,
            dataIndex: 'repayLiabilityAmount',
          }),
          MatchOptionColumn({
            title: '联保标识',
            width: 160,
            dataIndex: 'jointGuarantorFlag',
            matchOption: 'crGuarantorJointFlag',
          }),
          showActionColumn &&
            MatchOptionColumn({
              title: '审批状态',
              width: 140,
              dataIndex: 'approvalStatus',
              matchOption: 'crApprovalStatus',
            }),
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
        ].filter(Boolean)}
      />
    </div>
  )
}

export default observer(Index)
