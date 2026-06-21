import { useMemo, useEffect } from 'react'
import { Table, App } from '@zswl/components'
import { observer, getSessionStorage } from '@zswl/admin'
import { getFormColumns, hasValue } from '@/utils'
import { AmountColumn, AmountFormat, InputColumn, MatchOptionColumn } from '@/components/Format'
import { COMMON_COLUMNS } from '../../../CreditTableColumns'
import { Switch, Tooltip } from 'antd'
import { CREATETABLE_PARAMS } from '../../../CreditTableConfig/CreditTableConfig'
import styles from '../index.less'
import Store from './store'
import _ from 'lodash'
import { saveServer } from '@/utils'

function CreditTableDiYa(props = {}) {
  const { componentKey, curTab, showActionColumn, showSearch, channel, canEdit } = props
  const formColumns = getFormColumns(
    COMMON_COLUMNS,
    [
      '编号',
      {
        title: '客户名称',
        rename: '抵押人名称',
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
        columnsFilter={'Tab_DiYa_1'}
        onFilter={(key, val) => saveServer('Tab_DiYa_1', val)}
        resizable
        columnWidth={160}
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
            width: 140,
          }),
          InputColumn({
            title: '抵押合同编号',
            width: 320,
            dataIndex: 'mortgageContractCode',
          }),
          InputColumn({
            title: '抵押合同编号(简)',
            width: 200,
            dataIndex: 'mortgageContractCode2',
          }),
          MatchOptionColumn({
            title: '抵押人类型',
            width: 140,
            dataIndex: 'mortgageType',
            matchOption: 'crClientType',
          }),
          InputColumn({
            title: '抵押人名称',
            dataIndex: 'mortgageName',
            width: 280,
          }),
          InputColumn({
            title: '抵押人身份标识类型',
            width: 160,
            dataIndex: 'mortgageIdType',
            render: (value, record) => {
              const mortgageIdType = record.mortgageIdType?.value
              return hasValue(mortgageIdType)
                ? mortgageIdType == 1
                  ? App.matchOption('certType', mortgageIdType).label
                  : '统一社会信用代码'
                : ''
            },
          }),
          InputColumn({
            title: '抵押人身份标识号码',
            width: 200,
            dataIndex: 'mortgageId',
          }),
          MatchOptionColumn({
            title: '最高额担保标识',
            width: 160,
            dataIndex: 'maxFlag',
            matchOption: 'yesOrNo',
          }),
          MatchOptionColumn({
            title: '评估机构类型',
            width: 160,
            dataIndex: 'appraisalCompanyType',
            matchOption: 'crMortgageAppraisalCompanyType',
          }),
          InputColumn({
            title: '评估日期',
            width: 160,
            dataIndex: 'assessedDate',
          }),
          InputColumn({
            title: '抵押物描述',
            width: 160,
            dataIndex: 'mortgageDescribe',
            render: (value) => {
              const newValue = value?.value
              return (
                <Tooltip title={newValue}>
                  <span className={styles.ellipsis_3}>{newValue || '-'}</span>
                </Tooltip>
              )
            },
          }),
          InputColumn({
            title: '序号',
            width: 90,
            dataIndex: 'sequence',
          }),
          MatchOptionColumn({
            title: '抵押物种类',
            width: 220,
            dataIndex: 'type',
            matchOption: 'crMortgageDataType',
          }),
          MatchOptionColumn({
            title: '抵押物识别号类型',
            width: 200,
            dataIndex: 'modelType',
            matchOption: 'crMortgageModelType',
          }),
          InputColumn({
            title: '抵押物唯一识别号',
            width: 250,
            dataIndex: 'model',
          }),
          AmountColumn({
            title: '评估价值(元)',
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

export default observer(CreditTableDiYa)
