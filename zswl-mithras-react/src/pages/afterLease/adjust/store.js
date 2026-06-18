import { TableStore, ModalStore, PageStore } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import { timeFormat } from '@/utils'
import { debounce as _debounce } from 'lodash'
import { message } from 'antd'
import Api from '@/api/afterLease/adjust'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  page = new PageStore({})

  $table = new TableStore({
    request: (data) => {
      const currentData = {
        ...data,
        beganTime: data.adjustTime ? timeFormat(data.adjustTime[0]) : undefined,
        endTime: data.adjustTime ? timeFormat(data.adjustTime[1]) : undefined,
        adjustTime: undefined,
      }
      return Api.getList(currentData)
    },
  })

  // 创建
  curAction = {}
  projectList = []
  getProject = _debounce(async (e) => {
    const res = await Api.getProjList({ projVagueName: e })
    this.projectList = res ?? []
  }, 500)

  createModal = new ModalStore({
    onOpen: (data) => {
      this.curAction = data
      this.getProject()
    },
    onFinish: async (values) => {
      const { projId } = values
      const { code, msg, data } = await Api.addInfo({
        projId,
        afterLeaseAdjustType: this.curAction.type,
      })
      if (code === 200) {
        this.createModal.close()
        this.$table.search()
        history.push(`/afterLease/adjust/detail/${data.adjustId}?isCreate=true`)
      } else {
        message.info(msg)
      }
    },
  })
}
export default Store
