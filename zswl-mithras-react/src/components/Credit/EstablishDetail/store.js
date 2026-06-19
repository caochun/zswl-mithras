import { makeAutoObservable, history } from '@zswl/admin'
import { ModalStore, PageStore } from '@zswl/components'
import Api from '@/api/credit/groupCreditEstablishApi'
import versionApi from '@/api/credit/groupCreditEstablishVersionApi'
import { compareDetail } from '@/utils'
import { message } from 'antd'
import approvalRemarkApi from '@/api/credit/approvalRemarkApi'
import { validateModal } from '@/utils/modal'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  bizType
  projectId
  baseInfoShowValue = true //  基本信息编辑态
  QSShowValue = true // 报价方案编辑态
  QSZLShowValue = true // 报价方案-新增租赁编辑态

  approvalLoading = false
  setBaseInfoShowValue = (val) => {
    this.baseInfoShowValue = val
  }
  setQSShowValue = (val) => {
    this.QSShowValue = val
  }
  setQSZLShowValue = (val) => {
    this.QSZLShowValue = val
  }
  isProjSponsor = false //判断是不是主办
  page = new PageStore({
    request: async (params) => {
      const { id, isFormApproval, businessVersion, approvalParams } = params
      this.projectId = id
      const approvalDetail = await approvalRemarkApi
        .postEstablishRemarkAll(approvalParams)
        .then((res) => res)
        .catch((e) => {
          console.log(e)
          return {}
        })

      if (!isFormApproval) {
        const res = await Api.postInfoDetail({
          groupCreditEstablishId: id,
        })

        if (res) {
          this.isProjSponsor = res.isProjSponsor
          this.bizType = res.bizType
          return { newDetail: res, approvalDetail }
        }
      } else {
        const res = await Api.postInfoDetailCompare({
          groupCreditEstablishId: id,
          businessVersion,
        })
        return { ...compareDetail(res), approvalDetail }
      }

      return {}
    },
  })

  postProjectBaseInfoModify = async (params, callback) => {
    await Api.postInfoModify({ id: this.projectId, ...params })
    await this.page.init()
  }
  //变更日志
  changeLog = async (id) => {
    history.push(`/credit/establish/detail/log/${id}`)
  }
  submitApproval = async (id, onlyCheck, extParams = {}) => {
    if (onlyCheck) {
      await versionApi.postEstablishEffect({ id, onlyCheck, ...extParams })
      return await this.validateRat(id)
    }
    try {
      this.approvalLoading = true
      await this.validateRat(id)
      await versionApi.postEstablishEffect({ id, onlyCheck, ...extParams })
      await this.page.init()
      // history.push('/credit/establish')
      message.success('提交审批成功')
      this.approvalLoading = false
    } catch (e) {
      this.approvalLoading = false
    }
  }
  goRat = async () => {
    const { newDetail } = this.page.getData()
    const { clientName } = newDetail ?? {}
    const search = JSON.stringify({
      clientName,
    })
    window.open(`/customer/customerRat?search=${search}`)
  }
  newDetail = {}
  updateInfo = async () => {
    const id = this.page.getParams().id
    const res = await Api.postBaseInfoUpdateRating({ id })
    message.success('更新成功')
    this.newDetail = res
  }
  validateRat = async (id) => {
    const { ratingClientIsDone } = await Api.ratingCheck({
      id,
    })
    return validateModal(
      {
        title: '评级校验未通过',
        okText: '客户评级',
        cancelText: '确定',
        onOk: () => {
          this.goRat()
          return Promise.reject()
        },

        content: '无生效的授信主体客户评级信息，请完成评级后再提交流程！',
      },
      !ratingClientIsDone
    )
  }
}
export default Store
