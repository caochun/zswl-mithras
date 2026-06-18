import { http } from '@zswl/admin'

const mock = false

export default {
  // 文件列表分组比对
  postListGroupCompare: (data: any): Promise<any> =>
    http.post('/file/list/group/compare/v2', data, { mock }),

  // 文件列表比对
  postFileListCompare: (data: any): Promise<any> =>
    http.post('/file/list/compare', data, { mock }),
}
