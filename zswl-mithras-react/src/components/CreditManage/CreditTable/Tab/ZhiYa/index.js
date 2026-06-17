import { useMemo, useEffect } from 'react'
import { Table, App } from '@zswl/components'
import { observer, getSessionStorage } from '@zswl/admin'
import { getFormColumns, hasValue } from '@/utils'
import { AmountColumn, AmountFormat, InputColumn, MatchOptionColumn } from '@/components/Format'
import { COMMON_COLUMNS } from '@/components/CreditManage/CreditTableColumns'
import { CREATETABLE_PARAMS } from '@/components/CreditManage/CreditTableConfig'
import Store from './store'
import { Switch } from 'antd'
import _ from 'lodash'
import { saveServer } from '@/utils'

function Index(props = {}) {
  const { componentKey, curTab, showActionColumn, showSearch, channel, canEdit } = props
  const formColumns = getFormColumns(
    COMMON_COLUMNS,
    [
      '编号',
      {
        title: '客户名称',
        rename: '出质人名称',
      },
      showActionColumn && '审批状态',
    ].filter(Boolean)
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
        columnsFilter={'Tab_ZhiYa_1'}
        onFilter={(key, val) => saveServer('Tab_ZhiYa_1', val)}
        resizable
        columnWidth={160}
        autoRequest={false}
        store={store.$table}
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
          AmountColumn({
            title: '借据本金(元)',
            dataIndex: 'applyPaymentAmount',
          }),
          InputColumn({
            title: '质押合同编号',
            width: 320,
            dataIndex: 'pledgeContractCode',
          }),
          InputColumn({
            title: '质押合同编号(简)',
            dataIndex: 'pledgeContractCode2',
            width: 220,
          }),
          MatchOptionColumn({
            title: '出质人类型',
            dataIndex: 'pledgeType',
            matchOption: 'crClientType',
          }),
          InputColumn({
            title: '出质人名称',
            width: 180,
            dataIndex: 'pledgeName',
          }),
          InputColumn({
            title: '出质人身份标识类型',
            width: 180,
            dataIndex: 'pledgeIdType',
            render: (value, record) => {
              const pledgeType = record.pledgeType?.value
              return hasValue(pledgeType)
                ? pledgeType == 1
                  ? App.matchOption('certType', pledgeType).label
                  : '统一社会信用代码'
                : ''
            },
          }),
          InputColumn({
            title: '出质人身份标识号码',
            width: 250,
            dataIndex: 'pledgeId',
          }),
          MatchOptionColumn({
            title: '最高额担保标识',
            width: 140,
            dataIndex: 'maxFlag',
            matchOption: 'yesOrNo',
          }),
          InputColumn({
            title: '序号',
            width: 90,
            dataIndex: 'sequence',
          }),
          MatchOptionColumn({
            title: '质押物种类',
            width: 140,
            dataIndex: 'type',
            matchOption: 'crPledgeDataType',
          }),
          AmountColumn({
            title: '质物价值(元)',
            width: 140,
            dataIndex: 'assessedValue',
          }),
          showActionColumn &&
            MatchOptionColumn({
              title: '审批状态',
              width: 180,
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
