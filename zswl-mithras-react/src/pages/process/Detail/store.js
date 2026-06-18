import { makeAutoObservable, getQuery, history } from '@zswl/admin'
import { TableStore, PageStore } from '@zswl/components'
import { debounce as _debounce } from 'lodash'
import meetingApi from '@/api/process/detail/projectReviewMeetingMinuteApi'
import Api from './api'
import { Modal, message } from 'antd'

const businessID = getQuery('businessKey')
class Store {
  constructor() {
    makeAutoObservable(this, { clientId: false })
  }
  isMeetingShowFlow = false
  showMinutesOfReviewMeeting = async ({ projReviewId, projFlowId, modelKey }) => {
    const isShowFlow = [
      'ProjReviewCreateFlow',
      'ProjReviewModifyFlow',
      'GroupCreditReviewCreateFlow',
      'GroupCreditReviewModifyFlow',
    ].includes(modelKey)
    const isReview = ['ProjReviewCreateFlow', 'ProjReviewModifyFlow'].includes(modelKey)
    if (isShowFlow) {
      const functionCode = isReview
        ? 'projReviewMeetMinuteBaseInfoDetail'
        : 'groupCreditReviewMeetMinuteBaseInfoDetail'
      const projReviewType = isReview ? 'PROJ_REVIEW_BASE' : 'GROUP_CREDIT_REVIEW'
      const res = await meetingApi.postInfoDetail(
        {
          projReviewId,
          projFlowId,
          projReviewType: projReviewType,
        },
        functionCode
      )
      this.isMeetingShowFlow = !!res
    }
  }
  detailData = {}
  queryDetail = async (id, diff) => {
    if (diff === 'processInstanceId') {
      this.detailData = await Api.processDetail({ processInstanceId: id })
    } else {
      this.detailData = await Api.taskDetail({ taskId: id })
      if(this.detailData.returnMsg=="银行贷款资金复核前请通知银行"&&this.detailData.taskName=="出纳"){
        Modal.confirm({
            title: '提示',
            content: this.detailData.returnMsg,
        })
      }
    }
    const { businessKey: projReviewId, processInstanceId: projFlowId, modelKey } = this.detailData
    await this.showMinutesOfReviewMeeting({ projReviewId, projFlowId, modelKey })
  }

  page = new PageStore({})
}
export default Store
