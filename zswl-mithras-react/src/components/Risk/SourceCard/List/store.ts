import { message } from 'antd'
import riskCardInfoApi from '@/api/risk/riskCardInfoApi'
import { ModalStore, PageStore, TableStore } from '@zswl/components'
import { history, makeAutoObservable } from '@zswl/admin'
import riskCardTargetCalc from '@/api/risk/riskCardTargetCalc'
import moment from 'moment'
import DataUpload from '@/components/DataUpload'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: (params) => {
      // return riskCardInfoApi.getDetail({ id: params?.id })
    },
  })
  table = new TableStore({
    request: (params) => {
      return riskCardInfoApi.postInfoList(params)
    },
  })
  add = async () => {
    this.createModal.open()
    // const { id } = await riskCardInfoApi.postInfoAdd({})
    // history.push(`/risk/riskStrategy/sourceCard/detail/${id}`)
  }
  delete = async () => {
    const { keys: ids } = this.table.getSelected()
    await riskCardInfoApi.postInfoRemove({ ids })
    message.success('删除成功')
    this.table.search()
  }
  import = async ({ file }) => {
    await riskCardTargetCalc.postCardImport({ file })
    message.success('导入成功')
  }
  preview = async () => {
    const year = moment().format('YYYY')
    const { id } = await riskCardInfoApi.getScoreDownload({ year })
    window.open(`/preview/reportPreview/${id}`)
  }
  createModal = new ModalStore({
    onFinish: async (values) => {
      const res = await riskCardInfoApi.postInfoAdd(values)
      history.push(`/risk/riskStrategy/sourceCard/detail/${res.id}`)
      this.createModal.close()
    },
  })
  uploadModal = new ModalStore({
    onFinish: async (values) => {
      const { fileList } = DataUpload.classify(values.file)

      const params = {
        file: fileList[0],
        year: moment(values.year).format('YYYY'),
      }

      await riskCardTargetCalc.postCardImport(params)
      message.success('导入成功')
      this.uploadModal.close()
    },
  })
}
export default new Store()
