import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from '@/components/Contract/PaymentApplyColumns'
import { getDescColumns } from '@/utils'
import { useEffect, useMemo, useRef, useState } from 'react'
import { Space, Form, message } from 'antd'
import { Button, Modal, ModalStore } from '@zswl/components'
import AddModal from './AddModal'
import ftpInterestChangeApi from '@/api/budget/pricing/ftpInterestChangeApi'
import { getQuery, observer } from '@zswl/admin'
import moment from 'moment'

const getDetail = async (params = {}) => {
  const res = await ftpInterestChangeApi.postApplyDetail(params)
  return res
}

const Index = ({ canEdit = true, modal, modalProps }) => {
  const detail = modal?.getInitialValues()
  const ref = useRef([])
  useEffect(() => {
    if (detail) {
      setDetailList(detail?.ftpAssessmentInfoList)
    }
  }, [detail])
  const [detailList, setDetailList] = useState([])
  const [form] = Form.useForm()

  const FtpColumns = getDescColumns(ALL_COLUMNS(), [
    'FTP基础价格',
    'FTP山区调整',
    'FTP评级调整',
    'FTP指引价格',
    'FTP是否质押',
    'FTP手工调整',
    '杭甬特殊调整',
    'FTP考核价格',
    '票据FTP价格',
    '变更起始日',
    '变更差额调整日',
    '变更原因',
  ])
  const [saveId, setSaveId] = useState(null)

  const handleDelete = async (receiptCode) => {
    const newDetailList = detailList.filter((item) => item.receiptCode !== receiptCode)
    setDetailList(newDetailList)
  }
  const isFormApproval = getQuery('typeId') == 'approval'

  const handleSave = async () => {
    const values = await Promise.all(
      ref.current.map(async (item) => {
        if (!item?.form) return undefined
        return await item.form.getFieldsFormatValue()
      })
    )
    const ftpAssessmentInfoList = values
      .map((item, index) => {
        if (!item) return null
        const { effectDate, ftpInterestDiffDate, ftpAssessmentInfoList, ...rest } = item ?? {}

        return {
          ...detailList[index],
          ...rest,
          effectDate: effectDate && moment(effectDate).format('YYYY-MM-DD'),
          ftpInterestDiffDate:
            ftpInterestDiffDate && moment(ftpInterestDiffDate).format('YYYY-MM-DD'),
        }
      })
      .filter(Boolean)

    const id = await ftpInterestChangeApi.postApplySave({
      id: detail?.id ?? saveId,
      ftpAssessmentInfoList,
    })
    setSaveId(id)
    const res = await getDetail({ id })
    setDetailList(res?.ftpAssessmentInfoList)
    ref.current.forEach((item, index) => {
      if (item?.form) {
        item.form.setFieldsValue({
          assessmentPrice: res?.ftpAssessmentInfoList[index]?.assessmentPrice / 10000,
        })
      }
    })
    return new Promise((resolve, reject) => {
      message.success('保存成功')
      resolve(id)
    })
  }

  const handleSubmit = async () => {
    const id = await handleSave()
    await ftpInterestChangeApi.postApplySubmit({
      id,
    })
    message.success('提交成功')
    setSaveId(null)
    modal.close()
  }

  const addModal = useMemo(() => {
    return new ModalStore({})
  }, [])
  const handleAdd = () => {
    addModal.open({})
  }
  return (
    <>
      <Modal
        title="FTP计息变更"
        store={modal}
        width={1200}
        destroyOnClose
        footer={
          <Space>
            {canEdit && <Button onClick={handleSave}>保存</Button>}
            {canEdit && !isFormApproval && (
              <Button type="primary" onClick={handleSubmit}>
                提交
              </Button>
            )}
          </Space>
        }
        {...modalProps}
      >
        <Form form={form}>
          {detailList.length === 0 && <Button onClick={handleAdd}>新增</Button>}
          {detailList.map((item, index) => {
            return (
              <div key={item.receiptCode || index} style={{ marginBottom: 10 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <Space>
                    <div>合同编号：{item?.contractCode}</div>
                    <div>借据编号：{item?.receiptCode}</div>
                    <div>客户名称：{item?.clientName}</div>
                  </Space>
                  <Space>
                    {canEdit && (
                      <Button onClick={() => handleDelete(item.receiptCode)}>删除</Button>
                    )}
                    {canEdit && <Button onClick={handleAdd}>新增</Button>}
                  </Space>
                </div>

                <div style={{ marginTop: 10 }}>
                  <EditDescription
                    column={3}
                    hiddenButton
                    title={null}
                    detail={item || {}}
                    canEdit={canEdit}
                    columns={FtpColumns}
                    initEdit={canEdit}
                    ref={(node) => {
                      ref.current[index] = node
                    }}
                  />
                </div>
              </div>
            )
          })}
        </Form>
      </Modal>
      <AddModal store={addModal} setDetailList={setDetailList} detailList={detailList} />
    </>
  )
}

export default observer(Index)
