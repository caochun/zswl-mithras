import { history, observer } from '@zswl/admin'
import store from './store'
import { BlackGrayList as List } from '@/components/BlackGray/BlackGrayEntries'

function Index({ path }) {
  return <List store={store} path={path} />
}

export default observer(Index)
