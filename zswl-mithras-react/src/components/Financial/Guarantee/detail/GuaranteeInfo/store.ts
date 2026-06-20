import { TableStore, Modal, ModalStore, App } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from '@/api/financial/guaranteeManage'
import moment from 'moment'
import DataUpload from '@/components/DataUpload'
class Store {
  id: any
  constructor({ id }) {
    this.id = id
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: async (searchData) => {
      // return []
      const data = await Api.postInfoList({ ...searchData, agencyId: +this.id })
      return data
    },
  })
  /**
   * 撤回
   */
  delete = () => {
    Modal.confirm({
      title: '是否确定关闭担保？',
      onOk: async () => {
        const { keys } = this.table.getSelected()
        await Api.postInfoRemove({ ids: keys })
        this.table.search()
      },
    })
  }
  okLoading = false
  finish = async (values) => {
    this.okLoading = true
    try {
      if (values.id) {
        const { fileList, originalList } = DataUpload.classify(values.files)
        values.addFiles = fileList
        const originalIdList = originalList.map((item) => item.id)
        values.delFileIds = values.fileListRSP
          .filter((item) => !originalIdList.includes(item.id))
          .map((item) => item.id)
        const data = await Api.postInfoModify(values)
      } else {
        const { fileList, originalList } = DataUpload.classify(values.files)

        values.files = fileList
        const data = await Api.postInfoAdd(values)
      }
      this.createModal.close()
      this.okLoading = false
      this.table.search()
    } catch (e) {
      this.okLoading = false
    }
    // history.push(``)
  }
  // 0:新增 1:初始编辑 2:编辑
  editMode = 0
  createModal = new ModalStore({
    onOpen: async (value) => {
      console.log('value: ', value)
      if (value?.id) {
        this.editMode = 1
        const data = await Api.postInfoDetail({
          id: value.id,
        })
        data.effectiveTime = [moment(data.effectiveTimeFrom), moment(data.effectiveTimeTo)]
        data.usedGuaranteeLimit = ((data.usedGuaranteeLimit || 0) / 10000).toFixed(0)
        data.remainingGuaranteeLimit = ((data.remainingGuaranteeLimit || 0) / 10000).toFixed(0)
        data.totalGuaranteeLimit = ((data.totalGuaranteeLimit || 0) / 10000).toFixed(0)
        data.files = data?.fileListRSP ?? []
        data.id = value?.id
        return data
      } else {
        this.editMode = 0
        return {}
      }
    },
    onFinish(values) {
      this.finish(values)
    },
  })
}
export default Store
