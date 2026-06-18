import { Button, Form, Modal, ModalStore } from '@zswl/components'
import BaseInfo from './BaseInfo'
import TrackingTask from './TrackingTask'
import { getLocalStorage, getQuery, observer } from '@zswl/admin'
import { useMemo, useRef, useState } from 'react'
import trackingApi from '@/api/lease/trackingApi'
import { message } from 'antd'

const Index = ({ projReviewMeetMinuteId, store, params, TrackModalChange }) => {
  // const { keys, rows } = store.table.getSelected()
  const isLedger = !params?.bizId
  const [modalType, setModalType] = useState('add')
  TrackModalChange && TrackModalChange(true)
  const modal = useMemo(
    () =>
      new ModalStore({
        onOpen: async ({ type, ...values }) => {
          setModalType(type)
          if (type === 'edit') {
            const { trackEventContractInfo, ...detail } = await trackingApi.getTrackEventDetail(
              values
            )
            setTimeout(() => {
              baseInfoRef.current?.setValues(trackEventContractInfo)
              trackRef.current?.setValues(detail)
            }, 10)
          } else if (type === 'add') {
            baseInfoRef.current?.reset()
            trackRef.current?.reset()
          }
        },
        onFinish: async () => {
          const baseInfoDetail = await baseInfoRef.current?.submit()
          const trackDetail = await trackRef.current?.submit()
          const addNew = async () => {
            await trackingApi.postTrackEventAdd({
              projReviewMeetMinuteId,
              ...baseInfoDetail,
              ...trackDetail,
              isLedger,
            })
            message.success('新增成功')
            modal.close()
            store && store.table?.search?.()
            TrackModalChange && TrackModalChange(false)
            return true
          }
          const editData = async () => {
            await trackingApi.postTrackEventUpdate({ ...baseInfoDetail, ...trackDetail })
            message.success('编辑成功')
            modal.close()
            store?.table?.search?.()
            return true
          }
          const func = trackDetail.id ? editData : addNew
          if (!trackDetail.planTime && !trackDetail.startRentAfterDay) {
            Modal.info({
              title: '提示',
              content:
                '请填写【计划日期】或【起租后X自然日】若都填写则跟踪事项流程发起日期以【起租后X自然日】为准！',
            })
          } else {
            return await func()
          }
        },
      }),
    [isLedger]
  )
  const baseInfoRef = useRef(null)
  const trackRef = useRef(null)

  return (
    <>
      <Button.Add onClick={() => modal.open({ type: 'add' })} key="add" access={'trackEventAdd'}>
        新增
      </Button.Add>
      {/* <Button.Edit
        onClick={() => modal.open({ id: keys[0], type: 'edit' })}
        key="add"
        access={'trackEventAdd'}
        disabled={keys.length !== 1}
        style={{ marginLeft: 8 }}
      >
        编辑
      </Button.Edit> */}
      <Modal store={modal} title="新增跟踪事项" width={800} destroyOnClose>
        <Form>
          <div style={{ overflowY: 'scroll', height: 500 }}>
            <BaseInfo ref={baseInfoRef} params={params} />
            <TrackingTask ref={trackRef} />
          </div>
        </Form>
      </Modal>
    </>
  )
}

export default observer(Index)
