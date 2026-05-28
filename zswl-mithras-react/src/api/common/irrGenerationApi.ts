/* prettier-ignore-start */
import * as Types from './interface/irrGenerationApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // irr计算
  postGenerationIrr: (data: Types.GenerationIrrRequest): Promise<Types.GenerationIrrResponse> =>
    http.post('/utils/cashFlow/generation/irr', data, { mock }),

  // irr详情预览文件地址获取
  postFileUrl: (data: Types.FileUrlRequest): Promise<Types.FileUrlResponse> =>
    http.post('/utils/cashFlow/generation/irr/file/url', data, { mock }),

  // 导入现金流计划表\/概算租金表
  postGenerationImport: (
    data: Types.GenerationImportRequest
  ): Promise<Types.GenerationImportResponse> =>
    http.post('/utils/cashFlow/generation/import', data, { mock, type: 'upload' }),

  // 导出现金流计划表\/概算租金表
  postGenerationExport: (
    data: Types.GenerationExportRequest,
    fileName?: string
  ): Promise<Types.GenerationExportResponse> =>
    http.post('/utils/cashFlow/generation/export', data, {
      mock,
      type: 'download',
      fileName,
    }),

  // 生成现金流计划表\/租金概算表
  postGenerationExecute: (
    data: Types.GenerationExecuteRequest
  ): Promise<Types.GenerationExecuteResponse> =>
    http.post('/utils/cashFlow/generation/execute', data, { mock }),
}

/* prettier-ignore-end */
