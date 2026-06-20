import { makeAutoObservable } from '@zswl/admin'
import { message } from 'antd'
import { hasPermission } from '@/utils'
import Api from '@/api/dashboard/unifiedTodo'

const pageParams = { page: 1, pageSize: 1 }

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  countObj = {}
  setCountObj = (data) => {
    this.countObj = data
  }

  updateMsgCount = async () => {
    const res = await Api.postMessageList({ ...pageParams, needRead: 2 })
    this.setCountObj({
      ...this.countObj,
      msgCount: res.total,
    })
  }

  publicMonitorCount = 0
  setPublicMonitorCount = (count) => {
    this.publicMonitorCount = count
  }

  getCount = async () => {
    const allCountFn2 = {
      todoCount: Api.postDashboardToDoList(pageParams),
      myInitiateCount: Api.postDashboardToDoMyProcessApply(pageParams),
      doingCount: Api.postDashboardToDoMyProcessDoing(pageParams),
      doneCount: Api.postDashboardToDoMyProcessFinish(pageParams),
      ccCount: Api.postDashboardTaskMyReceiveCCList(pageParams),
      msgCount: Api.postMessageList({ ...pageParams, needRead: 2 }),
    }
    const allCountFn = Object.values(allCountFn2)
      .filter(Boolean)
      .map((promise) => promise.catch((e) => console.log('错误信息: ' + e)))

    Promise.all(allCountFn)
      .then((res) => {
        this.setCountObj({
          todoCount: res[0]?.length,
          myInitiateCount: res[1]?.total,
          doingCount: res[2]?.total,
          doneCount: res[3]?.total,
          ccCount: res[4]?.total,
          msgCount: res[5]?.total,
        })
      })
      .catch((err) => {
        console.log({ err })
      })

    if (hasPermission('riskcontrolopinionmonitorunresolved-dashboard')) {
      const res = await Api.postMonitorUnresolved(pageParams)
      this.setPublicMonitorCount(res?.total)
    }
  }
}
export default Store
