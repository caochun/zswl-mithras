import { history, makeAutoObservable } from '@zswl/admin'
import { ModalStore, TableStore, Modal } from '@zswl/components'
import { message } from 'antd'
import moment from 'moment'
import reportPlanApi from '@/api/budget/accountsReceivable/reportPlanApi'

/**
 * 应收账款页面的数据管理Store
 */
class Store {
  constructor() {
    makeAutoObservable(this)
  }

  // 列表数据管理
  list = new TableStore({
    request: async (params) => {
      return await reportPlanApi.postBaseList(params)
    },
  })

  // 创建报送计划弹窗管理
  createModal = new ModalStore({
    onFinish: async (values) => {
      const res = await reportPlanApi.postBaseAdd({
        planDate: values.planDate && moment(values.planDate).format('YYYY-MM-DD'),
      })
      message.success('创建报送计划成功！')
      history.replace(`/budget/accountsReceivable`)
      this.list.search()
      this.createModal.close()
    },
  })

  /**
   * 删除报送计划
   */
  handleClose = async () => {
    const { keys } = this.list.getSelected()
    Modal.confirm({
      title: '请确认是否关闭该报送计划？',
      onOk: async () => {
        await reportPlanApi.postBaseClose({ id: keys[0] })
        message.success('关闭成功！')
        this.list.search()
      },
    })
  }
}

export default Store
