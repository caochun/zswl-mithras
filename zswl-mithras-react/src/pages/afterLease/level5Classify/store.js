import { InitProcessData } from '@/components/AfterLease/Level5ClassifyConfig'
import { getKeyOptionsLabelMapPlus } from '@/utils'
import { makeAutoObservable } from '@zswl/admin'
import { Modal, ModalStore, PageStore, TableStore } from '@zswl/components'
import { message } from 'antd'
import { unionBy } from 'lodash'
import Api from './api'

class Store {
  constructor({ quarter, year, id, businessVersion, modelKey }) {
    this.quarter = quarter
    this.year = year
    this.assetClassifyId = id
    this.businessVersion = businessVersion
    this.modelKey = modelKey
    makeAutoObservable(this)
  }

  page = new PageStore({
    request: async (params) => {
      const { isFormApproval, year, quarter } = params
      this.year = year
      this.quarter = quarter
      if (!isFormApproval) {
        this.getQuarterSelectData()
      }
    },
  })

  exportSummaryFile = async () => {
    await Api.postExportSummaryFile({
      id: this.assetClassifyId,
    })
  }

  formarQuarterData = (data) => {
    data = Array.isArray(data) ? data : []
    const QUARTER_COUNT = 4

    let defaultData = new Array(QUARTER_COUNT)
      .fill({ classificationAmounts: [] })
      .map((item, index) => {
        return {
          ...item,
          quarter: index + 1,
        }
      })

    const resultData = unionBy(data, defaultData, 'quarter')

    data = resultData.sort((a, b) => a?.quarter - b?.quarter)
    const result = []
    data.forEach((item) => {
      let classificationAmounts = []
      classificationAmounts = item.classificationAmounts?.map((i) => {
        return {
          name: getKeyOptionsLabelMapPlus('assetClassifyResultEnum')[i.classifyResult],
          value: i.classifyAmount,
        }
      })
      result.push({
        ...item,
        classificationAmounts,
      })
    })
    return result
  }

  // 季度选择
  changeQuarter = (q) => {
    this.quarter = q
    this.residueWorkday = null
    this.onQuarterChange()
  }
  // 年选择
  changeYear = (y) => {
    this.year = y
    this.getQuarterSelectData()
  }
  // 季度选择区
  quarterSelectData = []
  getQuarterSelectData = async () => {
    const res = await Api.getQuarterSelect({ year: this.year }).finally(() => {})
    const formatData = this.formarQuarterData(res)
    this.quarterSelectData = formatData
    this.onQuarterChange()
  }

  onQuarterChange = () => {
    if (this.quarterSelectData?.length > 0) {
      const quarterData = this.quarterSelectData.filter((item) => item.quarter === this.quarter)[0]
      const assetClassifyId = quarterData?.id
      if (!assetClassifyId) {
        this.processData = InitProcessData
        this.rawProcessData = InitProcessData
        this.assetClassifyId = null
        this.residueWorkday = null
        this.initType = ''
        this.midInitStatue = ''
        this.assetClassifyInitType = ''
        this.table.search()
        return
      }
      this.assetClassifyId = assetClassifyId
      this.initType = quarterData?.initType || ''
      this.midInitStatue = quarterData?.midInitStatue || ''
      this.getProcessData()
      this.getWorkDay()
      this.table.search()
    } else {
      this.assetClassifyId = null
      this.processData = InitProcessData
      this.rawProcessData = InitProcessData
      this.initType = ''
      this.midInitStatue = ''
      this.assetClassifyInitType = ''
    }
  }

  assetClassifyId
  assetClassifyStatus = 0
  processDataLoading = false
  processData = []
  rawProcessData = [] // 原始流程数据，用于按钮状态判断
  initType = ''
  midInitStatue = ''
  assetClassifyInitType = '' // 从process接口获取的初分类型
  getProcessData = async () => {
    this.processDataLoading = true

    const res = await Api.getProcess({ assetClassifyId: this.assetClassifyId }).finally(() => {
      this.processDataLoading = false
    })
    this.assetClassifyStatus = res.assetClassifyStatus
    this.assetClassifyInitType = res.assetClassifyInitType || '' // 获取初分类型

    // 保存原始流程数据用于按钮状态判断
    const result = InitProcessData.map((item, index) => {
      return {
        ...item,
        nodeLable: item.nodeLable,
        ...res?.nodeMessages?.[index],
      }
    })
    this.rawProcessData = result

    // 如果发起了季中初分，步骤条始终显示默认状态，但保留原始数据用于按钮状态判断
    // 即使风委会完成后，进度条也不展示数据，直到重新发起季末初分
    if (this.assetClassifyInitType === 'QUARTER_MID') {
      this.processData = InitProcessData
      return
    }

    this.processData = result
  }
  residueWorkday
  getWorkDay = async () => {
    const res = await Api.getWorkDay({
      assetClassifyId: this.assetClassifyId,
    })
    this.residueWorkday = res?.residueWorkday
  }

  table = new TableStore({
    request: async (params) => {
      if (!this.assetClassifyId) {
        this.table.setList([])
        return {
          list: [],
          total: 0,
        }
      }
      return Api.list({
        ...params,
        boardMeeting: this.page.getParams().isFormApproval
          ? this.modelKey === 'AssetClassifyBoardFlow'
          : undefined,
        queryLatestVersionClassifyResult: this.page.getParams().isFormApproval ? true : undefined,
        businessVersion: this.businessVersion,
        assetClassifyId: this.assetClassifyId,
      })
    },
  })

  sponsor = async (record) => {
    if (['REVIEW_MEETING', 'RISK_MEETING'].includes(record.key)) {
      this.nodeItemRecord = record
      this.processDataModal.open()
    } else {
      Modal.confirm({
        title: `是否发起${record.nodeLable}评审？`,
        onOk: async () => {
          const { key } = record
          if (key === 'REVIEW') {
            await Api.submitReviewCheck({ id: this.assetClassifyId })
          }
          message.success('操作成功')
          this.getWorkDay()
          this.getProcessData()
        },
      })
    }
  }

  nodeItemRecord = {}
  // 选择人员、上传资料 弹窗
  submitProcessData = async () => {
    if (this.nodeItemRecord.key === 'REVIEW_MEETING') {
      await Api.submitReviewmeeting({ id: this.assetClassifyId })
    } else if (this.nodeItemRecord.key === 'RISK_MEETING') {
      await Api.submitRiskmeeting({ id: this.assetClassifyId })
    }
    message.success('操作成功')
    this.processDataModal.close()
    this.getWorkDay()
    this.getProcessData()
  }
  processDataModal = new ModalStore({
    onFinish: async () => {},
  })

  editModal = new ModalStore({
    onOpen: (record) => {
      return { classifyResult: record.suggestResult, id: record.id }
    },
    onFinish: async ({ classifyResult, id }) => {
      await Api.saveClassifyresult({
        classifyResult,
        id,
      })
      message.success('修改成功')
      this.editModal.close()
      this.table.search()
    },
  })
  //操作列的单个按钮删除
  remove = async ({ id }) => {
    Modal.confirm({
      title: '确认删除吗？',
      onOk: async () => {
        await Api.remove({ id, assetClassifyId: this.assetClassifyId })
        message.success('删除成功！')
        this.table.search()
      },
    })
  }

  firstPartModal = new ModalStore({
    onOpen: () => {
      this.detailVisible = false
      return this.firstPartList
    },
  })
  detailVisible = false
  submitFirstPart = (value) => {
    this.firstPartModal.close()
    // this.detailVisible = false
  }
  firstPartList
  firstPart = async (record) => {
    this.tipsModal.open()
    // this.firstPartModal.open()
    try {
      this.approvalLoading = true
      const params = { year: this.year, quarter: this.quarter }
      const data = await Api.firstPart(params)
      this.firstPartList = data
      this.approvalLoading = false
      message.success('操作成功！')
      // 重新获取季度数据，会更新 initType
      await this.getQuarterSelectData()
      this.tipsModal.close()
      this.firstPartModal.open()
      this.table.search()
    } catch (e) {
      this.tipsModal.close()
      this.approvalLoading = false
    }
  }
  approvalLoading = false
  tipsModal = new ModalStore({})
  submitApproval = async () => {
    this.firstPartModal.open()
  }

  get isQuarterEndFlow() {
    return this.initType === 'QUARTER_END'
  }

  get isQuarterMidFlow() {
    return this.initType === 'QUARTER_MID'
  }
  // 是否只显示默认的流程展示（如果发起了季中初分且风委会未完成，则不显示发起按钮等交互功能）
  // 当风委会（RISK_MEETING）的 nodeStatue 为 FINISH 时，允许重新发起季末或季中初分
  get isOnlyShowDefaultProcess() {
    const riskMeetingNode = this.rawProcessData?.[3] // 风委会节点
    return this.assetClassifyInitType === 'QUARTER_MID' && riskMeetingNode?.nodeStatue !== 'FINISH'
  }
  // 获取复核按钮文本
  get midSeasonReviewButtonText() {
    const reviewNode = this.rawProcessData?.[1] // 使用原始数据获取复核节点状态
    if (reviewNode?.nodeStatue === 'PROCESS') {
      return '季中复核审批中'
    }
    return '季中复核'
  }
  // 复核是否在审批中
  get isMidSeasonReviewInProgress() {
    const reviewNode = this.rawProcessData?.[1] // 复核节点
    return reviewNode?.nodeStatue === 'PROCESS'
  }
  // 季中复核是否可以点击（应用 showBtn 相同逻辑）
  get canClickMidSeasonReview() {
    // 流程已结束时不可点击
    if (this.assetClassifyStatus === 1) return false
    const initNode = this.rawProcessData?.[0] // 初分节点
    const reviewNode = this.rawProcessData?.[1] // 复核节点
    if (!initNode || !reviewNode) return false
    // 当前节点为 WAIT 且上一节点为 FINISH 时可点击
    return reviewNode.nodeStatue === 'WAIT' && initNode.nodeStatue === 'FINISH'
  }
  // 获取评审会按钮文本
  get midSeasonReviewMeetingButtonText() {
    const reviewMeetingNode = this.rawProcessData?.[2] // 使用原始数据获取评审会节点状态
    if (reviewMeetingNode?.nodeStatue === 'PROCESS') {
      return '评审会审批中'
    }
    return '季中评审会'
  }
  // 获取风委会按钮文本
  get midSeasonRiskMeetingButtonText() {
    const riskMeetingNode = this.rawProcessData?.[3] // 使用原始数据获取风委会节点状态
    if (riskMeetingNode?.nodeStatue === 'PROCESS') {
      return '风委会审批中'
    }
    return '季中风委会'
  }
  get canClickReviewMeeting() {
    // 评审会节点 (index=2)
    // 应用 showBtn 相同逻辑：当前节点为 WAIT，上一节点为 FINISH，流程未结束
    // 流程已结束时不可点击
    if (this.assetClassifyStatus === 1) return false
    const reviewNode = this.rawProcessData?.[1] // 复核节点
    const reviewMeetingNode = this.rawProcessData?.[2] // 评审会节点
    if (!reviewNode || !reviewMeetingNode) return false
    // 当前节点为 WAIT 且上一节点为 FINISH 时可点击
    return reviewMeetingNode.nodeStatue === 'WAIT' && reviewNode.nodeStatue === 'FINISH'
  }
  get canClickRiskMeeting() {
    // 风委会节点 (index=3)
    // 应用 showBtn 相同逻辑：当前节点为 WAIT，上一节点为 FINISH，流程未结束
    // 流程已结束时不可点击
    if (this.assetClassifyStatus === 1) return false
    const reviewMeetingNode = this.rawProcessData?.[2] // 评审会节点
    const riskMeetingNode = this.rawProcessData?.[3] // 风委会节点
    if (!reviewMeetingNode || !riskMeetingNode) return false
    // 当前节点为 WAIT 且上一节点为 FINISH 时可点击
    return riskMeetingNode.nodeStatue === 'WAIT' && reviewMeetingNode.nodeStatue === 'FINISH'
  }
  get canClickMidSeasonInit() {
    // 季末流程时不可点击季中初分
    return this.initType !== 'QUARTER_END'
  }
  // 季中初分是否禁用
  get isMidSeasonInitDisabled() {
    const riskMeetingNode = this.rawProcessData?.[3]
    // 当季中初分流程全部完成（风委会 FINISH）时，允许重新发起季中初分
    if (this.assetClassifyInitType === 'QUARTER_MID' && riskMeetingNode?.nodeStatue === 'FINISH') {
      return false
    }
    // 复核、评审会、风委会中任一完成时禁用（流程进行中）
    const reviewNode = this.rawProcessData?.[1]
    const reviewMeetingNode = this.rawProcessData?.[2]
    if (
      [reviewNode?.nodeStatue, reviewMeetingNode?.nodeStatue, riskMeetingNode?.nodeStatue].includes(
        'FINISH'
      )
    ) {
      return true
    }
    // 不满足季末初分的点击条件时也禁用
    return !this.canClickQuarterEndInit
  }
  get canClickQuarterEndInit() {
    // 不是季中流程时，可以点击
    if (this.initType !== 'QUARTER_MID') {
      return true
    }
    const riskMeetingNode = this.rawProcessData?.[3] // 风委会节点
    // 如果当前是季中初分流程且风委会已完成，允许发起季末初分
    // 这里直接用 assetClassifyInitType 和节点状态判断，避免依赖可能未更新的 midInitStatue
    if (this.assetClassifyInitType === 'QUARTER_MID' && riskMeetingNode?.nodeStatue === 'FINISH') {
      return true
    }
    // 季中流程进行中时不可点击季末初分
    if (this.midInitStatue === 'PROCESS') {
      return false
    }
    // 季中初分完成后，需要等待风委会审批通过才能发起季末
    if (this.midInitStatue === 'FINISH') {
      return riskMeetingNode?.nodeStatue === 'FINISH'
    }
    return true
  }
  get isMidSeasonFlowDisabled() {
    // 季末流程时禁用季中流程按钮
    return this.initType === 'QUARTER_END'
  }
  get isQuarterEndFlowDisabled() {
    // 不是季中流程时，不禁用
    if (this.initType !== 'QUARTER_MID') {
      return false
    }

    const riskMeetingNode = this.rawProcessData?.[3] // 风委会节点
    // 如果当前是季中初分流程且风委会已完成，不禁用季末初分按钮
    // 这里直接用 assetClassifyInitType 和节点状态判断，避免依赖可能未更新的 midInitStatue
    if (this.assetClassifyInitType === 'QUARTER_MID' && riskMeetingNode?.nodeStatue === 'FINISH') {
      return false
    }
    // 季中流程进行中时禁用季末流程按钮
    if (this.midInitStatue === 'PROCESS') {
      return true
    }
    // 季中初分完成后，需要等待风委会审批通过才能发起季末
    if (this.midInitStatue === 'FINISH') {
      return riskMeetingNode?.nodeStatue !== 'FINISH'
    }
    return false
  }
  clientSelectModal = new ModalStore({
    onOpen: () => {
      this.clientSelectTable.search()
    },
  })
  clientSelectTable = new TableStore({
    request: async (params) => {
      return Api.postClientList({
        ...params,
        year: this.year,
        quarter: this.quarter,
      })
    },
  })
  confirmClientSelect = async (selectedIds, selectedRows) => {
    try {
      this.approvalLoading = true
      const params = {
        year: this.year,
        quarter: this.quarter,
        clientIds: selectedIds,
        initType: 'QUARTER_MID',
      }
      const data = await Api.postMidQuarterDivision(params)
      this.firstPartList = data
      this.approvalLoading = false
      message.success('操作成功！')
      await this.getQuarterSelectData()
      this.clientSelectModal.close()
      this.firstPartModal.open()
      this.table.search()
    } catch (e) {
      this.approvalLoading = false
    }
  }
  midSeasonInitialDivision = async () => {
    this.clientSelectModal.open()
  }
  midSeasonReview = async () => {
    Modal.confirm({
      title: '是否发起季中复核评审？',
      onOk: async () => {
        await Api.submitReviewCheck({ id: this.assetClassifyId })
        message.success('操作成功')
        this.getWorkDay()
        this.getProcessData()
      },
    })
  }
  midSeasonReviewMeeting = async () => {
    this.nodeItemRecord = {
      key: 'REVIEW_MEETING',
      nodeLable: '评审会',
    }
    this.processDataModal.open()
  }
  midSeasonRiskMeeting = async () => {
    this.nodeItemRecord = {
      key: 'RISK_MEETING',
      nodeLable: '风委会',
    }
    this.processDataModal.open()
  }
}
export default Store
