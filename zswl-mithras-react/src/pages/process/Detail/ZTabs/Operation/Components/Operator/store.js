import flowList from '@/api/common/flowList'
import publicInfoApi_edited from '@/api/cpm/payment/publicInfoApi_edited'
import customerRatApi from '@/api/customer/customerRat/customerRatApi'
import PaymentApplicationDetailApi from '@/api/cpm/payment/paymentApplicationDetail'
import { customerRatingUploadFile as uploadFile } from '@/components/Customer/CustomerEntries'
import { getApprovalText, indexCheck } from '@/utils/customerRat'
import OtherFilingMaterialsDetailApi from '@/api/fillingMaterials/otherFilingMaterialsDetail'
import FinancialReportStatisticsApi from '@/api/project/projReviewFinancialReport'
import { history, makeAutoObservable } from '@zswl/admin'
import { FormStore, Modal, ModalStore } from '@zswl/components'
import { message } from 'antd'
import BcxxStore from '../CompleteOperation/store'
import Api from '@/api/process/flowExecution'
import { getBackEndData } from './utils'
import { checkIrr } from '@/components/Contract/StartRentDetail'
class Store {
  constructor() {
    makeAutoObservable(this)
  }
  publicInfoRef = undefined
  pathname = '/process/receive'
  detailData = {}
  operatorForm = new FormStore({})
  clearCsr = (ccTabReadOnlyFlag, ccUerList) => ccTabReadOnlyFlag && ccUerList && this.operatorForm.setFieldsValue({ ccUserIdList: [] })
  recoverCsr = (ccTabReadOnlyFlag, ccUerList) => ccTabReadOnlyFlag && ccUerList && this.operatorForm.setFieldsValue({ ccUserIdList: ccUerList })
  //取消
  cancelProcess = async () => {
    const formData = this.operatorForm.getFieldValue()
    const { processInstanceId, modelKey, businessKey, ccTabReadOnlyFlag, ccUerList } = this.detailData
    Modal.confirm({
      title: '确认取消吗？',
      onOk: async () => {
        if (modelKey === 'PaymentActualDetailFlow') {
          const res = await Api.postPayMentCloseBeforeCheck({ id: businessKey })
          res.msg && message.info(res.msg)
        }
        this.clearCsr(ccTabReadOnlyFlag, ccUerList)
        try {
          await Api.cancelProcess({
            processInstanceId,
            ...formData,
            ccUserIdList: ccTabReadOnlyFlag && ccUerList ? [] : formData.ccUserIdList,
            scene: 1,
          })
          message.success('取消成功！')
          history.push(this.pathname)
        } catch (error) {
          this.recoverCsr(ccTabReadOnlyFlag, ccUerList)
        }
      },
    })
  }
  messageModal = new ModalStore({
    onFinish: async (values) => {
      console.log('values: ', values)
      const params = this.messageModal.getInitialValues()
      const { message } = values
      this.passProcess({ ...params, needConfirm: false, extParams: { message } })
    },
  })
  // 放款审核:付款申请距离最新评审流程（如有变更取评审变更）审批通过日期超 6 个月
  paymentTimeOutCheck = async ({ businessKey, params }) => {
    const result = await PaymentApplicationDetailApi.paymentTimeOutCheck({
      id: businessKey,
    })
    if (result === 1) {
      this.checkPaymentTimeOut.open({
        submit: () => {
          let { message } = this.operatorForm.getFieldValue()
          const text = `距离最新项目评审流程审批通过日期已超过6个月，已上传补充报告。\n`
          const reg = new RegExp(text, 'g')
          message = message ? message?.replace(reg, '') : ''
          this.messageModal.open({
            ...params,
            message: `${text}` + message,
          })
        },
      })
      return true
    }
    return false
  }
  publicModal = new ModalStore({})
  checkPublic = async (afterFunc) => {
    const { processInstanceId, modelKey, businessKey: paymentId } = this.detailData
    const customerList = await Api.publicCheck({ paymentId })
    if (customerList.length) {
      this.publicModal.open({ customerList })
      return
    }
    afterFunc?.()
  }
  // 二次确认
  beforePassProcess = async (params) => {
    const { dynamicFormKeyList, businessKey, modelKey, curTaskActivityIds, taskActivityId, clientId } = this.detailData
    const formData = await this.operatorForm.validateFields()
    const needCheckIrr = ['ContractStartRentFlow',"ContractAddNewReceiptFlow"].includes(modelKey)
    if (needCheckIrr) {
      await checkIrr({ contractId: businessKey },false)
    }
    if (modelKey === 'PaymentCreateFlow' && taskActivityId === 'userTask_projectmanager') {
      const paymentResult = await PaymentApplicationDetailApi.postPaymentFinanceCheckClientOpinion({
        id: businessKey,
      })
      if (!paymentResult) {
        Modal.confirm({
          title: '当前合同的【项目利润分配】流程暂未审批通过，请通过后再提交！',
        })
        return
      }
    }
    // 付款申请，舆情警告
    if (dynamicFormKeyList.includes('payment_clientOpinionNotice') || (modelKey === 'PaymentCreateFlow' && curTaskActivityIds === 'userTask_startUser')) {
      const result = await PaymentApplicationDetailApi.getMonitorCount({
        clientId,
      })
      if (result > 0) {
        Modal.confirm({
          title: '该合同存在未处理完成的舆情信息，请关注舆情处理流程进度！',
          onOk: async () => {
            if (dynamicFormKeyList.includes('payment_checkProjreviewTimeout')) {
              // 放款审核:付款申请距离最新评审流程（如有变更取评审变更）审批通过日期超 6 个月
              const isTimeout = await this.paymentTimeOutCheck({ businessKey, params })
              if (isTimeout) return
            }
            this.passProcess(params)
          },
        })
        return
      }
    }
    const validateDataList = ['userTask_otherInitReview', 'userTask_otherReview02', 'userTask_otherReview', 'userTask_otheryyglbfzr'
    ]
    const validateData = validateDataList.includes(curTaskActivityIds) || validateDataList.includes(taskActivityId)
    if (modelKey === 'OtherFilingMaterialsApplyFlow' && validateData) {
      const result = await OtherFilingMaterialsDetailApi.checkMaterialsDesc({
        id: businessKey,
      })
      if (!result) return
    }
    if (dynamicFormKeyList.includes('contract_early_repay_financial_confirm')) {
      //合同变更-提前还款流程财务确认节点增加动态表单
      const { isPass } = formData
      if (isPass) {
        Modal.confirm({
          title: '请确认提前还款金额已到账后，再点击【同意】操作！',
          onOk: async () => {
            this.passProcess({ ...params })
          },
        })
        return
      }
    }
    if (dynamicFormKeyList.includes('payment_checkApproveAmount')) {
      //付款申请-运营经办/复核提交流程时
      const result = await Api.postPayMentCheckApplyAmount({
        paymentId: businessKey,
      })
      if (result.isOverApprovedAmount || result.isOverContractAmount) {
        Modal.confirm({
          title: result.isOverContractAmount ? '付款申请金额已超出合同金额，是否继续提交？' : '付款申请金额已超出项目批复金额，是否继续提交？',
          onOk: async () => {
            this.passProcess({ ...params })
          },
        })
        return
      }
    }
    if (dynamicFormKeyList.includes('projReview_checkCorpSubjectItem')) {
      //风控经理:评审流程提交时检验客户管理模块财务报表录入是否完整
      const result = await FinancialReportStatisticsApi.checkResult({
        id: businessKey,
      })
      if (result.checkResult === 0) {
        this.checkCorpSubjectModal.open({
          submit: () => this.passProcess({ ...params, needConfirm: false }),
        })
        return
      }
    }
    // 放款审核:付款申请距离最新评审流程（如有变更取评审变更）审批通过日期超 6 个月
    if (dynamicFormKeyList.includes('payment_checkProjreviewTimeout')) {
      const isTimeout = await this.paymentTimeOutCheck({ businessKey, params })
      if (isTimeout) return
    }
    this.passProcess(params)
  }
  afterPassProcess = () => {
    const finalAction = () => {
      message.success('执行成功！')
      history.push(this.pathname)
    }
    // 合同起租、付款核销流程，财务经理岗提示
    const { dynamicFormKeyList } = this.detailData
    if (dynamicFormKeyList.includes('notice_addContractCode')) {
      Modal.info({
        title: `请至苍穹财务系统添加该合同编号，否则财务系统无法起租。`,
        onOk: () => {
          finalAction()
        },
      })
      return
    }
    finalAction()
  }
  // 租赁物退回按钮
  returnBack = async ({ groupContentStr = [], ...params }) => {
    const formData = this.operatorForm.getFieldValue()
    const { ccUserIdList, message: approveMsg, attentionFlag, ...rest } = formData
    const { ccTabReadOnlyFlag, ccUerList } = this.detailData
    const newMessage = groupContentStr.join('') + (approveMsg ? `<div>审批意见: ${approveMsg}</div>` : '')
    const newParams = {
      ...params,
      ccUserIdList: ccTabReadOnlyFlag && ccUerList ? [] : formData.ccUserIdList,
      message: newMessage,
      attentionFlag,
    }
    await flowList.randomReturn(newParams)
    message.success('执行成功！')
    history.push(this.pathname)
  }
  validPublicInfo = async () => {
    const { processInstanceId, mainModule, taskActivityId } = this.detailData
    if (taskActivityId === 'userTask_projectmanager' && mainModule === 'PAYMENT') {
      const res = await publicInfoApi_edited.postSubmitCheck({ processInstanceId })
      if (!res) {
        return new Promise((resolve, reject) => {
          Modal.confirm({
            title: '提示',
            content: '公开信息存在异常情况，请点击公开信息查询报告填写解释说明后提交！',
            cancelText: '确定',
            okText: '公开信息查询报告',
            onOk: () => {
              this.publicInfoRef.current.searchPublicInfo()
            },
          })
          return reject()
        })
      }
    }
  }
  passProcess = async ({ buttonKey, onlyCheck, extParams = {}, needConfirm = true }) => {
    const { taskId, curTaskIds, dynamicFormKeyList, mainModule, taskActivityId, businessKey: id, modelKey } = this.detailData
    const formData = await this.operatorForm.validateFields()
    const { ccUserIdList, message: approveMsg, attentionFlag, ...rest } = formData
    const bcxxFormData = await BcxxStore.completeOperation.validateFields()
    let title = '确认执行吗？'
    if (buttonKey == 'VOTE_DISAGREE') {
      title = '确认拒绝吗？'
    }
    let backEndData = { taskId, buttonKey, ccUserIdList, message: approveMsg, attentionFlag, dynamicFormData: {}, ...extParams }
    // 我发起的页面，任务ID
    if (this.pathname === '/process/application') {
      backEndData.taskId = taskId || curTaskIds
    }
    getBackEndData({ dynamicFormKeyList, formData,backEndData, bcxxFormData, dynamicFormData: this.detailData.dynamicFormData,
    })
    const isRatRisk = mainModule === 'RATING_CLIENT' && taskActivityId === 'userTask_riskManager'

    if (isRatRisk) {
      await indexCheck({ id })
    }
    if (buttonKey == 'VOTE_CONDITION_AGREE' || buttonKey == 'VOTE_DISAGREE') {
      if (!formData.message) {
        message.info('请输入相应的审批意见！')
        return
      }
    }
    await this.validPublicInfo()
    if (needConfirm) {
      Modal.confirm({
        title,
        onOk: async () => {
          await Api.passProcess(backEndData)
          this.afterPassProcess()
        },
      })
    } else {
      await Api.passProcess(backEndData)
      this.afterPassProcess()
    }
  }
  //撤回任务
  withdrawTask = async () => {
    const formData = this.operatorForm.getFieldValue()
    const { taskId, ccTabReadOnlyFlag, ccUerList } = this.detailData
    Modal.confirm({
      title: '确认撤回吗？',
      onOk: async () => {
        this.clearCsr(ccTabReadOnlyFlag, ccUerList)
        try {
          await Api.withdrawTask({ taskId, ...formData, ccUserIdList: ccTabReadOnlyFlag && ccUerList ? [] : formData.ccUserIdList, scene: 1 })
          message.success('执行成功！')
          history.push(this.pathname)
        } catch (error) {
          this.recoverCsr(ccTabReadOnlyFlag, ccUerList)
        }
      },
    })
  }
  //拒绝
  reject = async () => {
    const formData = this.operatorForm.getFieldValue()
    const { taskId, ccUerList, ccTabReadOnlyFlag } = this.detailData
    Modal.confirm({
      title: '确认拒绝吗？',
      onOk: async () => {
        this.clearCsr(ccTabReadOnlyFlag, ccUerList)
        try {
          await Api.reject({
            taskId,
            ...formData,
            ccUserIdList: ccTabReadOnlyFlag && ccUerList ? [] : formData.ccUserIdList,
          })
          message.success('执行成功！')
          history.push(this.pathname)
        } catch (error) {
          this.recoverCsr(ccTabReadOnlyFlag, ccUerList)
        }
      },
    })
  }
  // 撤回到发起人
  withdrawToStartUser = async () => {
    const formData = this.operatorForm.getFieldValue()
    const { processInstanceId, ccTabReadOnlyFlag, ccUerList } = this.detailData
    Modal.confirm({
      title: '确认执行吗？',
      onOk: async () => {
        this.clearCsr(ccTabReadOnlyFlag, ccUerList)
        try {
          await Api.withdrawToStartUser({
            processInstanceId,
            ...formData,
            ccUserIdList: ccTabReadOnlyFlag && ccUerList ? [] : formData.ccUserIdList,
          })
          message.success('执行成功！')
          history.push(this.pathname)
        } catch (error) {
          this.recoverCsr(ccTabReadOnlyFlag, ccUerList)
        }
      },
    })
  }
  //退回到指定节点
  backToStep = async ({ activityId, buttonKey }) => {
    const formData = this.operatorForm.getFieldValue()
    const { taskId, ccTabReadOnlyFlag, ccUerList } = this.detailData
    let title = '确认要执行吗？'
    if (buttonKey == 'ZL_PR_RECONSIDER') {
      title = '是否确定复议？'
    }
    Modal.confirm({
      title,
      onOk: async () => {
        this.clearCsr(ccTabReadOnlyFlag, ccUerList)
        try {
          await Api.backToStep({ taskId, buttonKey, activityId, ...formData, ccUserIdList: ccTabReadOnlyFlag && ccUerList ? [] : formData.ccUserIdList })
          message.success('执行成功！')
          history.push(this.pathname)
        } catch (error) {
          this.recoverCsr(ccTabReadOnlyFlag, ccUerList)
        }
      },
    })
  }
  //协同
  collaborate = async ({ collaborateUserId }) => {
    const formData = this.operatorForm.getFieldValue()
    const { taskId, ccTabReadOnlyFlag, ccUerList } = this.detailData
    Modal.confirm({
      title: '确认执行吗？',
      onOk: async () => {
        await Api.collaborate({
          taskId,
          collaborateUserId,
          ...formData,
          ccUserIdList: ccTabReadOnlyFlag && ccUerList ? [] : formData.ccUserIdList,
        })
        message.success('执行成功！')
        history.push(this.pathname)
      },
    })
  }
  //驳回到发起人
  backToStartUser = async (s, f, buttonKey) => {
    const formData = this.operatorForm.getFieldValue()
    const { taskId, dynamicFormKeyList, taskActivityId, mainModule, businessKey: id, ccTabReadOnlyFlag, ccUerList } = this.detailData
    const bcxxFormData = BcxxStore.completeOperation.getFieldValue()
    Modal.confirm({
      title: '确认执行吗？',
      onOk: async () => {
        if (f == 1) {
          const isRatRisk = mainModule === 'RATING_CLIENT' && taskActivityId === 'userTask_riskManager'
          if (isRatRisk) {
            const text = await getApprovalText({ id })
            const message = formData.message ? `<div>${formData?.message}</div>${text}` : text
            formData.message = message
          }
          this.clearCsr(ccTabReadOnlyFlag, ccUerList)
          try {
            await Api.backToStartUser({ backType: s.key, taskId, ...formData, ccUserIdList: ccTabReadOnlyFlag && ccUerList ? [] : formData.ccUserIdList })
            message.success('执行成功！')
            history.push(this.pathname)
          } catch (error) {
            this.recoverCsr(ccTabReadOnlyFlag, ccUerList)
          }
        } else {
          if (dynamicFormKeyList.length > 0 && dynamicFormKeyList.includes('projReview_setProjectClassify')) {
            if (!bcxxFormData?.projectClassify) {
              message.info('请选择项目分类！')
              return
            }
            formData.dynamicFormData = {
              projReview_setProjectClassify: { projectClassify: bcxxFormData?.projectClassify },
            }
          }
          if (ccTabReadOnlyFlag && ccUerList) {
            this.operatorForm.setFieldsValue({ ccUserIdList: [] })
          }
          try {
            await Api.backToStartUser({ taskId, ...formData, buttonKey, ccUserIdList: ccTabReadOnlyFlag && ccUerList ? [] : formData.ccUserIdList })
            message.success('执行成功！')
            history.push(this.pathname)
          } catch (error) {
            if (ccTabReadOnlyFlag && ccUerList) {
              this.operatorForm.setFieldsValue({
                ccUserIdList: ccUerList,
              })
            }
          }
        }
      },
    })
  }
  // 风控经理:评审流程提交时检验客户管理模块财务报表录入是否完整
  checkCorpSubjectMethods = {}
  checkCorpSubjectModal = new ModalStore({
    onOpen: async (params) => {
      this.checkCorpSubjectMethods = { ...params }
    },
  })
  checkPaymentTimeOutMethods = {}
  checkPaymentTimeOut = new ModalStore({
    onOpen: async (params) => (this.checkPaymentTimeOutMethods = { ...params }),
  })
  // 转办
  transferModal = new ModalStore({
    onOpen: async () => { },
    onFinish: async (values) => {
      const { ccTabReadOnlyFlag, ccUerList } = this.detailData
      ccTabReadOnlyFlag &&
        ccUerList &&
        this.operatorForm.setFieldsValue({
          ccUserIdList: [],
        })
      await Api.transfer({
        processInstanceId: this.detailData.processInstanceId,
        employeeId: values?.user,
      })
      this.transferModal.close()
      message.success('执行成功！')
      history.push(this.pathname)
    },
  })
  overturnModal = new ModalStore({
    // 推翻评级
    onOpen: async () => {
      const { businessKey: id } = this.detailData
      const res = await customerRatApi.postClientDetail({ id })
      const { finalScoreValue, finalScore, ...rest } = res
      return { finalScore: finalScoreValue, ...rest }
    },
    onFinish: async (values) => {
      const { taskActivityId, businessKey: id, ccTabReadOnlyFlag, ccUerList } = this.detailData
      // 风控经理复核 用 SUBMIt 下迁用FOLLOWING
      const buttonKey = taskActivityId === 'userTask_riskManager_fuhe' ? 'SUBMIT' : 'FOLLOWING'
      this.operatorForm.setFieldsValue({ message: values.overturnOpinion })
      const formData = await this.operatorForm.validateFields()
      const { message: overturnOpinion } = formData
      ccTabReadOnlyFlag &&
        ccUerList &&
        this.operatorForm.setFieldsValue({
          ccUserIdList: [],
        })
      await indexCheck({ id })
      await customerRatApi.postClientOverturn({ id, overturnOpinion, ...values })
      await uploadFile(values.files, {
        moduleType: 'RATING_CLIENT',
        mainId: id,
        materialsType: 'RATING_CLIENT_SUPPLEMENT_FILE',
      })
      await this.passProcess({ buttonKey, needConfirm: false })
      this.overturnModal.close()
    },
  })
  overturn = () => { }
}
export default new Store()
