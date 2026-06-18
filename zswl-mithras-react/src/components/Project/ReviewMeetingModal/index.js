import { useMemo } from 'react'
import MinutesOfReviewMeeting from '../ReviewMeetingMinute'
import { ModalStore, Modal, Button } from '@zswl/components'
import Api from '@/api/project/projReviewMeetingMinute'
import { message } from 'antd'

const MeetingModal = ({
  id,
  processInstanceId,
  projReviewType = 'PROJ_REVIEW_BASE',
  functionCode = 'projReviewMeetMinuteBaseInfoDetail',
}) => {
  const modal = useMemo(() => {
    return new ModalStore()
  }, [])
  const handleOpen = async () => {
    const res = await Api.postInfoDetail(
      {
        projReviewId: id,
        isEffect: 1,
        projFlowId: processInstanceId,
        projReviewType,
      },
      functionCode
    )
    if (res) {
      modal.open()
    } else {
      message.error('暂无生效的评审会纪要')
    }
  }

  return (
    <>
      <Button type="link" onClick={handleOpen}>
        评审会纪要
      </Button>
      <Modal title="评审会纪要" footer={null} width={1200} destroyOnClose store={modal}>
        <MinutesOfReviewMeeting
          contentType="popo"
          detailData={{ businessKey: id, processInstanceId }}
          projReviewType={projReviewType}
        />
      </Modal>
    </>
  )
}

export default MeetingModal
