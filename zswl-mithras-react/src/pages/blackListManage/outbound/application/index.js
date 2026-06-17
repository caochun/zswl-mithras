import { history, observer } from '@zswl/admin'
import store from './store'
import List from '@/components/BlackGray/Manage/List'

function Index({ path }) {
  return <List store={store} path={path} />
}

export default observer(Index)
