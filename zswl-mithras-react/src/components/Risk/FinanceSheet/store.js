import { makeAutoObservable } from '@zswl/admin'
import { TableStore } from '@zswl/components'
import { message } from 'antd'
import DataUpload from '@/components/DataUpload'
import Api from '@/api/risk/financeSheet'
import moment from 'moment'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: async (params) => {
      const currentData = {
        ...params,
        factorDate: params.factorDate
          ? moment(params.factorDate).format('yyyy-MM') + '-01'
          : undefined,
      }
      return Api.list(currentData)
    },
  })

  loading = false
  importSheet = async (values) => {
    const { fileList } = DataUpload.classify(values)
    this.loading = true
    await Api.import({
      file: fileList[0],
    }).finally(() => {
      this.loading = false
    })
    message.success('导入成功')
    this.table.search()
  }

  allSelect = {}
  getSelect = async () => {
    const res = await Api.allSelect()
    this.allSelect = {
      ...res,
      needReportEumn: [
        {
          value: true,
          label: '是',
        },
        {
          value: false,
          label: '否',
        },
      ],
    }
  }
}
export default new Store()
