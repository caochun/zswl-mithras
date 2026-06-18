import { makeAutoObservable } from '@zswl/admin'
import { ModalStore } from '@zswl/components'
import { message } from 'antd'
import rootStore from '../store'
import { downFile } from '@/utils'
import Api from '@/api/common/materialsApi'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  businessVersion
  projectDataDetail
  getProjectDataDetail = async (id) => {
    if (id) {
      const res = await Api.postEstablishList({
        groupCreditEstablishId: id,
        businessVersion: this.businessVersion,
      })
      if (res) {
        this.projectDataDetail = res
      }
      return res
    }
  }
  download = async (ids) => {
    const res = await Api.postProjDownload({ ids })
    downFile(res)

    // if (code === 200) {
    //   message.info('下载成功')
    // } else {
    //   msg && message.info(msg)
    // }
  }
  remove = async (ids, businessType, callback) => {
    const { code, msg } = await Api.postProjectDataRemove({ ids, businessType })
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
    formData.append('businessType', 'PROJ_ESTABLISH')
    formData.append('materialsType', 'PROJ_INFORMATION')
    formData.append('belongId', rootStore.page.getParams().id)
    const { code, msg } = await Api.postProjectDataUpload(formData)
    if (code === 200) {
      this.getProjectDataDetail(rootStore.page.getParams().id)
    } else {
      message.info(msg)
    }
  }
  // loading = false
  getProjectDataDownload = async (ids) => {
    // this.loading = true
    const res = await Api.postProjDownload({ ids })
    downFile(res)
    // this.loading = false
  }
  createModal = new ModalStore({
    onFinish: async (values) => {
      const formData = new FormData()
      console.log(values)
      const file = new File(values.file, values.file[0].name)

      formData.append('file', file)
      formData.append('businessType', 'PROJ_ESTABLISH')
      formData.append('materialsType', values.materialsType)
      formData.append('belongId', rootStore.page.getParams().id)
      const { code } = await Api.postProjectDataUpload(formData)
      if (code === 200) {
        this.createModal.close()
        this.getProjectDataDetail(rootStore.page.getParams().id)
      }
    },
  })
}
export default new Store()
