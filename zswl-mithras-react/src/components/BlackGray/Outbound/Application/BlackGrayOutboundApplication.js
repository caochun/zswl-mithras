import { observer } from '@zswl/admin'
import store from './store'
import List from '../../Manage/List'

function BlackGrayOutboundApplication({ path }) {
  return <List store={store} path={path} />
}

export default observer(BlackGrayOutboundApplication)
