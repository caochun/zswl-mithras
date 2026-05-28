import { makeAutoObservable, history } from '@zswl/admin'
import { TableStore } from '@zswl/components'
import allotApi from '@/api/kpi/projectAllot/allot'
import { message } from 'antd'
import { isBusinesshead } from '@/utils'
import Api from './api'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  activeKey = 'undeal'
  setActiveKey = (key) => {
    this.activeKey = key
  }

  commonParams = {}
  setCommonParams = (params) => {
    this.commonParams = params
  }

  unDealTable = new TableStore({
    request: async (params) => {
      const { page, pageSize, ...rest } = params
      this.setCommonParams(rest)
      const data = await allotApi.postProjectdistributionPagelist({
        ...params,
        distributionStatus: 0,
      })
      return data
    },
  })

  dealTable = new TableStore({
    request: async (params) => {
      const { page, pageSize, ...rest } = params
      this.setCommonParams(rest)
      const data = await allotApi.postProjectdistributionPagelist({
        ...params,
        distributionStatus: 1,
      })
      return data
    },
  })

  export = async (type) => {
    const params =
      type === 'deal'
        ? { ...this.dealTable.getParams(), distributionStatus: 1 }
        : { ...this.unDealTable.getParams(), distributionStatus: 0 }
    await Api.postAllocationExport(params)
    message.success('导出成功')
  }

  adjust = (pathname) => {
    const { rows } = this.dealTable.getSelected()
    if (!rows.length) {
      message.info('请先选择列表项')
      return
    }
    // const { belongDeptId } = rows[0]
    // if (!isBusinesshead(belongDeptId)) {
    //   message.info('只有业务负责人才有权限操作')
    //   return
    // }
    history.push(`${pathname}/detail/${rows[0]?.id}?source=adjust`)
  }
}
export default new Store()
