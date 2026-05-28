import { history, observer } from '@zswl/admin'
import store from './store'
import List from '../../components/List'

function Index({ path }) {
  return <List store={store} path={path} type="break" />
}

export default observer(Index)
