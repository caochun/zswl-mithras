import { history, observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { Input, message } from 'antd'
import { useState } from 'react'
import Api from '../api'
import store from '../store'

const { Item } = Form

function FileModal() {
  const [options, setOptions] = useState([])
  const [loading, setLoading] = useState(false)

  const loadProjects = async () => {
    setLoading(true)
    try {
      const data = await Api.getReviewProject()
      setOptions(
        data?.map((item) => ({
          label: item.projName,
          value: item.projReviewId,
          clientName: item.clientName,
          projCode: item.projCode,
        })) || []
      )
    } catch (error) {
      console.error(error)
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (val) => {
    if (val) {
      const selected = options.find((item) => item.value === val)
      if (selected) {
        store.fileForm.setFieldsValue({
          projCode: selected.projCode || '',
          clientName: selected.clientName || '',
        })
      }
    } else {
      store.fileForm.setFieldsValue({
        projCode: '',
        clientName: '',
      })
    }
  }

  const handleOk = async () => {
    const values = await store.fileForm.validateFields()
    if (!values.projReviewId) {
      message.warning('请选择项目名称！')
      throw new Error('请选择项目名称')
    }
    const id = await Api.confirmProject({ projReviewId: values.projReviewId })
    message.success('发起归档成功！')
    store.table.search()
    store.fileModal.close()
    setTimeout(() => {
      history.push(`/archives/otherFilingMaterials/detail/${id}?approveStatus=UN_SUBMIT`)
    }, 300)
  }

  return (
    <Modal
      title="其他资料归档发起"
      store={store.fileModal}
      okText="确定"
      destroyOnClose
      onOk={handleOk}
      afterClose={() => {
        store.fileForm.resetFields()
        setOptions([])
      }}
      width={600}
    >
      <Form store={store.fileForm} labelCol={{ span: 6 }}>
        <Item
          label="项目名称"
          name="projReviewId"
          rules={[{ required: true, message: '请选择项目名称！' }]}
        >
          <Select
            showSearch
            allowClear
            placeholder="请选择项目名称"
            filterOption={(input, option) =>
              (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
            }
            onFocus={loadProjects}
            onChange={handleChange}
            options={options}
            loading={loading}
            notFoundContent={loading ? '加载中...' : '暂无数据'}
          />
        </Item>
        <Item
          label="项目编号"
          name="projCode"
          rules={[{ required: true, message: '请选择项目编号！' }]}
        >
          <Input disabled placeholder="" />
        </Item>
        <Item
          label="客户名称"
          name="clientName"
          rules={[{ required: true, message: '请选择客户名称！' }]}
        >
          <Input disabled placeholder="" />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(FileModal)
