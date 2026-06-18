import { makeAutoObservable, history } from '@zswl/admin'
import { FormStore, ModalStore, PageStore } from '@zswl/components'
import { message, Modal } from 'antd'
import { compareDetail } from '@/utils'
import Api from '@/api/contract/contractDetail'
import priceApi from '@/api/contract/priceApi'
import approvalRemarkApi from '@/api/contract/approvalRemarkApi'
import { validateModal } from '@/utils/modal'
class Store {
  constructor(data) {
    this.isFormApproval = data?.isFormApproval
    makeAutoObservable(this)
  }

  // 报价方案数据
  priceData = {}
  setPriceData = (data) => {
    this.priceData = data
  }

  // 业务场景
  bizType
  setBizType = (type) => {
    this.bizType = type
  }
  contractStatus
  contractProcessStatus

  // 基本信息编辑态
  baseInfoShowValue = true
  setBaseInfoShowValue = (val) => {
    this.baseInfoShowValue = val
  }

  // 是否主办
  isProjSponsor = false

  // 业务类型
  leaseTypes = ''

  page = new PageStore({
    request: async ({ contractId, isFormApproval, businessVersion, approvalParams }) => {
      const approvalDetail = await approvalRemarkApi
        .postRemarkAll(approvalParams)
        .then((res) => res)
        .catch((e) => {
          console.log(e)
          return [{}, {}]
        })

      if (isFormApproval) {
        const res = await Api.getBaseInfoCompare({ id: contractId, businessVersion })
        this.setBizType(res.bizType?.value)
        this.contractStatus = res.contractStatus?.value
        this.contractProcessStatus = res.contractProcessStatus?.value
        this.leaseTypes = res.leaseType?.value
        const newData = compareDetail(res)
        return { ...newData, isProjSponsor: res.isProjSponsor?.value, approvalDetail }
      } else {
        const res = await Api.getBaseInfo({ id: contractId })
        this.setBizType(res.bizType)
        this.contractStatus = res.contractStatus
        this.contractProcessStatus = res.contractProcessStatus
        this.leaseTypes = res.leaseType
        return { detail: res, isProjSponsor: res.isProjSponsor, approvalDetail }
      }
    },
  })

  approvalLoading = false
  baseForm = new FormStore({})
  handleOpen = async () => {
    const id = this.page.getParams().contractId
    const data = await priceApi.postIRRCalculate({ id })
    return data
  }
  irrIsChange = false
  setIrrIsChange = (val) => {
    this.irrIsChange = val
  }
  irr = ''
  irrChange = async (value) => {
    const irr = this.baseForm.getFieldValue('irr')
    if (irr === this.irr) return
    this.irr = irr
    const contractId = this.page.getParams().contractId
    const irrPercent = (irr * 10000).toFixed(0)
    console.log('irrPercent: ', irrPercent)
    const params = {
      contractId,
      irrPercent,
    }
    await Api.priceIrrSave(params)
  }
  // 提交审核
  firstSubmitRet
  firstSubmitParams
  /* eslint-disable */

  submitApproval = async ({ id, type, onlyCheck, extParams, baoJiaRef }) => {
    const { baseEdit, dataSource } = baoJiaRef
    if (baseEdit) {
      message.info('报价方案未保存，请先保存后提交审批！')
      return
    }
    const { applyCreditAmount, contractAmount } = dataSource
    const { remainAvailableQuota } = this.page.getData().detail

    const params = {
      contractId: id,
      changeType: 'OTHER',
      onlyCheck,
      ...extParams,
    }
    if (type) {
      // 变更类型 other
      if (onlyCheck) {
        const res = await Api.submitChangeFlow(params)
        return res
      }
      await Api.submitChangeFlow(params)
      await this.page.init()
      message.success('提交成功')
    } else {
      await Api.submitApproval({ contractId: id, onlyCheck: true })
      const needConfirm =
        (['ZZ', 'ZL'].includes(this.bizType) && applyCreditAmount > remainAvailableQuota) ||
        (['ZR', 'BL'].includes(this.bizType) && contractAmount > remainAvailableQuota)
      await validateModal(
        { title: '合同金额已超出该项目剩余可用额度，是否继续提交审批？' },
        needConfirm
      )
      this.remarkModal.open()
    }
  }
  remarkModal = new ModalStore({
    onFinish: async ({ remark }) => {
      const contractId = this.page.getParams().contractId
      await Api.submitApproval({ contractId, remark })
      await this.page.init()
      message.success('提交成功')
      this.remarkModal.close()
    },
  })

  saveBaseInfo = async (params) => {
    const detail = this.isFormApproval ? this.page.getData().newDetail : this.page.getData().detail
    await Api.postBaseInfoModify({
      ...detail,
      ...params,
      id: this.page.getParams().contractId,
    })
    this.setBaseInfoShowValue(true)
    // message.success('保存成功')
    this.page.init()
  }
  saveChangeRemark = async (params) => {
    await Api.postChangeremarkModify({
      ...params,
      contractId: this.page.getParams().contractId,
    })
    await this.page.init()
  }

  // 变更日志
  changeLog = async (id) => {
    history.push(`/contract/list/detail/log/${id}?bizType=${this.bizType}`)
  }
  // 其他-取消变更
  cancelFlow = async () => {
    Modal.confirm({
      title: `是否取消操作？`,
      onOk: async () => {
        await Api.cancelFlow({
          contractId: this.page.getParams().contractId,
          changeType: 'OTHER',
        })
        message.success('操作成功')
        setTimeout(() => {
          history.push(`/contract/list`)
        }, 500)
      },
    })
  }
}
export default Store
