import { jumpZhongDeng } from '@/utils'
import { observer } from '@zswl/admin'
import { Button } from '@zswl/components'

const Index = ({ params = {} }) => {
  return (
    <Button onClick={() => jumpZhongDeng(params)} type="link">
      中登网查询
    </Button>
  )
}

export default observer(Index)
