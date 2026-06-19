import { makeAutoObservable } from '@zswl/admin'
import Api from '@/api/contract/component/ContractMaterials/api'
class Store {
  constructor() {
    makeAutoObservable(this)
  }

  getList = async (contractId, businessVersion) => {
    const res = await Api.postDataList({ contractId, businessVersion })
    const result = res.map((item, index) => {
      return {
        id: `folder-${index}`,
        key: item.groupTypeName,
        value: item.fileDataList.map((i, j) => {
          return {
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
