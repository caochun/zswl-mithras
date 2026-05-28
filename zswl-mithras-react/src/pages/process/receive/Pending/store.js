import { makeAutoObservable } from '@zswl/admin'
import { TableStore, Modal } from '@zswl/components'

import Api from './api'
import { message } from 'antd'
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

  batchPass = async () => {
    const { keys } = this.table.getSelected()

    Modal.confirm({
      title: '确认一键通过已选中的流程吗？',
      onOk: async () => {
        await Api.batchPass({ taskIdList: keys })
        message.success('操作成功')
        this.table.search()
      },
    })
  }
}
export default new Store()
