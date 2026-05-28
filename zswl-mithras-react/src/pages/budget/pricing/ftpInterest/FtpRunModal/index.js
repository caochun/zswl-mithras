import { Modal, ModalStore, Form } from '@zswl/components'
import { DatePicker, message } from 'antd'
import { observer } from '@zswl/admin'
import moment from 'moment'
import { useState, useEffect, forwardRef, useImperativeHandle } from 'react'
import Api from './api'

const { Item } = Form

const Index = ({ onFinish }, ref) => {
  const [lastMonth, setLastMonth] = useState()

  const $editModal = new ModalStore({
    onFinish: async (values) => {
      await Api.postFtpInterestRecalculate({
        interestDate: values.interestDate,
      })
      message.success('操作成功')
      $editModal.close()
      onFinish?.(values)
    },
  })

  const disabledDate = (current) => {
    if (!lastMonth) return false
    return current && current < moment(lastMonth).endOf('month')
  }

  const getLastMonth = async () => {
    const res = await Api.postFtpInterestLastMonth()
    setLastMonth(res)
  }

  useImperativeHandle(ref, () => ({
    open: () => {
      $editModal.open()
    },
  }))

  useEffect(() => {
    getLastMonth()
  }, [])

  return (
    <Modal store={$editModal} title="FTP计息" width={400}>
      <Form labelCol={{ span: 6 }}>
        <Item
          name="interestDate"
          label="日期"
          rules={[{ required: true, message: '请选择' }]}
          transform={(value) => {
            return moment(value).format('YYYY-MM')
          }}
        >
          <DatePicker disabledDate={disabledDate} picker="month" />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(forwardRef(Index))
