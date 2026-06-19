import { TableStore, ModalStore, Modal} from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import {message} from 'antd'
import Api from '@/api/cpm/contractCpmApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  table = new TableStore({
    request: async (rest) => {
      return await Api.getList({ ...rest })
    },
  })

  exportList = async () => {
    const params = this.table.getParams()
    await Api.exportList({ ...params, pageSize: 5000 })
  }

  pushRentNotify = () => {
    Modal.confirm({
      content:'是否发送次月租金支付通知书至会计岗？',
      okButtonProps: {
        onClick: async(e) => {
          e.stopPropagation(); // 阻止事件冒泡到 Modal 内置逻辑
          e.preventDefault(); // 阻止默认行为
          const params = this.table.getParams()
          const res = await Api.pushRentNotify({ ...params})
          message.success('发送成功')
        },
      }
    })
  }

  $checkLetter = new ModalStore({})
}
export default new Store()
