import { TableStore, Modal, ModalStore, PageStore, App } from '@zswl/components'
import { makeAutoObservable, history } from '@zswl/admin'
import Api from '../api'
import evaluationAgencyApi from '@/api/lease/evaluationAgencyApi'
import { message } from 'antd'
import { uniqueId } from 'lodash'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  page = new PageStore({
    request: async ({ id }) => {
      const res = await Api.postContractDetail({
        id,
      })
      return res
    },
  })

  leaseTypeStr = ''
  leaseType = ''
  leaseTypeChange = (value) => {
    const { leaseItemManagerLeaseItemType } = App.getData().optionsType
    const labels = value?.map(
      (value) => leaseItemManagerLeaseItemType.find((item) => item.value === value)?.label
    )
    this.leaseType = value
    this.leaseTypeStr = labels?.join('、')
  }
  detail = {}
  detailModal = new ModalStore({
    onOpen: async (record) => {
      const res = await evaluationAgencyApi.postAppraisalDetail({ companyId: record.companyId })
      this.detail = res
      return res
    },
  })
  editable = false
  setEditable = (editable) => {
    this.editable = editable
  }
  update = async () => {
    const { creditCode, companyId } = this.detail
    await evaluationAgencyApi.postAppraisalLasted({ creditCode })
    const res = await evaluationAgencyApi.postAppraisalDetail({ companyId })
    this.detail = res
    message.success('更新成功')
  }
  table = new TableStore({
    request: async (params) => {
      const { id: leaseItemId } = this.page.getParams()
      const res = await evaluationAgencyApi.postLeaseItemList({ leaseItemId })
      return res.map((v) => ({ ...v, id: uniqueId() }))
    },
    pagination: false,
  })
  save = async () => {
    const { id: leaseItemId } = this.page.getParams()
    const { list, values } = await this.table.submit()
    const every = list.every((item) => item.companyId && item.purpose)

    const hasRepeat =
      new Set(list.map((item) => item.companyId).filter(Boolean)).size !== list.length

    if (!every) {
      message.error('请填写评估机构和用途')
      return false
    }
    if (hasRepeat) {
      message.error('评估机构不能重复')
      return false
    }
    await evaluationAgencyApi.postAppraisalRelation({
      leaseItemId,
      relationList: list,
    })
    this.table.search()
    this.setEditable(false)
    message.success('保存成功')
  }
  add = async () => {
    this.table.addRow({})
  }
  addModal = new ModalStore({
    onFinish: async (values) => {
      const companyId = await evaluationAgencyApi.postAppraisalAdd(values)
      this.addModal.close()
      message.success('添加成功')
      this.detailModal.open({ companyId })
      await this.getCompanyList()
    },
  })
  delete = async (record) => {
    this.table.deleteRow(record)
  }
  companyList = []
  setCompanyList = (companyList) => {
    this.companyList = companyList
  }
  getCompanyList = async (companyName) => {
    const res = await evaluationAgencyApi.postCompanyList({ companyName, pageSize: 999 })
    this.setCompanyList(res.list)
  }

  getLessees = () => {
    const pageData = this.page.getData()
    const userNames = []
    pageData?.tenant?.map((item) => {
      userNames.push(item.clientName)
    })
    console.log(userNames)
    return [...new Set(userNames)]?.join(',')
  }
}
export default Store
