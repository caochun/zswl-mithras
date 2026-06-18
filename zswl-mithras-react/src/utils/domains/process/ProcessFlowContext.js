import { boolean } from 'mathjs'
import { createContext, useContext } from 'react'

export const FlowDataContext = createContext({
  // 注册回调函数的方法
  registerCallback: (callback) => {},
  // 触发回调函数的方法，返回boolean决定是否阻断
  triggerCallback: (params) => boolean,
})

export function FlowDataProvider({ value, children }) {
  return <FlowDataContext.Provider value={value}>{children}</FlowDataContext.Provider>
}

export function useFlowData() {
  const ctxValue = useContext(FlowDataContext)
  return {
    ...ctxValue,
  }
}

// 补充信息，文件上传的配置 fileKeyEnum
export const fileKeyEnum = {
  projEstablish_setRiskFile: { label: '立项会会议纪要', materialsType: 'MEETING_MINUTES' },
  projEstablish_setRiskFileOther: { label: '其他', materialsType: 'OTHER' },
  projReview_setLawFile: { label: '法律合规意见书', materialsType: 'LEGAL_COMPLIANCE_REPORT' },
  projReview_setRiskFile: { label: '审查报告', materialsType: 'RISK_REVIEW_REPORT' },
  projReview_setFinanceFile: {
    label: '项目收益率审查意见书',
    materialsType: 'YIELD_REVIEW_REPORT',
  },
  projReview_setMeetingFile: { label: '评审会会议纪要', materialsType: 'MEETING_REVIEW_REPORT' },
  projReview_setFullCommitteeFile: {
    label: '专职评审意见',
    materialsType: 'PROFESSIONAL_REVIEW_COMMENTS',
  },
  payment_setLoanApprovalFile: { label: '审核材料', materialsType: 'LOAN_APPROVAL' },
  adjust_setRiskFile: { label: '审查报告', materialsType: 'RISK_REVIEW_REPORT' },
  adjust_setLawFile: { label: '法律合规意见书', materialsType: 'LEGAL_COMPLIANCE_REPORT' },
  adjust_setMeetingFile: { label: '评审会会议纪要', materialsType: 'MEETING_REVIEW_REPORT' },
  adjust_setMeetingRecord: { label: '评审会会议记录', materialsType: 'MEETING_REVIEW_RECORD' },

  projReview_setMeetingRecord: { label: '评审会会议记录', materialsType: 'MEETING_REVIEW_RECORD' },

  projReview_setPricingMeetingMinutes: {
    label: '总经办秘书上传会议纪要',
    materialsType: 'GMO_MEETING_MINUTES',
  },
  projReview_setDirectorMeetingFile: {
    label: '董事会会议纪要',
    materialsType: 'DIRECTOR_MEETING_REPORT',
  },
  ftp_setMeetingFile: {
    label: '会议纪要',
    rules: [{ required: true }],
    materialsType: 'MEETING_FILE',
  },
  ftp_setSupplement: {
    label: '补充材料',
    rules: [{ required: true }],
    materialsType: 'SUPPLEMENT',
  },
  projReview_setPricingMeetingFile: {
    label: '业务定价会议纪要',
    rules: [{ required: true }],
    materialsType: 'BUSINESS_PRICING_APPROVAL_MEETING_REPORT',
  },
  ratingClient_reviewSetFile: {
    label: '补充说明资料',
    materialsType: 'RATING_CLIENT_SUPPLEMENT_FILE',
  },
}

// 补充信息, 有这个模块的key，dynamicFormKeyList
export const complementListExtra = [
  // 风控经理
  'projEstablish_setRiskManager',
  // 项目分类
  'projReview_setProjectClassify',
  'follow_up_rental_inspection_form',
]

// 流程展示，有投票结果的key，dynamicFormKeyList
export const votingResultsList = [
  'adjust_showJudgesVotingResults',
  'projReview_showJudgesVotingResults',
  'projReview_showDirectorsVotingResults',
  'projReview_showMeetingVotingResults',
  'ftp_showVotingResults',
  'projReview_pricingShowVoteResult',
]
