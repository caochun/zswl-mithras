import { http } from '@zswl/admin'

const mock = false

export default {
  getLibrary: (data, functionCode = 'clientBlackGrayBaseInfoLibrary') =>
    http.post('/black/gray/base/info/library', data, {
      mock,
      headers: {
        functionCode,
      },
    }),
}
