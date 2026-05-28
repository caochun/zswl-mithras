import { observer } from '@zswl/admin'
import { Descriptions, Page, Table, TableStore } from '@zswl/components'
import store from './store'
import _ from 'lodash'
import { AmountColumn, AmountFormat } from '@/components/Format'
import { useMemo } from 'react'
import { getKeyOptionsLabelMap, hasValue } from '@/utils'
import useGetAllSelect from '../useGetAllSelect'
import { saveServer } from '@/utils'

const labelStyle = {
  background: '#F5F6FA',
}

const valNeedFormatUnit = ['WAN', 'YUAN', 'PERCENT', 'YI']

const detailColumns = [
  {
    title: '客户名称',
    dataIndex: 'clientName',
    actions: ({ clientName: name, clientId, clientType, domesticOrAbroad }) => [
      {
        name,
        to: `/customer/maintain/detail/${clientId}?clientType=${clientType}&domesticOrAbroad=${domesticOrAbroad}&flag=info&typeId=create`,
      },
    ],
  },
  {
    title: '合同编号',
    dataIndex: 'contractCode',
    actions: ({ contractCode: name, contractId }) => [
      { name, to: `/contract/list/detail/${contractId}` },
    ],
  },
  AmountColumn({ title: '金额', dataIndex: 'amount' }),
]

function Index({ params, query }) {
  const { date } = query
  const detail = store.page.getData()
  const { contractDetailList } = detail
  const { allSelect } = useGetAllSelect()
  const detailTable = useMemo(
    () =>
      new TableStore({
        request: () => contractDetailList,
      }),
    [contractDetailList]
  )
  const columns = [
    { title: '指标名称', dataIndex: 'metricName', width: 200 },
    { title: '数据时点', dataIndex: 'dataTime', width: 200 },
    { title: '一级分类', dataIndex: 'oneLevelType', width: 120 },
    { title: '二级分类', dataIndex: 'twoLevelType', width: 160 },
    { title: '指标大类', dataIndex: 'metricFirstType', width: 120 },
    { title: '指标小类', dataIndex: 'metricSecondType', width: 180 },
    {
      title: '当期值(系统计算)',
      dataIndex: 'metricValue',
      align: 'right',
      render: (value, { unit }) => {
        return valNeedFormatUnit.includes(unit) && !window.isNaN(value) ? (
          AmountFormat({ value })
        ) : hasValue(value) ? (
          <Tooltip title={value}>{value}</Tooltip>
        ) : (
          '-'
        )
      },
    },
    {
      title: '当期值(人工修正)',
      dataIndex: 'metricValueAdjusted',
      align: 'right',
      render: (value, { unit }) => {
        return valNeedFormatUnit.includes(unit) && !window.isNaN(value) ? (
          AmountFormat({ value })
        ) : hasValue(value) ? (
          <Tooltip title={value} placement="left">
            {value}
          </Tooltip>
        ) : (
          '-'
        )
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      render: (v) => {
        return getKeyOptionsLabelMap('riskMetricStatus', allSelect)[v] || '-'
      },
    },
    {
      title: '单位',
      dataIndex: 'unit',
      render: (v) => {
        return getKeyOptionsLabelMap('riskMetricUnit', allSelect)[v] || '-'
      },
    },
    {
      title: '报送频率',
      dataIndex: 'frequency',
      render: (v) => {
        return getKeyOptionsLabelMap('riskMetricFrequency', allSelect)[v] || '-'
      },
    },
  ]

  return (
    <Page params={{ ...params, date }} store={store.page}>
      <Descriptions
        title={'指标信息'}
        bordered
        column={2}
        dataSource={detail}
        items={columns}
        className={'z-description'}
        labelStyle={labelStyle}
      />
      {!_.isEmpty(contractDetailList) && (
        <>
          <h3 style={{ margin: '20px 0' }}>指标计算详情</h3>
          <Table         columnsFilter={'cloudMetricValue_detail_idjs'}
        onFilter={(key,val) => saveServer('cloudMetricValue_detail_idjs',val)} dataSource={contractDetailList} columns={detailColumns} store={detailTable} />
        </>
      )}
    </Page>
  )
}

export default observer(Index)
