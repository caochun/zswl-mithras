import feikongSsoApi from '@/api/dashboard/feikongSsoApi'
import userCustomConfigApi from './userCustomConfigApi'

export default {
  ...userCustomConfigApi,
  ...feikongSsoApi,
}
