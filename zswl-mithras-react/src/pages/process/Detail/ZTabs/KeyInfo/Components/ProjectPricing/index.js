import { observer } from '@zswl/admin'
import EditDescription from '@/components/Table/EditDescription'
import { AmountColumn, AmountFormatter } from '@/components/Format'
import { useEffect, useState } from 'react'
import { compareDetail } from '@/utils'
import FormListItem from '@/pages/project/Components/FormListItem'
import { useFlowData } from '@/utils/processFlow'
import Api from './api'
import { bizRender } from '../ContractApplication'

const detailMap = {
  ZL: 'leasePriceDetailRSP',
  BL: 'factoringPriceDetailRSP',
  ZZ: 'leasePriceDetailRSP',
  ZR: 'aocPriceDetailRSP',
}

const columns = [
  { title: '客户名称', dataIndex: 'clientName' },
  AmountColumn({
    title: '存量风险敞口（元）',
    dataIndex: 'residualRiskAmount',
    render: (val, record) => {
      const { clientId, lesseeInfo } = record
      const value = lesseeInfo?.find(({ clientId: id }) => id === clientId)?.stockRiskExposure ?? 0
      return <AmountFormatter value={value} />
    },
  }),
  {
    title: '担保人',
    dataIndex: 'guaranteeInfo',
    render: (val) => <FormListItem.Detail values={val} />,
    span: 2,
  },
  {
    title: '业务类型',
    dataIndex: 'bizType',
    matchOption: 'projEstablishBizType',
    render: bizRender,
  },
  // MatchOptionColumn({ title: '业务类型', dataIndex: 'bizType', matchOption: 'projEstablishBizType' }),
  AmountColumn({ title: '申报授信金额（元）', dataIndex: 'applyCreditAmount' }),
  { title: '租赁期限(月）', dataIndex: 'leaseMonthCount' },
  AmountColumn({ title: '租赁利率（%）', dataIndex: 'leaseRatePercent' }),
  {
    title: 'IRR（%）',
    dataIndex: 'irrPercent',
    render: (val) => <AmountFormatter value={val} />,
  },
  { title: '项目主办', dataIndex: 'projSponsorUserName' },
  { title: '业务部门', dataIndex: 'bizDeptName' },
]

const Index = () => {
  const { detailData } = useFlowData()
  const { businessKey: id, businessVersion, processInstanceId } = detailData
  const [moduleData, setModuleData] = useState({})

  const getDetail = async () => {
    const baseInfo = await Api.reviewBaseInfoCompare({ id, processInstanceId, businessVersion })
    const { bizType } = baseInfo
    const res = await Api.reviewQSCompare({ projectId: id, processInstanceId, businessVersion })
    const priceDetail = res[detailMap[bizType?.value ?? bizType]] ?? {}
    const data = { ...priceDetail, ...baseInfo }
    setModuleData(compareDetail(data).newDetail)
  }
  useEffect(() => {
    getDetail()
  }, [JSON.stringify(detailData)])

  return <EditDescription columns={columns} title={''} detail={moduleData} canEdit={false} />
}

export default observer(Index)
