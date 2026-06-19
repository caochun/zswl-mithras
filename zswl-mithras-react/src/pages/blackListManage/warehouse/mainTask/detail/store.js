import { App, FormStore, ModalStore, PageStore, TableStore, Upload } from '@zswl/components'
import { getQuery, history, makeAutoObservable } from '@zswl/admin'
import warehouseTaskApi from '@/api/blackGray/warehouseTaskApi'
import recordTableApi from '@/api/blackGray/recordTableApi'
import { message } from 'antd'
import approvalControlApi from '@/api/blackGray/approvalControlApi'
import moment from 'moment'

const Api = {}
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  isSupplyGroupInfo = false
  setIsSupplyGroupInfo = (value) => {
    this.isSupplyGroupInfo = value
  }
  page = new PageStore({
    request: async (params) => {
      const data = await warehouseTaskApi.postTaskDetail({ id: `${params?.id}` })
      const { uploadFileList = [], isSupplyGroupInfo, ...rest } = data
      this.setIsSupplyGroupInfo(isSupplyGroupInfo)
      const newUploadFileList = await App.queryFileData(uploadFileList)
      return {
        uploadFileList: newUploadFileList,
        uploadFile: 1,
        ...rest,
      }
    },
  })
  form = new FormStore()
  table = new TableStore({
    request: async (params) => {
      const { taskNum, auditStatus } = this.page.getData()
      return await recordTableApi.postRecordList({
        taskNum,
        isHistory: auditStatus === 4 ? 0 : undefined,
      })
    },
  })
  subManageTable = new TableStore({})
  download = () => {}
  checkFile = async (files = []) => {
    if (files?.every((item) => item.status === 'done')) {
      const fileKeys = files.map((item) => item.key)
      this.exportFileKeys = fileKeys
      this.table.search({ fileKeys })
    }
  }
  export = (params) =>
    recordTableApi.getRecordExport({ ...params, taskNum: this.page.getData().taskNum })
  submit = async (values = {}) => {
    await this.save(true)
    const { id } = this.page.getParams()
    if (!this.isSupplyGroupInfo) {
      message.info('请先查询所属集团')
      return
    }
    await approvalControlApi.postManualTask({ ...values, id })
    message.success(`提交成功`)
    history.goBack()
  }

  save = async (isSubmit = false) => {
    const params = await this.form.submit()
    const { id } = this.page.getParams()
    const { uploadFileList = [], ...rest } = params
    if (uploadFileList.length) {
      await warehouseTaskApi.postTaskModify({ uploadFileList, id })
    }
    if (!isSubmit) message.success('保存成功')
  }
  approvalForm = new FormStore({})
  singleModalStore = new ModalStore({
    onOpen: ({
      warehouseTime,
      blackGrayType,
      applyReasonType,
      enterpriseName,
      unifiedSocialCreditCode,
      ...params
    }) => {
      const periodUnderObservation = blackGrayType === 'BLACK_LIST' ? '12' : '6'
      return {
        warehouseTime: warehouseTime ? moment(warehouseTime) : moment(),
        blackGrayType,
        periodUnderObservation,
        applyReasonType: applyReasonType?.[0],
        enterpriseName: enterpriseName
          ? { label: enterpriseName, value: unifiedSocialCreditCode }
          : undefined,
        ...params,
      }
    },
    onFinish: async ({ warehouseTime, ...params }) => {
      const { taskNum } = this.page.getData()
      const func = params.id ? recordTableApi.postRecordModify : recordTableApi.postRecordAdd
      const recordId = await func({
        ...params,
        taskNum,
        warehouseTime,
        source: 'INTERNAL_APPROVAL',
      })
      message.success(`${params.id ? '编辑' : '保存'}成功`)
      this.singleModalStore.close()
      this.table.search()
      return { applyReasonType: [] }
    },
  })
}
export default Store
