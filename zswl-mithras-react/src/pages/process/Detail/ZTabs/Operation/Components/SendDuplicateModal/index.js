import { observer } from '@zswl/admin'
import store from './store'
import OperatorStore from '../Operator/store'
import { Select, App } from '@zswl/components'
import { Modal, Form, message } from 'antd'
import styles from './index.less'
import { useState, useEffect } from 'react'
import Api from './api'

function Index({ processInstanceId, visible, taskId, flag, externalForm, callBack, detailData }) {
  //flag="collaborate"
  const { founderList } = store
  const { ccTabReadOnlyFlag, ccUerList} = detailData
  const [show, setShow] = useState(false)
  const [form] = Form.useForm()
  useEffect(() => {
    setShow(visible)
  }, [processInstanceId, visible])
  useEffect(() => {
    return () => {
      App.resetStore(store)
    }
  }, [])
  useEffect(() => {
    // if (flag == 'collaborate') {
    store.searchFounder('')
  }, [flag])
  const handleCancel = () => {
    setShow(false)
    callBack && callBack()
  }
  const handleOk = async () => {
    //console.log(form.getFieldValue().ccUserIdList, 999999999)
    if (flag == 'collaborate') {
      OperatorStore.collaborate({
        collaborateUserId: form.getFieldValue().ccUserIdList,
        taskId,
      })
      callBack && callBack()
      return
    }
    await Api.saveExecution({ processInstanceId, ccUserIdList: ccTabReadOnlyFlag && ccUerList ? [] : form.getFieldValue().ccUserIdList })
    message.success('抄送成功！')
    setShow(false)
    callBack && callBack()
  }
  return (
    <>
      <Modal
        title={flag == 'collaborate' ? '协同人员' : '抄送'}
        visible={show}
        onCancel={handleCancel}
        onOk={handleOk}
        destroyOnClose
      >
        <Form form={form} preserve={false}>
          <Form.Item name="ccUserIdList" label={flag == 'collaborate' ? '协同人员' : '抄送人员'}>
            <Select
              mode={flag == 'collaborate' ? '' : 'multiple'}
              style={{ width: '100%' }}
              options={founderList}
              placeholder="请输入"
              onSearch={(e) => {
                store.searchFounder(e)
              }}
            />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}

export default observer(Index)
