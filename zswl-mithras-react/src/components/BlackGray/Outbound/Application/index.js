import { observer } from '@zswl/admin'
import store from './store'
import List from '../../Manage/List'

function Index({ path }) {
  return <List store={store} path={path} />
}

export default observer(Index)
