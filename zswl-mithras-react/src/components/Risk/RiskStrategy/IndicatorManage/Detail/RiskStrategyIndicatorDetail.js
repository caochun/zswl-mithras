import { EditDescription } from '@/components/Table'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'
import { Descriptions, Page, Table, TableStore } from '@zswl/components'
import ALL_COLUMNS from '../Column'
import store from './store'
import _ from 'lodash'
import { AmountColumn } from '@/components/Format'
import { useMemo } from 'react'
import { saveServer } from '@/utils'

const items = [
  '指标编号',
  '指标类型',
  '指标类别',
  '指标名称',
  '创建时间',
  '更新时间',
  '当前值',
  '计算说明',
]
const columns = getDescColumns(ALL_COLUMNS, items)

const descColumns = getDescColumns(ALL_COLUMNS, ['限额值设定'])
const warningColumns = getDescColumns(ALL_COLUMNS, ['预警监测状态', '预警值'])

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
  AmountColumn({ title: '剩余本金', dataIndex: 'remainingPrincipal' }),
  AmountColumn({ title: '保证金', dataIndex: 'deposit' }),
]
const labelStyle = {
  // color: 'red',
  background: '#F5F6FA',
}
const tableColumns = [
  {
    title: '国标二级行业',
    dataIndex: 'twoLevelIndustryName',
  },
  AmountColumn({
    title: '创新业务行业风险敞口（亿元）',
    dataIndex: 'remaining',
    precision: 4,
  }),
]
function RiskStrategyIndicatorDetail({ params, query }) {
  const { date } = query
  const detail = store.page.getData()
  const { innovativeBizRspList, clientDetailList } = detail
  const detailTable = useMemo(
    () =>
      new TableStore({
        request: () => clientDetailList,
      }),
    [clientDetailList]
  )

  return (
    <Page params={{ ...params, date }} store={store.page}>
      <Descriptions
        title={'指标信息'}
        bordered
        column={3}
        dataSource={detail}
        items={columns}
        extra={[date && <div>快照时间：{date}</div>]}
        className={'z-description'}
        labelStyle={labelStyle}
      />
      {!_.isEmpty(innovativeBizRspList) && (
        <>
          <h3 style={{ margin: '20px 0' }}>创新业务行业风险敞口</h3>
          <Table         columnsFilter={'indicatorManage_detail_idjs_1'}
        onFilter={(key,val) => saveServer('indicatorManage_detail_idjs_1',val)} dataSource={innovativeBizRspList} columns={tableColumns}></Table>
        </>
      )}

      <EditDescription
        title={'限额值设定'}
        detail={detail}
        canEdit={!date}
        columns={descColumns}
        saveData={(data) => store.save(data, 'limitValue')}
        access="riskcontrolstrategymodify"
        style={{ marginTop: 12 }}
      />

      <EditDescription
        title={'预警值设定'}
        detail={detail}
        canEdit={!date}
        columns={warningColumns}
        saveData={(data) => store.save(data, 'earlyWarningValue')}
        access="riskcontrolstrategymodify"
        style={{ marginTop: 12 }}
      />
      {!_.isEmpty(clientDetailList) && (
        <>
          <h3 style={{ margin: '20px 0' }}>指标计算详情</h3>
          <Table columnsFilter={'indicatorManage_detail_idjs_2'}
        onFilter={(key,val) => saveServer('indicatorManage_detail_idjs_2',val)} dataSource={clientDetailList} columns={detailColumns} store={detailTable} />
        </>
      )}
    </Page>
  )
}

export default observer(RiskStrategyIndicatorDetail)
