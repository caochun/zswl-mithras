import { http } from '@zswl/admin'

const mock = false

export default {
  postGenerationIrr: (data: any): Promise<any> =>
    http.post('/utils/cashFlow/generation/irr', data, { mock }),
  postGenerationImport: (data: any): Promise<any> =>
    http.post('/utils/cashFlow/generation/import', data, { mock, type: 'upload' }),
  postGenerationExport: (data: any, fileName?: string): Promise<any> =>
    http.post('/utils/cashFlow/generation/export', data, {
      mock,
      type: 'download',
      fileName,
    }),
  postGenerationExecute: (data: any): Promise<any> =>
    http.post('/utils/cashFlow/generation/execute', data, { mock }),
}
