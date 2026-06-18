import { observer } from '@zswl/admin'
import { useState, useEffect } from 'react'
import { AmountColumn, AmountFormatter, InputColumn } from '@/components/Format'
import { EditDescription } from '@/components/Table'
import { useFlowData } from '@/process/ProcessFlowContext'
import Api from './api'
import { bizRender } from '../ContractApplication'

const columns = [
  { title: '客户名称', dataIndex: 'clientName' },
  AmountColumn({ title: '项目剩余未付金额（元）', dataIndex: 'projectRemainingUnpaidAmount' }),
  {
    title: '业务类型',
    dataIndex: 'bizType',
    matchOption: 'projEstablishBizType',
    render: bizRender,
  },
  AmountColumn({ title: '合同金额（元）', dataIndex: 'applyCreditAmount' }),
  InputColumn({ title: '合同编号', dataIndex: 'contractCode' }),
  AmountColumn({
    title: '合同剩余未付金额（元）',
    dataIndex: 'creditAmount',
    render: (val, record) => {
      const { applyCreditAmount, amountPaid } = record
      const value = (applyCreditAmount ?? 0) - (amountPaid ?? 0)
      return <AmountFormatter value={value} initFormat={10000} />
    },
  }),
  AmountColumn({ title: '合同IRR（%）', dataIndex: 'contractEstimateIrr', suffix: '%' }),
  AmountColumn({
    title: '本次申请付款金额（元）',
    dataIndex: 'applyPaymentAmount',
    isHighlight: true,
  }),
  { title: '项目主办', dataIndex: 'projSponsorUserName' },
  { title: '业务部门', dataIndex: 'bizDeptName' },
]

const Index = ({}) => {
  const { detailData } = useFlowData()
  const { businessKey: id } = detailData
  const [moduleData, setModuleData] = useState({})

  const getDetail = async () => {
    const res = await Api.getDetail({ id })
    setModuleData(res)
  }

  useEffect(() => {
    getDetail()
  }, [JSON.stringify(detailData)])

  return (
    <EditDescription
      columns={columns}
      title={''}
      detail={moduleData}
      canEdit={false}
      labelStyle={{ width: 200 }}
    />
  )
}

export default observer(Index)
