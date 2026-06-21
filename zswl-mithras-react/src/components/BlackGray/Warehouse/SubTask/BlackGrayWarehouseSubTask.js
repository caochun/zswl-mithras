import { observer } from '@zswl/admin'
import InsideList from '../MainTask/InsideList'
import { Page } from '@zswl/components'

function BlackGrayWarehouseSubTask({ path }) {
  return (
    <Page>
      <InsideList sub path={path} />
    </Page>
  )
}
export default observer(BlackGrayWarehouseSubTask)
