import { http } from '@zswl/admin'

export default {
  postDashboardFileExport: (data, functionCode = 'dashboardFileExport') =>
    http.post('/file/export', data, {
      type: 'download',
      headers: {
        functionCode,
      },
    }),
}
