import { http } from '@zswl/admin'

export default {
  workbenchAnnouncementTop: (params) => http.post('/workbench/announcement/top', params, {}),
  workbenchAnnouncementDetail: (params) => http.post('/workbench/announcement/detail', params, {}),
  workbenchAnnouncementRemove: (params) => http.post('/workbench/announcement/remove', params, {}),
  workbenchAnnouncementList: (params) => http.post('/workbench/announcement/list', params, {}),
  workbenchAnnouncementModify: (params) => http.post('/workbench/announcement/modify', params, {}),
  workbenchAnnouncementAdd: (params) => http.post('/workbench/announcement/add', params, {}),
}
