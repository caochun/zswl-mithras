import { useMemo, useEffect } from 'react'
import { Table, App } from '@zswl/components'
import { observer, getSessionStorage } from '@zswl/admin'
import { Switch, Tooltip } from 'antd'
import { getFormColumns } from '@/utils'
import { COMMON_COLUMNS } from '../../../CreditTableColumns'
import { CREATETABLE_PARAMS, AmountFormat } from '../../../CreditTableConfig/CreditTableConfig'
import styles from '../index.less'
import Store from './store'
import { AmountColumn, InputColumn, MatchOptionColumn } from '@/components/Format'
import _ from 'lodash'
import { saveServer } from '@/utils'

function CreditTableKuHu(props = {}) {
  const { componentKey, curTab, showActionColumn, showSearch, canEdit } = props
  const formColumns = getFormColumns(
    COMMON_COLUMNS,
    ['客户名称', showActionColumn && '审批状态'].filter(Boolean)
  )
  const store = useMemo(() => {
    return new Store({ baseParams: props.baseParams })
  }, [props])

  useEffect(() => {
    if (componentKey === curTab) {
      store.$table.search()
    }
  }, [componentKey, curTab])

  return (
    <div>
      <Table
        columnsFilter={'Tab_KuHu_1'}
        onFilter={(key, val) => saveServer('Tab_KuHu_1', val)}
        resizable
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
            title: '客户编号',
            dataIndex: 'clientCode',
            width: 160,
            fixed: 'left',
          }),
          InputColumn({
            title: '客户名称',
            dataIndex: 'clientName',
            width: 280,
          }),
          InputColumn({
            title: '中征码',
            dataIndex: 'zhongZhengCode',
            width: 200,
          }),
          MatchOptionColumn({
            title: '存续状态',
            dataIndex: 'continuousStatus',
            matchOption: 'continuousStatus',
          }),
          MatchOptionColumn({
            title: '组织机构类型',
            dataIndex: 'orgType',
            matchOption: 'orgType',
          }),
          InputColumn({
            title: '注册地址',
            dataIndex: 'registerAddress',
            width: 250,
          }),
          InputColumn({
            title: '行政区划',
            dataIndex: 'regionCode',
          }),
          InputColumn({
            title: '成立日期',
            dataIndex: 'establishDate',
          }),
          InputColumn({
            title: '营业许可到期日',
            dataIndex: 'bizLicenseEndDate',
          }),
          InputColumn({
            title: '业务范围',
            dataIndex: 'bizScope',
            render: (value) => {
              const newValue = typeof value === 'object' ? value?.value : value
              return (
                <Tooltip title={newValue}>
                  <span className={styles.ellipsis_3}>{newValue}</span>
                </Tooltip>
              )
            },
          }),
          InputColumn({
            title: '行业分类',
            dataIndex: 'industryTypeName',
          }),
          MatchOptionColumn({
            title: '经济类型',
            dataIndex: 'economyType',
            matchOption: 'economyType',
          }),
          MatchOptionColumn({
            title: '企业规模',
            dataIndex: 'orgScale',
            matchOption: 'orgScaleType',
          }),
          MatchOptionColumn({
            title: '注册资本币种',
            dataIndex: 'registerCurrencyType',
            matchOption: 'currencyType',
          }),
          AmountColumn({
            title: '注册资本(元)',
            dataIndex: 'registerCapital',
          }),
          InputColumn({
            title: '法人代表',
            dataIndex: 'corpRepresent',
          }),
          MatchOptionColumn({
            title: '法人证件类型',
            dataIndex: 'corpCertType',
            matchOption: 'certType',
          }),
          InputColumn({
            title: '法人证件号码',
            dataIndex: 'corpCertCode',
            width: 200,
          }),
          InputColumn({
            title: '数据更新日期',
            dataIndex: 'effectDate',
            width: 200,
          }),
          showActionColumn &&
            MatchOptionColumn({
              title: '审批状态',
              dataIndex: 'approvalStatus',
              width: 180,
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

export default observer(CreditTableKuHu)
