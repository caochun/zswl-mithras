import { makeAutoObservable } from '@zswl/admin'
import { TableStore, Modal } from '@zswl/components'
import { message } from 'antd'
import Api from './api'
import wrapStore from '../store'

class Store {
  constructor() {
    makeAutoObservable(this, { clientId: false })
  }
  //地址信息
  table = new TableStore({
    request: async (parameter) => {
      const { projName, projCode, contractCode, ...rest } = parameter
      return await Api.getList({
        ...rest,
        extra: {
          projName,
          projCode,
          contractCode,
        },
      })
    },
  })
  cancelProcess = async ({ processInstanceId }) => {
    Modal.confirm({
      title: '确认取消吗？',
      onOk: async () => {
        await Api.cancelProcess({ processInstanceId })
        this.table.search()
        message.success('取消成功！')
        wrapStore.myProcessCount()
      },
    })
  }
}
export default new Store()
