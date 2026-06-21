import { InstagramOutlined } from '@ant-design/icons'
import { Button } from '@zswl/components'
import { history } from '@zswl/admin'
import { useFlowData } from '@/utils/domains/process/ProcessFlowContext'

const ProcessToSnapshot = () => {
  const { detailData } = useFlowData()
  const { processInstanceId } = detailData

  // 审批快照面包线
  const toSnapshot = () => {
    const { pathname } = window.location
    const pathnameStr = pathname.split('/').filter(Boolean).splice(1, 1)?.join()
    history.push(`/process/${pathnameStr}/detail/snapshoot/${processInstanceId}?flag=info`)
  }

  return (
    <Button icon={<InstagramOutlined />} onClick={toSnapshot}>
      审批快照
    </Button>
  )
}

export default ProcessToSnapshot
