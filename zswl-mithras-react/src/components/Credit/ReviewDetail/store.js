import { makeAutoObservable, history, getQuery } from '@zswl/admin'
import { ModalStore, PageStore } from '@zswl/components'
import { message } from 'antd'
import Api from '@/api/credit/groupCreditReviewApi'
import { compareDetail } from '@/utils'
import approvalRemarkApi from '@/api/credit/approvalRemarkApi'
import { validateModal } from '@/utils/modal'

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  baseInfoShowValue = true //  基本信息编辑态
  setBaseInfoShowValue = (val) => {
    this.baseInfoShowValue = val
  }

  page = new PageStore({
    request: async (params) => {
      const { id, isFormApproval, businessVersion, approvalParams, reconsiderParams } = params
      const [approvalDetail, reconsiderDetail] = await Promise.all([
        approvalRemarkApi.postReviewRemarkAll(approvalParams),
        approvalRemarkApi.postReviewRemarkAll(reconsiderParams),
      ])
        .then((res) => res)
        .catch((e) => {
          console.log(e)
          return [{}, {}]
        })

      if (!isFormApproval) {
        const res = await Api.getBaseInfo({
          groupCreditReviewId: id,
        })
        return { newDetail: res, approvalDetail, reconsiderDetail }
      } else {
        const res = await Api.getBaseInfoCompare({
          groupCreditReviewId: id,
          businessVersion,
        })
        return { ...compareDetail(res), approvalDetail, reconsiderDetail }
      }
    },
  })

  postProjectBaseInfoModify = async (params) => {
    await Api.postBaseInfoModify({ ...params, id: this.page.getParams().id })
    await this.page.init()
  }

  //变更日志
  changeLog = async (id) => {
    history.push(`/credit/review/detail/log/${id}`)
  }

  approvalLoading = false
  submitApproval = async (onlyCheck = false, extParams = {}) => {
    const id = this.page.getParams().id
    if (onlyCheck) {
      await Api.submit({ id, onlyCheck, ...extParams })
      return await this.validateRat(id)
    }
    if (this.baseInfoShowValue) {
      try {
        this.approvalLoading = true
        await this.validateRat(id)
        await Api.submit({ id, onlyCheck, ...extParams })
        await this.page.init()
        message.success('提交审批成功')
        this.approvalLoading = false
      } catch (e) {
        this.approvalLoading = false
      }
    } else {
      message.info('基本信息未保存，请先保存后提交审批！')
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
