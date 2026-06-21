import { observer } from '@zswl/admin'
import store from './store'
import List from '../../Manage/List'

function BlackGrayBreakThroughApplication({ path }) {
  return <List store={store} path={path} type="break" />
}

export default observer(BlackGrayBreakThroughApplication)
