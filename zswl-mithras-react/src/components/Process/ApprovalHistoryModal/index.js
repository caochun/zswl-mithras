import { observer } from '@zswl/admin'
import store from './store'
import ApprovalHistory from '../ApprovalHistory'
import { Modal } from 'antd'
import { App } from '@zswl/components'
import { useState, useEffect } from 'react'

function Index({ visible, processInstanceId, callBack }) {
  const [show, setShow] = useState(false)

  useEffect(() => {
    setShow(visible)
  }, [visible, processInstanceId])
  useEffect(() => {
    return () => {
      App.resetStore(store)
    }
  }, [])
  const handleCancel = () => {
    setShow(false)
    callBack && callBack()
  }
  return (
    <>
      <Modal
        footer={null}
        visible={show}
        width={'90%'}
        onCancel={handleCancel}
        destroyOnClose
        bodyStyle={{ maxHeight: '600px', overflowY: 'auto' }}
        title="审批历史"
      >
        <ApprovalHistory processInstanceId={processInstanceId} />
      </Modal>
    </>
  )
}

export default observer(Index)
