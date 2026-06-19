import { message } from 'antd'
import { FormStore, ModalStore, PageStore, TableStore } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import riskCardInfoApi from '@/api/risk/riskCardInfoApi'
import riskCardTargetCalc from '@/api/risk/riskCardTargetCalc'
import riskCardTargetApi from '@/api/risk/riskCardTargetApi'
import _ from 'lodash'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async (params) => {
      const { id } = params
      const res = await riskCardInfoApi.postInfoDetail({ id })
      this.getTargetList()
      return res
    },
  })
  form = new FormStore({})
  import = ({ file }) => {
    return riskCardTargetCalc.postCardImport({ file })
  }
  cacheForm = {}
  getTargetList = async () => {
    const cardId = this.page.getParams()?.id
    const res = await riskCardTargetApi.postTargetList({ cardId })
    this.cacheForm = res?.list || []
    setTimeout(() => {
      this.form.setFieldsValue({
        targetList: res?.list || [],
      })
    }, 0)
  }
  saveTarget = async (data) => {
    const cardId = this.page.getParams().id
    const { areaConfig, areaStatus } = data
    data.areaConfig = areaStatus === 'NO_PARTITION' ? [areaConfig[0]] : areaConfig
    data.cardId = cardId
    const func = data.id ? riskCardTargetApi.postTargetModify : riskCardTargetApi.postTargetAdd
    const res = await func(data)
    await this.getTargetList()
    this.page.init()
  }

  saveData = async (params) => {
    params.id = this.page.getParams().id
    await riskCardInfoApi.postInfoModify(params)
    await this.page.init()
  }
  deleteTarget = async (id) => {
    const res = await riskCardTargetApi.postTargetRemove({ id })
    message.success('删除成功')
    await this.page.init()
    await this.getTargetList()
  }
  calcModal = new ModalStore({ onOpen: async () => {} })
  cancel = () => {
    this.form.setFieldsValue({
      targetList: this.cacheForm,
    })
  }
  tryCalc = async (data) => {
    const { status } = this.page.getData()
    if (status === 'NOT_EFFECT') {
      message.error('生效的卡片才能进行试计算')
      return
    }
    this.calcModal.open()
  }
}
export default Store
