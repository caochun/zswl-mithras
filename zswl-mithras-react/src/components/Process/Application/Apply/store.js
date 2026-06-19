import { makeAutoObservable } from '@zswl/admin'
import { TableStore, Modal } from '@zswl/components'
import { message } from 'antd'
import Api from '@/api/process/application/myProcessApi'
import wrapStore from '../store'
class Store {
  constructor() {
    makeAutoObservable(this, { clientId: false })
  }

  table = new TableStore({
    request: async (parameter) => {
      const { projName, projCode, contractCode, ...rest } = parameter
      return await Api.getApplyingList({
        ...rest,
        extra: {
          projName,
          projCode,
          contractCode,
        },
      })
    },
  })
  //撤回
  withdrawToStartUse = async ({ processInstanceId }) => {
    Modal.confirm({
      title: '确认撤回吗？',
      onOk: async () => {
        await Api.withdrawToStartUser({ processInstanceId })
        this.table.search()
        message.success('撤回成功！')
        wrapStore.myProcessCount()
      },
    })
  }
  cancelProcess = async ({ processInstanceId, modelKey, businessKey }) => {
    Modal.confirm({
      title: '确认取消吗？',
      onOk: async () => {
        if (modelKey === 'PaymentActualDetailFlow') {
          const res = await Api.postPayMentCloseBeforeCheck({ id: businessKey })
          res.msg && message.info(res.msg)
        }
        await Api.cancelProcess({ processInstanceId })
        this.table.search()
        message.success('取消成功！')
        wrapStore.myProcessCount()
      },
    })
  }
}
export default new Store()
