import { makeAutoObservable } from '@zswl/admin'
import { TableStore } from '@zswl/components'
import api from '@/api/message/messageNotification'
import moment from 'moment'
import { Modal, message } from 'antd'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  msgTable = new TableStore({
    request: async (params) => {
      return await this.getList(params)
    },
  })
  getList = async (params) => {
    const res = await api.postMessageList({
      ...params,
      createStart:
        params.time && moment(params.time[0]).startOf('day').format('yyyy-MM-DD HH:mm:ss'),
      createEnd: params.time && moment(params.time[1]).endOf('day').format('yyyy-MM-DD HH:mm:ss'),
    })
    return res
  }
  readmsgAll = async () => {
    Modal.confirm({
      title: `是否将所有消息设为已读？`,
      onOk: async () => {
        await api.postReadAllMessage({ messageChannel: 'PC' })
        message.success('操作成功')
        this.msgTable.search()
      },
    })
  }
}

export default new Store()
