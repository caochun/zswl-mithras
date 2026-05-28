import { PageStore, Modal } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import { message } from 'antd'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  saveData = async (values) => {
    await Api.modifyCheckPlan({
      id: this.page.getParams().id,
      ...values,
    })
    this.page.init()
  }

  onSubmit = async () => {
    await Api.processPublish({
      planId: this.page.getParams().id,
    })
    message.success('提交成功')
    history.push('/afterLease/checkPlan?reload=true')
  }

  page = new PageStore({
    request: async (params) => {
      const res = await Api.getCheckPlanDetail({
        id: params.id,
        businessVersion: params.businessVersion,
      })
      return res ?? {}
    },
  })

  cancelFlow = async (id) => {
    Modal.confirm({
      title: `是否取消操作？`,
      onOk: async () => {
        await Api.processCancel({
          id,
        })
        message.info('操作成功')
        setTimeout(() => {
          history.push(`/afterLease/checkPlan`)
        }, 500)
      },
    })
  }
}
export default Store
