import { makeAutoObservable, history } from '@zswl/admin'
import { ModalStore, PageStore, TableStore } from '@zswl/components'
import Api from '@/api/financial/orgManage'
import infoColumn from '../InfoColumn'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  isProjSponsor = false //判断是不是主办
  baseData = {}
  page = new PageStore({
    request: async (params) => {
      const { id, isFormApproval } = params
      if (!isFormApproval) {
        const res = await Api.postAgencyDetail({
          id,
        })
        this.baseData = res
        return { detail: res }
      }
      return {}
    },
  })

  postProjectBaseInfoModify = async (params, callback) => {
    const id = this.page.getParams()?.id
    await Api.postAgencyModify({ id, ...params })
    const res = await Api.postAgencyDetail({
      id,
    })
    this.baseData = res
    await this.page.setData({ detail: res })
  }
  syncLoading = false
  asyncThePage = async (id) => {
    this.syncLoading = true
    try {
      const data = await Api.postAgencySync({ id })
      this.syncLoading = false
      const tableData = []
      Object.entries(data)
        .filter(([key, value]) => value)
        .forEach(([key, value]) => {
          infoColumn.forEach((item) => {
            if (item.dataIndex === key) {
              tableData.push({
                name: item.title,
                data: value,
                itemRender: item.render,
                field: key,
              })
            }
          })
        })
      this.visible = true
      setTimeout(() => {
        this.asyncTable.setList(tableData)
      }, 0)
      // await this.page.init()
    } catch (e) {
      this.syncLoading = false
    }
  }
  asyncTable = new TableStore({})
  visible = false

  isEyeChange = (item) => {
    const { list } = this.asyncTable.getEditorData()
    this.asyncTable.setList(list)
  }
  syncCancel = () => {
    this.visible = false
  }
}
export default Store
