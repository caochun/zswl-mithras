import { FileTable } from '@/components'
import { useMemo } from 'react'
import Api from '@/api/financial/fundApi'
;[
  {
    label: '银行承兑汇票',
    value: 'BANK_ACCEPTANCE',
    children: null,
  },
  {
    label: '信用证',
    value: 'LETTER_OF_CREDIT',
    children: null,
  },
  {
    label: '保理融资',
    value: 'FACTORING_FINANCING',
    children: null,
  },
  {
    label: '流动资金贷款',
    value: 'WORKING_CAPITAL_LOAN',
    children: null,
  },
  {
    label: '项目贷款',
    value: 'PROJECT_LOAN',
    children: null,
  },
  {
    label: '商业承兑汇票',
    value: 'COMMERCE_ACCEPTANCE',
    children: null,
  },
  {
    label: '银团',
    value: 'SYNDICATIONS',
    children: null,
  },
  {
    label: '其他',
    value: 'OTHER',
    children: null,
  },
]
const ENUM_TYPE_MAP = {
  BANK_ACCEPTANCE: 'fundFinancingMaterialsEnumBank',
  LETTER_OF_CREDIT: 'fundFinancingMaterialsEnumLetter',
  FACTORING_FINANCING: 'fundFinancingMaterialsEnumFactoring',
  WORKING_CAPITAL_LOAN: 'fundFinancingMaterialsEnumWorking',
  PROJECT_LOAN: 'fundFinancingMaterialsEnumWorking',
  COMMERCE_ACCEPTANCE: 'fundFinancingMaterialsEnumCommerce',
  SYNDICATIONS: 'fundFinancingMaterialsEnumOther',
  OTHER: 'fundFinancingMaterialsEnumOther',
}
const Index = (props) => {
  const { financingId: mainId, canEdit = true, businessVersion, baseInfoData, processType } = props
  const isRecordFlow = processType === 'FinancingRecordFlow'
  const enumType = ENUM_TYPE_MAP[baseInfoData?.businessType]
  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const params = useMemo(
    () => ({
      mainId,
      moduleType: 'FUND_FINANCING',
      businessVersion,
    }),
    [mainId, businessVersion]
  )
  const downloadApi = async ({ id }) => {
    return Api.download({ fileId: id })
  }
  const batchDownloadApi = async ({ fileIds }) => {
    return Api.batchDownload({
      id: mainId,
      moduleCode: 'FUND_FINANCING',
      fileIds,
    })
  }
  return (
    <FileTable
      enumType={'fundFinancingMaterialsEnumWorking'}
      title={'资料清单'}
      canEdit={canEdit || isRecordFlow}
      batchDownloadApi={batchDownloadApi}
      downloadApi={downloadApi}
      columns={columns}
      canBatchDownload
      params={params}
    />
  )
}

export default Index
