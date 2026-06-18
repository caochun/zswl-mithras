import { App, ModalStore, PageStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import { message } from 'antd'
import moment from 'moment'
import EclRecordApi from '@/api/budget/provisioning/eclRecordApi'
import eclBusinessApi from '@/api/budget/provisioning/eclBusinessApi'
import ftpInterestChangeApi from '@/api/budget/pricing/ftpInterestChangeApi'
import { validateModal } from '@/utils/modal'

const enum2LabelEnum = (enums) => {
  const isObject = !!enums?.[0]?.label
  const uniqueEnums = [...new Set(enums.map((item) => (isObject ? item?.label : item)))]
  return (uniqueEnums ?? []).map((label) => ({ label, value: label }))
}

class ProvisioningDataAddModalStore {
  constructor({ afterSubmit, autoObservable = true } = {}) {
    this.afterSubmit = afterSubmit
    if (autoObservable) {
      makeAutoObservable(this)
    }
  }

  activeTab = 'manual'
  fileList = []
  receiptCodeList = []

  setActiveTab = (tab) => {
    this.activeTab = tab
  }

  addModal = new ModalStore({
    onOpen: (record = {}) => {
      this.setActiveTab('manual')
      this.fileList = []
      if (record.promotionResult) {
        record.promotionResultHandle = record.promotionResult
      }
      if (record.contractExpirationDate) {
        record.contractExpirationDate = moment(record.contractExpirationDate)
      }
      if (record.classify) {
        record.riskLevel = record.classify
      }
      return record
    },
    onFinish: async (values) => {
      if (this.activeTab === 'batch') {
        await this.batchImport({ file: this.fileList[0] })
        return
      }

      const payload = { ...values }
      if (values.contractLeaseType && typeof values.contractLeaseType === 'object') {
        payload.bizType = values.contractLeaseType.value
        payload.contractLeaseType = values.contractLeaseType.label
      }
      if (values.profitBelongDeptName && typeof values.profitBelongDeptName === 'object') {
        payload.profitBelongDeptId = values.profitBelongDeptName.value
        payload.profitBelongDeptName = values.profitBelongDeptName.label
      }
      if (values.projClassifyName && typeof values.projClassifyName === 'object') {
        payload.projClassify = values.projClassifyName.value
        payload.projClassifyName = values.projClassifyName.label
      }
      if (values.contractExpirationDate) {
        payload.contractExpirationDate = moment(values.contractExpirationDate).format('YYYY-MM-DD')
      }

      await this.addRecord(payload)
    },
  })

  openAddModal = (record = {}) => {
    this.addModal.open(record)
  }

  addRecord = async (record) => {
    const isEdit = !!record.id
    if (!isEdit) {
      const repeatContract = await EclRecordApi.postRecordAddCheck(record)
      await validateModal(
        {
          title: `「合同编号${repeatContract}」重复，是否继续新增？`,
          content: '点击【确认】则数据覆盖，点击【取消】则回到列表页',
        },
        !!repeatContract
      )
    }

    const func = isEdit ? EclRecordApi.postRecordModify : EclRecordApi.postRecordAdd
    await func({ ...record })

    message.success('操作成功')
    this.addModal.close()
    this.afterSubmit?.()
  }

  batchImport = async (formData) => {
    const repeatContract = await EclRecordApi.postImportCheck(formData)
    await validateModal(
      {
        title: `「合同编号${repeatContract?.join('、')}」重复，是否继续新增？`,
        content: '点击【确认】则数据覆盖，点击【取消】则回到列表页',
      },
      !!repeatContract.length
    )

    await EclRecordApi.postRecordImport(formData)

    message.success('导入成功')
    this.addModal.close()
    this.afterSubmit?.()
  }

  downloadTemplate = async () => {
    await EclRecordApi.getImportTemplate()
  }

  page = new PageStore({
    request: async () => {
      try {
        const [
          { configEnum: innerBreachMapping },
          { configEnum: ratingMapping },
          { configEnum: lossLgd },
        ] = await Promise.all([
          eclBusinessApi.postConfigDetail({ configCode: 'INNER_BREACH_MAPPING' }),
          eclBusinessApi.postConfigDetail({ configCode: 'RATING_MAPPING' }),
          eclBusinessApi.postConfigDetail({ configCode: 'LOSS_LGD' }),
        ])
        const { outerLevelEnum } = JSON.parse(ratingMapping)?.enums
        const { innerLevelEnum } = JSON.parse(innerBreachMapping)?.enums
        const { leaseTypeEnum } = JSON.parse(lossLgd)?.enums
        const { assetClassifyResultEnum, kpiRatingModelGroupEnum } = App.getData().optionsType
        return {
          enums: {
            assetClassifyResultEnum: enum2LabelEnum(assetClassifyResultEnum),
            kpiRatingModelGroupEnum: enum2LabelEnum(kpiRatingModelGroupEnum),
            innerLevelEnum: enum2LabelEnum(innerLevelEnum),
            outerLevelEnum: enum2LabelEnum(outerLevelEnum),
            leaseTypeEnum: enum2LabelEnum(leaseTypeEnum),
          },
        }
      } catch (error) {
        return { enums: {} }
      }
    },
  })

  beforeUpload = (file) => {
    this.fileList = [file]
    return false
  }

  onRemove = (file) => {
    const index = this.fileList.indexOf(file)
    const newFileList = this.fileList.slice()
    newFileList.splice(index, 1)
    this.fileList = newFileList
  }

  onChangeContractCode = async ({ value }) => {
    const form = this.addModal.getFormStore()
    const res = await ftpInterestChangeApi.postReceiptListByContract(
      { contractId: value },
      'eclReceiptlistByContract'
    )
    this.receiptCodeList = res?.map(({ receiptCode: label }) => ({ label, value: label })) || []
    form.setFieldsValue({
      receiptCode: null,
    })
  }
}

export default ProvisioningDataAddModalStore
