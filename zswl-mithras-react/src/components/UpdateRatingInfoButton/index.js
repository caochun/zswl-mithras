import { observer } from '@zswl/admin'
import { Button } from '@zswl/components'

const Index = ({ updateInfo }) => {
  return (
    <Button type="primary" onClick={updateInfo} style={{ marginRight: 8 }}>
      更新评级信息
    </Button>
  )
}

export default observer(Index)
