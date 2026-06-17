import { Button, Drawer } from '@zswl/components'
import { observer } from '@zswl/admin'
import TrackList from '@/components/Lease/Tracking/List'
import { useState } from 'react'

const Index = ({ projReviewMeetMinuteId ='',params }) => {
  const [open, setOpen] = useState(false)

  const showDrawer = () => {
    setOpen(true)
  }

  const onClose = () => {
    setOpen(false)
  }
  return (
    <>
      <Button type="link" onClick={showDrawer}>
        跟踪事项
      </Button>
      <Drawer title="跟踪事项" onClose={onClose} open={open} width={800}>
        <TrackList type="drawer" projReviewMeetMinuteId={projReviewMeetMinuteId} defaultData={params} />
      </Drawer>
    </>
  )
}

export default observer(Index)
