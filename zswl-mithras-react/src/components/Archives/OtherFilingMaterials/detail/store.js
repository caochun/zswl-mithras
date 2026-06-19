import { makeAutoObservable, history } from '@zswl/admin'
import { PageStore } from '@zswl/components'
import { message, Modal } from 'antd'
import otherFilingMaterialsApi from '@/api/archives/otherFilingMaterials'
import parentStore from '../store'
class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({
    request: (params) => {
      if (!params?.id) return {}
      return otherFilingMaterialsApi.getOperationsDirDict(params.id)
    },
  })
  cancel = (id) => {
    Modal.confirm({
      title: '是否取消操作？',
      onOk: async () => {
        await otherFilingMaterialsApi.cancel({
          id,
        })
        message.success('取消成功')
        parentStore.table.search()
        setTimeout(() => {
          history.push('/archives/otherFilingMaterials')
        }, 500)
      },
    })
  }
  submit = async (id) => {
    Modal.confirm({
      title: '确认提交吗？',
      onOk: async () => {
        await otherFilingMaterialsApi.submit({
          id,
        })
        message.success('提交成功')
        parentStore.table.search()
        setTimeout(() => {
          history.push(`/archives/otherFilingMaterials`)
        }, 500)
      },
    })
  }
}
export default Store
