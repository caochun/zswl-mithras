import { http } from '@zswl/admin'
const mock = false

export default {
  // 修改项目评审会议纪要表-保存
  postInfoModify: (params,functionCode) => http.post('/proj/review/meet/minute/base/info/modify', params,
    {
      headers: {
        functionCode: functionCode || 'projReviewMeetMinuteBaseInfoModify'
      }
    }
  ),
  // 新增项目评审会议纪要表详情
  postInfoDetail: (params ,functionCode) => http.post('/proj/review/meet/minute/base/info/detail', params,
    {
      headers: {
        functionCode: functionCode || 'projReviewMeetMinuteBaseInfoDetail'
      }
    }
  ),
  // 项目评审会议纪要-获取交易结构用户
  postRelatedCustomers: (params,functionCode) => http.post('/proj/review/meet/minute/get/related/customers', params,
    {
      headers: {
        functionCode: functionCode||'projReviewMeetMinuteGetRelatedCustomers'
      }
    }
  ),
  // 修改项目评审会议纪要表-提交
  postInfoSubmit: (params,functionCode) => http.post('/proj/review/meet/minute/base/info/submit', params,
    {
      headers: {
        functionCode: functionCode||'projReviewMeetMinuteBaseInfoSubmit'
      }
    }
  ),
  // 项目评审会议纪要表-跟踪事项列表
  postTrackEventList: (params,functionCode) => http.post('/proj/review/meet/minute/trackEvent/list', params,
    {
      headers: {
        functionCode: functionCode||'projReviewMeetMinuteTrackEventList'
      }
    }
  ),
  // 删除跟踪事项列表
  getTrackEventClose:(params,functionCode) => http.get('/trackEvent/close', params,
    {
      headers: {
        functionCode: functionCode||'projReviewTrackEventClose'
      }
    }
  ),
  // 上传现金流计划表
  postCashflowplanUpload: (params,functionCode) => http.post('/proj/review/quotation/proposal/cashflowplan/upload', params,
    {
      mock,
      type: 'upload',
      headers: {
        functionCode: functionCode||'projReviewQuotationProposalCashflowplanUpload'
      }
    }
  ),
  // 获取现金流计划表
  postCashflowplanList: (params,functionCode) => http.post('/proj/review/quotation/proposal/cashflowplan/list', params,
    {
      mock,
      headers: {
        functionCode: functionCode||'projReviewQuotationProposalCashflowplanList'
      }
    }
  ),
  // 导出租金表
  postRentExport: (params,functionCode) => http.post('/proj/review/quotation/proposal/cashflowplan/rent/export', params,
    {
      mock,
      type: 'download',
      headers: {
        functionCode: functionCode||'projReviewQuotationProposalCashflowplanRentExport'
      }
    }),
  // 导出现金流表 
  postCashflowExport: (params,functionCode) => http.post('/proj/review/quotation/proposal/cashflowplan/cashflow/export', params,
    {
      mock,
      type: 'download',
      headers: {
        functionCode: functionCode||'selectorgs-groupCreditEstablish'
      }
    }
  ),

}
