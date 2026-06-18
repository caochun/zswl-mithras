/* prettier-ignore-start */
import * as Types from './interface/warehouseTaskApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改黑灰名单任务表
  postTaskModify: (data: Types.TaskModifyRequest): Promise<Types.TaskModifyResponse> =>
    http.post('/black/gray/warehouse/task/modify', data, { mock }),

  // 删除黑灰名单任务表
  postTaskRemove: (data: Types.TaskRemoveRequest): Promise<Types.TaskRemoveResponse> =>
    http.post('/black/gray/warehouse/task/remove', data, { mock }),

  // 新增黑灰名单任务表
  postTaskAdd: (data: Types.TaskAddRequest): Promise<Types.TaskAddResponse> =>
    http.post('/black/gray/warehouse/task/add', data, { mock }),

  // 黑灰名单任务表列表
  postTaskList: (data: Types.TaskListRequest): Promise<Types.TaskListResponse> =>
    http.post('/black/gray/warehouse/task/list', data, { mock }),

  // 黑灰名单任务表详情
  postTaskDetail: (data: Types.TaskDetailRequest): Promise<Types.TaskDetailResponse> =>
    http.post('/black/gray/warehouse/task/detail', data, { mock }),
}

/* prettier-ignore-end */
