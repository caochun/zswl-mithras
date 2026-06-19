import { makeAutoObservable } from '@zswl/admin'
import { TableStore } from '@zswl/components'
import { message } from 'antd'
import Api from '@/api/contract/material'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  unSignedTable = new TableStore({
    request: (params) => {
      return Api.postManageList({
        ...params,
      })
    },
  })
  signedTable = new TableStore({
    request: (params) => {
      return Api.postManageList({
        ...params,
      })
    },
  })

  downloadFile = async (contractId) => {
    await Api.postDownloadAll({ contractId })
    message.success('下载成功')
  }
}
export default Store
