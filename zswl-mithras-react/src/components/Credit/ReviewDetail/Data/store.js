import { makeAutoObservable } from '@zswl/admin'
import { ModalStore } from '@zswl/components'
import { message } from 'antd'
import rootStore from '../store'
import Api from '@/api/credit/groupCreditMaterialsApi'
import { downFile } from '@/utils/downFunction'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  businessVersion
  projectDataDetail
  getProjectDataDetail = async (id) => {
    if (id) {
      const res = await Api.postReviewList({
        groupCreditReviewId: id,
        businessVersion: this.businessVersion,
      })
      if (res) {
        this.projectDataDetail = res
      }
      return res
    }
  }
  download = async (ids, filename) => {
    const res = await Api.postReviewDownload({ ids, filename })
    downFile(res)
  }
  remove = async (ids, businessType, callback) => {
    const { code, msg } = await Api.postReviewRemove({ ids, businessType })
    if (code === 200) {
      message.info('删除成功')
      callback && callback()
      this.getProjectDataDetail(rootStore.page.getParams().id)
    } else {
      message.info(msg)
    }
  }

  upload = async (fileData) => {
    const formData = new FormData()
    const fileValue = new File([fileData], fileData.name)
    formData.append('file', fileValue)
    formData.append('businessType', 'PROJ_REVIEW')
    formData.append('materialsType', 'PROJ_INFORMATION')
    formData.append('belongId', rootStore.page.getParams().id)
    const { code, msg } = await Api.postReviewUpload(formData)
    if (code === 200) {
      message.success('上传成功！')
      this.getProjectDataDetail(rootStore.page.getParams().id)
    } else {
      message.info(msg)
    }
  }

  createModal = new ModalStore({
    onFinish: async (values) => {
      const formData = new FormData()
      const file = new File(values.file, values.file[0].name)

      formData.append('file', file)
      formData.append('businessType', 'PROJ_ESTABLISH')
      formData.append('materialsType', values.materialsType)
      formData.append('belongId', rootStore.page.getParams().id)
      const { code } = await Api.postReviewUpload(formData)
      if (code === 200) {
        this.createModal.close()
        this.getProjectDataDetail(rootStore.page.getParams().id)
      }
    },
  })
}
export default new Store()
