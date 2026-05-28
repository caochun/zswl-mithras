import { observer, Host } from '@zswl/admin'
import InsideList from '../mainTask/InsideList'
import { Page } from '@zswl/components'

function Index({ path }) {
  return (
    <Page>
      <InsideList sub path={path} />
    </Page>
  )
}
export default observer(Index)
