import { FormStore, ModalStore, PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import litigationRegistrationApi from '@/api/overdue/litigationRegistrationApi'
import { message } from 'antd'
import moment from 'moment'

const formatTrialInfo = (trialInfo) => {
  if (!trialInfo) return {}
  const newTrialInfo = Object.entries(trialInfo).reduce((acc, [key, value]) => {
    if (key === 'id') {
      acc.trialInfoId = value
      return acc
    }
    if (key.indexOf('Date') > -1) {
      acc[key] = value ? moment(value) : undefined
    } else {
      acc[key] = value
    }
    return acc
  }, {})

  return newTrialInfo
}
class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({
    request: async (params) => {
      const { defendants, caseProgresses, trialInfo, contractCodes, contractIds, ...rest } =
        await litigationRegistrationApi.getLitigationDetail({
          id: params?.id,
        })
      const contract = (contractCodes ?? []).map((code, index) => ({
        label: code,
        value: contractIds[index],
      }))
      this.defendantList = defendants
      this.caseProgressesList = caseProgresses
      this.contractChange(contract)
      return { ...formatTrialInfo(trialInfo), ...rest, contract }
    },
  })
  contractList = []
  contractChange = (value: any[]) => {
    this.contractList = value
  }
  form = new FormStore({})
  table = new TableStore({
    request: async (params) => {
      return this.caseProgressesList
    },
  })
  caseProgressesList = []
  reloadTable = async () => {
    const id = this.page.getParams()?.id
    const { caseProgresses, ...rest } = await litigationRegistrationApi.getLitigationDetail({ id })
    this.caseProgressesList = caseProgresses
    this.table.search()
  }

  defendantList = []
  defendantTable = new TableStore({
    request: async (params) => {
      return this.defendantList
    },
  })
  reloadDefendantTable = async () => {
    const id = this.page.getParams()?.id
    const { defendants, ...rest } = await litigationRegistrationApi.getLitigationDetail({ id })
    this.defendantList = defendants
    this.defendantTable.search()
  }
  defendantModal = new ModalStore({
    onFinish: async (data) => {
      const hasRepeat = this.defendantList.some(
        (item) => item.certificateNumber === data.certificateNumber
      )
      if (hasRepeat) {
        message.error('该被告已存在,请勿重复添加')
        return
      }
      const id = this.page.getParams()?.id
      await litigationRegistrationApi.postDefendantAdd({ ...data, lrId: id })
      await this.reloadDefendantTable()
      this.defendantModal.close()
      message.success('新增成功')
    },
  })

  deleteDefendant = async () => {
    const lrId = this.page.getParams()?.id
    const { keys } = this.defendantTable.getSelected()
    await litigationRegistrationApi.postDefendantRemove({ ids: keys, lrId })
    await this.reloadDefendantTable()
    message.success('删除成功')
  }
  progressModal = new ModalStore({
    onOpen: ({ caseProgressesList }) => {
      if (caseProgressesList?.length > 0) {
        return caseProgressesList[0]
      }
      return { stage: 'firstInstance', status: null }
    },
  })
  addProgress = () => {
    this.progressModal.open({ caseProgressesList: this.caseProgressesList })
  }
  save = async () => {
    const id = this.page.getParams()?.id
    const trialInfoId = this.page.getData()?.trialInfoId
    const { contract, clientName, ...trialInfo } = await this.form.submit()
    const contractIds = contract?.map((item) => item.value)
    const contractCodes = contract?.map((item) => item.label)
    await litigationRegistrationApi.postLitigationSave({
      id,
      contractIds,
      contractCodes,
      trialInfo: { ...trialInfo, id: trialInfoId, lrId: id },
    })
    message.success('保存成功')
  }

  updateStatus = async (status: string) => {
    const id = this.page.getParams()?.id
    const form = this.progressModal.getFormStore()
    const { stage } = await form.validateFields()
    await litigationRegistrationApi.postProgressAdd({ lrId: id, status, stage })
    await this.reloadTable()
    this.progressModal.close()
    message.success('新增成功')
  }
  closeCase = () => {}
}
export default Store
