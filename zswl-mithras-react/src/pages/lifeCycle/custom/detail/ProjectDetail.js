import customCycleApi from '@/api/lifeCycle/customCycleApi'
import { AmountColumn, MatchOptionColumn } from '@/components/Format'
import { observer } from '@zswl/admin'
import { Descriptions, Table, TableStore } from '@zswl/components'
import { Card } from 'antd'
import { useMemo } from 'react'
import { saveServer } from '@/utils'

const labelStyle = {
  // color: 'red',
  background: '#F5F6FA',
  width: 170,
}
const initFormat = 10000 * 10000
const columns = [
  {
    title: '项目名称',
    dataIndex: 'name',
    actions({ projectName: name, projectId: id, projectId: establishId, projReviewId: reviewId }) {
      // return [{ name, to: `/lifeCycle/projectLifeCycle/detail/${id}?dataType=${dataType}` }]
      return [
        {
          name,
          to: `/lifeCycle/projectLifeCycle/detail/${id}?establishId=${establishId}&reviewId=${reviewId}`,
        },
      ]
    },
  },
  AmountColumn({
    title: '申报授信金额（万元）',
    dataIndex: 'applyCreditAmount',
    initFormat,
  }),
  AmountColumn({
    title: '合同金额（万元）',
    dataIndex: 'contractAmount',
    initFormat,
  }),
  MatchOptionColumn({
    title: '业务类型',
    dataIndex: 'bizType',
    matchOption: 'projEstablishBizType',
  }),
  MatchOptionColumn({ title: '项目阶段', dataIndex: 'projStage', matchOption: 'projStageEnum' }),
]
const infoColumns = [
  AmountColumn({
    title: '授信金额（万元）',
    dataIndex: 'applyCreditAmount',
  }),
  AmountColumn({
    title: '剩余本金（万元）',
    dataIndex: 'lastPrincipal',
  }),
  AmountColumn({
    title: '剩余风险敞口（万元）',
    dataIndex: 'stockRiskExposure',
  }),
]
const Detail = ({ id, detail }) => {
  const table = useMemo(() => {
    return new TableStore({
      request: (params) => {
        return customCycleApi.postLifecycleProjectlist({ ...params, clientId: id })
      },
    })
  }, [])
  return (
    <Card title="项目详情">
      <Descriptions
        column={3}
        bordered
        dataSource={detail}
        items={infoColumns}
        style={{ padding: '6px 0' }}
        labelStyle={labelStyle}
      />

      <Table         columnsFilter={'custom_detail_ProjectDetail'}
        onFilter={(key,val) => saveServer('custom_detail_ProjectDetail',val)} columns={columns} store={table} />
    </Card>
  )
}

export default observer(Detail)
