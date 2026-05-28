import { PageStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import { message } from 'antd'
import Api from '../api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async (param) => {
      const res = await Api.postClientApplyDetail({
        clientId: param.id,
        processInstanceId: param.processInstanceId,
        batchNo: param.batchNo,
      })
      return res || {}
    },
  })

  handleCancel = () => {
    history.push('/customer/maintain')
  }
  handleSubmit = async () => {
    const { id, batchNo } = this.page.getParams()
    await Api.postClientApplyEffect({
      clientId: id,
      batchNo,
      ...this.page.getData(),
    })
    message.success('提交成功')
    history.push('/customer/maintain')
  }

  saveData = async (values) => {
    const { id, processInstanceId, batchNo } = this.page.getParams()
    const params = {
      clientId: id,
      processInstanceId,
      batchNo,
      applyReason: values.applyReason,
    }
    await Api.postApplyModify(params)
    this.page.init()
  }
}
export default Store
