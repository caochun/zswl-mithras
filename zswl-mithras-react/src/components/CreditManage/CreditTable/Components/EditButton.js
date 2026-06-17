import { TextAreaColumn } from '@/components/Format'
import { compareDetail, compareTableData, formScrollToField } from '@/utils'
import { observer } from '@zswl/admin'
import { Button, DescStore, Descriptions, Form, Modal, ModalStore } from '@zswl/components'
import { Divider, Space, message } from 'antd'
import { useMemo, useState } from 'react'
import _ from 'lodash'

const labelStyle = {
  // color: 'red',
  background: '#F5F6FA',
  width: 180,
}
const contentStyle = {
  minWidth: 230,
  maxWidth: 320,
}

// 参数
// columns: 表格列
// canEdit: 是否可以编辑
// record: 当前行数据
// editType: 编辑类型 0 编辑 1 查看
const EditButton = ({ saveData, columns, disabled, record }) => {
  const [editType, setEditType] = useState(0)
  const { detail, newDetail, isLog } = useMemo(() => compareDetail(record), [record])
  const editModal = useMemo(() => new ModalStore({}), [])
  const isEdit = editType === 0
  const newColumns = [
    ...columns,
    TextAreaColumn({
      title: '修改原因',
      dataIndex: 'reason',
      requiredMark: true,
      required: true,
      editable: isEdit,
    }),
  ]
  const items = useMemo(() => {
    if (!isLog) return newColumns
    return newColumns.map((v) => {
      const newData = { ...v }
      if (isLog[newData.dataIndex]) {
        newData.contentStyle = { color: 'red' }
        newData.labelStyle = { color: 'red' }
      }
      return newData
    })
  }, [isLog, newColumns, detail])
  const handleClick = (val) => {
    setEditType(val)
    editModal.open({})
  }
  const [loading, setLoading] = useState(false)
  const handleOk = async () => {
    const form = newDetailDesc.getFormStore()
    try {
      const validPass = await form
        .validateFields()
        .then()
        .catch((e) => {
          formScrollToField(e, form)
        })
      if (!validPass) return
      const data = form.getFieldsFormatValue()

      setLoading(true)
      await saveData?.({ ...newDetail, ...data })
      editModal.close()
      setLoading(false)
    } catch (err) {
      console.log('err: ', err)
      setLoading(false)
    }
  }
  const title = editType === 0 ? '编辑' : '查看详情'
  const newDetailDesc = useMemo(
    () =>
      new DescStore({
        request: () => (newDetail.label === 'REMOVE' ? { reason: newDetail.reason } : newDetail),
      }),
    [newDetail]
  )
  const detailDesc = useMemo(
    () =>
      new DescStore({
        request: () => detail,
      }),
    [detail]
  )
  const isRemove = newDetail.label === 'REMOVE' || newDetail.isOverdue
  return (
    <Space>
      <Button
        type="link"
        onClick={() => handleClick(0)}
        disabled={disabled || isRemove}
        style={{ padding: 0 }}
      >
        编辑
      </Button>
      {newDetail.label && (
        <a className="btn btn-primary" onClick={() => handleClick(1)}>
          查看详情
        </a>
      )}
      <Modal
        title={title}
        store={editModal}
        width={900}
        onOk={handleOk}
        destroyOnClose
        footer={
          editType === 1 ? (
            <Button type="primary" onClick={editModal.close}>
              关闭
            </Button>
          ) : undefined
        }
      >
        {editType === 1 && (
          <Descriptions
            bordered
            column={2}
            store={detailDesc}
            items={items}
            contentStyle={contentStyle}
            labelStyle={labelStyle}
            dataSource={detail}
            editable={false}
          />
        )}
        {editType === 1 && <Divider>修改后</Divider>}

        <Descriptions
          formProps={{ scrollToFirstError: true }}
          bordered
          column={2}
          items={isEdit ? newColumns : items}
          store={newDetailDesc}
          contentStyle={contentStyle}
          labelStyle={labelStyle}
          dataSource={newDetail}
          editable={isEdit}
        />
      </Modal>
    </Space>
  )
}
export default observer(EditButton)
