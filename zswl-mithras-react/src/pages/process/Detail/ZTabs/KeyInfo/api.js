import { http } from '@zswl/admin'

export default {
  // 获取审批流业务重要文件
  getApprovalFile: (data) => http.post('/flow/file/important/list', data),
  //获取审批流会议决议文件
  getApprovalMeetingFile: (data) => http.post('/flow/file/decision/list', data),
}
