import { observer } from '@zswl/admin'
import { EditDescription } from '@/components/Table'
import { AmountColumn, AmountFormatter } from '@/components/Format'
import { compareDetail, getEnumFlatObjByType } from '@/utils'
import { useEffect, useState } from 'react'
import { ContractBizTypePriceDetailMap as bizTypePriceDetailMap } from '@/components/Contract/ConfigEntries'
import { useFlowData } from '@/utils/domains/process/ProcessFlowContext'
import Api from '@/api/process/detail/keyInfoSnapshotApi'

export const bizRender = (val, record) => {
  const {
    bizType,
    factoringType,
    zrType,
    zrTypes,
    leaseType,
    leaseTypes,
    bizTypeCode,
    leaseTypeCode,
  } = record

  const bizTypeVal = bizType?.value ?? bizType ?? bizTypeCode
  const bizName = getEnumFlatObjByType('projEstablishBizType')[bizTypeVal]
  const lesseeInfo =
    leaseTypes?.map((v) => getEnumFlatObjByType('leaseType')[v]).join('、') ??
    getEnumFlatObjByType('leaseType')[leaseType] ??
    getEnumFlatObjByType('leaseType')[leaseTypeCode]
  const factoringName = getEnumFlatObjByType('factoringType')[factoringType?.value ?? factoringType]
  let zrName =
    zrTypes
      ?.map((item) => {
        return getEnumFlatObjByType('zrType')[item]
      })
      .join('、') ?? getEnumFlatObjByType('zrType')[zrType?.value ?? zrType]

  if (bizTypeVal === 'ZL') return `${bizName}-${lesseeInfo}`
  if (bizTypeVal === 'BL') return `${bizName}-${factoringName}`
  if (bizTypeVal === 'ZR') return `${bizName}-${zrName}`
}

const Index = ({}) => {
  const { detailData } = useFlowData()
  const { mainModule, businessKey: id, businessVersion, modelKey } = detailData
  const [moduleData, setModuleData] = useState({})

  const isEarly = modelKey === 'ContractEarlySettleFlow'
  // 合同调整
  const getDetail = async () => {
    const params = {
      contractId: id,
      moduleType: mainModule,
      businessVersion,
    }
    const detail = await Api.getContractDetail({ id })
    const { bizType } = detail
    let earlyDetail = {}
    const res = await Api.getContractQSDetailCompare(params)
    const contractDetail = res[bizTypePriceDetailMap[bizType]] ?? {}
    if (isEarly)
      earlyDetail = await Api.getLatestPlanDetail({
        contractId: id,
        businessVersion,
        planType: 'SETTLE_IN_ADVANCE',
        needReal: 0,
      })
    const data = { ...detail, ...compareDetail(contractDetail).newDetail, ...earlyDetail }
    setModuleData(data)
  }

  const columns = [
    { title: '客户名称', dataIndex: 'clientName' },
    AmountColumn({ title: '项目授信总额（元）', dataIndex: 'projCreditAmount' }),
    {
      title: '业务类型',
      dataIndex: 'bizType',
      matchOption: 'projEstablishBizType',
      render: bizRender,
    },
    { title: '合同编号', dataIndex: 'contractCode' },
    AmountColumn({
      title: '合同金额（元）',
      dataIndex: 'applyCreditAmount',
      render: (val, record) => {
        const value = record?.applyCreditAmount ?? record?.contractAmount
        return <AmountFormatter value={value} />
      },
    }),
    {
      title: '租赁期限(月）',
      dataIndex: 'leaseMonthCount',
      render: (val, record) =>
        record?.leaseMonthCount ?? record?.factoringCreditTerm ?? record?.aocCreditTerm ?? '-',
    },
    {
      title: '合同利率（%）',
      dataIndex: 'leaseRate',
      render: (val, record) => {
        const { lprPercent, lprAddPercent } = record
        const value = (lprPercent ?? 0) + (lprAddPercent ?? 0)
        return <AmountFormatter value={value} />
      },
    },
    {
      title: 'IRR（%）',
      dataIndex: 'irrPercent',
      render: (val) => <AmountFormatter value={val} />,
    },
    AmountColumn({
      title: '手续费率（%）',
      dataIndex: 'earnestMoneyRate',
      render: (val, record) => {
        // 手续费率=服务费/合同金额
        const value = (
          ((record?.consultingFee ?? 0) /
            (record?.applyCreditAmount ?? record.contractAmount ?? 0)) *
          100
        ).toFixed(1)
        return <AmountFormatter value={value} initFormat={1} />
      },
    }),
    AmountColumn({
      title: '保证金率（%）',
      dataIndex: 'consultingFeeRate',
      render: (val, record) => {
        // 保证金率=保证金/合同金额
        const value = (
          ((record?.earnestMoney ?? 0) /
            (record?.applyCreditAmount ?? record.contractAmount ?? 0)) *
          100
        ).toFixed(1)
        return <AmountFormatter value={value} initFormat={1} />
      },
    }),
    { title: '项目主办', dataIndex: 'projSponsorUserName' },
    { title: '业务部门', dataIndex: 'bizDeptName' },
    isEarly && { title: '原到期日', dataIndex: 'originalDeadline' },
    isEarly && { title: '申请结清日', dataIndex: 'applySettleDate' },
    isEarly && {
      title: '提前结清说明',
      dataIndex: 'settleRemark',
      span: 2,
      render: (val) => val || '-',
    },
  ].filter(Boolean)

  useEffect(() => {
    getDetail()
  }, [JSON.stringify(detailData)])

  return <EditDescription detail={{ ...moduleData }} columns={columns} title={''} canEdit={false} />
}

export default observer(Index)
