import { makeAutoObservable } from '@zswl/admin'
import { message } from 'antd'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  dataSource = []
  getList = async (contractId, businessVersion) => {
    const res = await Api.postLeaseFileList({ contractId, businessVersion })
    const result = res.map((item, index) => {
      return {
        id: `folder-${index}`,
        key: item.groupTypeName,
        value: item.fileDataList.map((i, j) => {
          return {
            ...i,
            id: i.fileId,
            key: i.fileId,
            idType: i.idType,
            name: `${index + 1}-${j + 1}. ${i.fileName}`,
          }
        }),
      }
    })
    return result
  }
}
export default Store
