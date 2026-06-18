import { history, makeAutoObservable } from '@zswl/admin'
import { App, ModalStore, TableStore } from '@zswl/components'
import { message } from 'antd'
import QSStore from '../QuotationScheme/store'
import DataApi from '../Data/api'
import Api from './api'
class Store {
  constructor({ rootStore }) {
    this.rootStore = rootStore
    makeAutoObservable(this)
  }
  onlyOfficeParams
  reportLoading = false
  expandKeys = ['folder-0']
  setExpandKeys = (data) => {
    this.expandKeys = data
  }
  // rootStore = null
  // initStore = (val) => {
  //   this.rootStore = val
  // }
  table = new TableStore({
    request: async (params) => {
      if (!this.rootStore.page.getParams().id) {
        return []
      }
      const res = await Api.postReportList({
        ...params,
        projEstablishId: this.rootStore?.projectId,
        businessVersion: this.rootStore?.page.getParams().businessVersion,
      })
      this.setExpandKeys(res?.map((item, index) => `folder-${index}`))
      return (
        res.map((item, index) => {
          return {
            typeName: App.matchOption('projEstablishMaterialsEnum', item.key).label,
            id: `folder-${index}`,
            children: item.value,
          }
        }) || []
      )
    },
  })

  upload = async (params, callback) => {
    const formData = new FormData()
    const { materialsType, file, config } = params
    formData.append('file', file)
    formData.append('projEstablishId', this.rootStore?.page.getParams().id)
    formData.append('materialsType', materialsType)
    const { code, msg } = await Api.postReportUpload(formData, config)
    if (code === 200) {
      message.success('上传成功')
      this.table.search()
      callback && callback()
    } else {
      message.info(msg)
    }
    return code
  }

  uploadModal = new ModalStore({
    onFinish: (values) => {
      this.upload(values, () => this.uploadModal.close())
    },
  })
  remove = async (id) => {
    const { code, msg } = await Api.postReportRemove({ id })
    if (code === 200) {
      message.success('删除成功')
      this.table.search({ projEstablishId: this.rootStore?.page.getParams().id })
    } else {
      message.info(msg)
    }
  }
  generate = async (projEstablishId) => {
    try {
      this.reportLoading = true
      const { code, data, msg } = await Api.postReportGenerate({ projEstablishId })
      this.reportLoading = false
      if (code === 200) {
        message.success('报告生成成功')

        window.open(`/preview/reportPreview/${data}`)
      } else {
        message.info(msg)
      }
    } catch (e) {
      this.reportLoading = false
    }
  }
}
export default Store
