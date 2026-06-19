import { useEffect, useState } from 'react'
import { observer } from '@zswl/admin'
import { Form, Modal, Select, App } from '@zswl/components'
import { debounce as _debounce } from 'lodash'
import store from '../store'
import { Input } from 'antd'
import Api from '@/api/contract/baseInfo'

const { Item } = Form

function EditModal() {
  const [projectList, setProjectList] = useState([])
  const [showLeaseType, setShowLeaseType] = useState(false)

  const [form] = Form.useForm()
  const { optionsType } = App.getData()

  const searchProject = _debounce(async (e) => {
    const res = await Api.postProjList({ projVagueName: e, projReviewStatus: 'TAKE_EFFECT' })
    setProjectList(res ?? [])
  }, 500)

  useEffect(() => {
    searchProject()
  }, [])

  const onSearch = (value) => {
    searchProject(value)
  }

  const onChange = (record) => {
    if (!record) {
      form.setFieldsValue({
        clientName: undefined,
        leaseType: undefined,
      })
      searchProject()
      setShowLeaseType(false)
      return
    }
    const curProj = projectList.filter((item) => item.id === record.key)[0]

    setShowLeaseType(curProj.bizType === 'ZL' || curProj.bizType === 'ZZ')

    form.setFieldsValue({
      clientName: curProj.clientNames[0],
      bizType: curProj.bizType,
      projReviewId: curProj.id,
    })
  }

  return (
    <Modal title={'创建合同'} store={store.createModal} okText={'确定'} destroyOnClose>
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        <Item
          label={'项目名称'}
          name={'projName'}
          rules={[{ required: true, message: '请选择项目！' }]}
        >
          <Select
            placeholder="请输入"
            allowClear
            debounceSearch
            options={projectList}
            labelInValue
            filterOption={false}
            style={{ maxWidth: '100%' }}
            fieldNames={{ value: 'id', label: 'projName' }}
            onSearch={onSearch}
            onChange={onChange}
          />
        </Item>
        <Item dependencies={['projName']} noStyle>
          {() => {
            if (showLeaseType) {
              return (
                <Item
                  label={'租赁类型'}
                  name={'leaseType'}
                  rules={[{ required: true, message: '请选择租赁类型！' }]}
                >
                  <Select
                    options={optionsType.leaseType}
                    labelInValue
                    filterOption={false}
                    style={{ maxWidth: '100%' }}
                  />
                </Item>
              )
            }
          }}
        </Item>
        <Item dependencies={['projName']} noStyle>
          {({ getFieldValue }) => {
            const val = getFieldValue('projName')
            if (val) {
              return (
                <Item label={'客户名称'} name={'clientName'}>
                  <Input placeholder={'客户名称'} disabled />
                </Item>
              )
            }
          }}
        </Item>
        <Item name="bizType" hidden>
          <Input />
        </Item>
        <Item name="projReviewId" hidden>
          <Input />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(EditModal)
