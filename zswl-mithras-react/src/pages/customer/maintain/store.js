import { TableStore, Modal, ModalStore, PageStore } from '@zswl/components'
import { makeAutoObservable, history, setSessionStorage, getSessionStorage } from '@zswl/admin'
import { message } from 'antd'
import qs from 'query-string'
import Api from './api'

class DataStore {
  constructor() {
    makeAutoObservable(this)
  }
  table = new TableStore({
    request: async ({ createDate, updateDate, industryType, ...rest }) => {
      const [start, end] = createDate || []
      const [start1, end1] = updateDate || []
      const industryTypes = industryType ? industryType[industryType.length - 1] : ''
      const searchData = {
        showApprovalFlag: true,
        createDateFrom: start?.format('yyyy-MM-DD'),
        updateDateFrom: start1?.format('yyyy-MM-DD'),
        createDateTo: end?.format('yyyy-MM-DD'),
        updateDateTo: end1?.format('yyyy-MM-DD'),
        ...rest,
      }
      if (industryType) {
        searchData.industryType = industryTypes
      }
      const res = await Api.getList(searchData)
      return res
    },
  })
  industry = []
  initIndustry = async () => {
    const getLocalIndustryMap = getSessionStorage('industryMap')
    if (getLocalIndustryMap) {
      this.industry = getLocalIndustryMap
      return
    }
    const list = await Api.getAllIndustry()
    this.industry = list
    setSessionStorage('industryMap', JSON.stringify(list))
  }

  /**
   * 撤回
   */
  remove = ({ id, clientName }) => {
    Modal.confirm({
      title: `请再次确认是否删除客户：${clientName} ？`,
      onOk: async () => {
        await Api.remove({ id })
        this.table.search()
      },
    })
  }

  /**
   * 新增客户
   */

  createModal = new ModalStore({
    onFinish: async (values) => {
      const { clientType, domesticOrAbroad } = values
      if (clientType === 'NORMAL') {
        const id = await Api.createNormal(values)
        this.toDetail({ id, clientType, domesticOrAbroad, isCreate: 1 })
        this.createModal.close()
      } else {
        const { code, success, msg, data } = await Api.createCorp(values)
        if (success) {
          this.toDetail({ id: data.clientId, clientType, domesticOrAbroad, isCreate: 1 })
          this.createModal.close()
        } else if (code === -511) {
          Modal.confirm({
            title: '是否继续添加客户？',
            onOk: async () => {
              const { clientId } = await Api.createCorpMuteTyc({ muteTycError: true, ...values })
              this.toDetail({ id: clientId, clientType, domesticOrAbroad, isCreate: 1 })
              this.createModal.close()
            },
            onCancel: () => {
              this.createModal.close()
            },
          })
        } else {
          message.error(msg)
        }
      }
    },
  })

  handoverModal = new ModalStore({
    onFinish: async (values) => {
      const { belongDept, belongSponsor } = values
      this.handoverModal.close()
      const initData = {
        belongSponsorName: belongSponsor.label,
        belongSponsorId: belongSponsor.value,
        belongDeptId: belongDept.value,
        belongDeptName: belongDept.label,
      }
      history.push(`/customer/maintain/handover?initData=${JSON.stringify(initData)}`)
    },
  })
  withdraw = () => {
    this.handoverModal.open()
  }
  toDetail = async ({ id, clientType, domesticOrAbroad, flag, typeId, isCreate }) => {
    // 调用接口判断，该客户已被${客户所属业务部门}-${客户所属主办}占有，当前无查看权限。
    const res = await Api.postClientApplyOccupy({ clientId: id })
    if (res?.message) {
      message.warn(res?.message)
      return
    }
    const queryObj = {
      clientType,
      domesticOrAbroad,
      flag: flag || 'create',
      typeId: typeId || 'create',
      // 客户列表这边跳详情，需要这个字段，表示只能看自己的版本。其他的入口进来只能看 生效的版本
      isClientDetail: true,
      isCreate,
    }
    if (id) {
      history.push(`/customer/maintain/detail/${id}?${qs.stringify(queryObj)}`)
    }
  }

  btnStatus = {}
  getButtonStatus = async (id) => {
    this.btnStatus = await Api.getButtonStatus({ id })
  }

  applyPermissionModal = new ModalStore({
    onFinish: async (values) => {
      const { clientId } = values
      this.applyPermissionModal.close()
      await Api.postClientApplyValidate({ clientId })
      const batchNo = await Api.postClientBatchNumber({ clientId })
      history.push(`/customer/maintain/applyPermission/${clientId}?batchNo=${batchNo}`)
    },
  })
}
export default DataStore
