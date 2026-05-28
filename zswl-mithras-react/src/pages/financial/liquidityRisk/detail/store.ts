import { makeAutoObservable, history } from '@zswl/admin'
import { PageStore, FormStore, Modal } from '@zswl/components'
import Api from './api'
import { message } from 'antd'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: (params) => {
      if (params.id) {
        // id存在表示编辑，首先去获取详情，获取详情后根据需求可能需要对返回的数据进行相应的转换处理
        return Api.getDetail(params)
      }
    },
  })

  form = new FormStore()

  submit = async () => {
    const values = await this.form.submit()
    Modal.confirm({
      title: '确定提交吗？',
      onOk: async () => {
        const { id } = this.page.getParams()
        // 这里根据需求可能需要对values做一些转换处理
        const data = { id, ...values }
        if (id) {
          // 编辑
          await Api.update(data)
        } else {
          await Api.create(data)
        }
        message.success(`${id ? '编辑' : '创建'}成功`)
        history.goBack()
      },
    })
  }
}
export default new Store()
