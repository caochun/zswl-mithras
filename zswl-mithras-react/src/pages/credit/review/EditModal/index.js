import { useEffect, useState } from 'react'
import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { debounce as _debounce } from 'lodash'
import { formatPercent, amountFormat } from '@/utils'
import { Input } from 'antd'
import store from '../store'
import groupCreditReviewApi from '@/api/credit/groupCreditReviewApi'

const { Item } = Form

function CreateModal() {
  const [projectList, setProjectList] = useState([])
  const [form] = Form.useForm()

  useEffect(() => {
    if (store.$createModal.visible) {
      getProject()
    }
  }, [store.$createModal.visible])

  const getProject = _debounce(async (val) => {
    const res = await groupCreditReviewApi.postEstablishQuery({ projVagueName: val })
    setProjectList(res ?? [])
  }, 500)

  const onSearch = (value) => {
    getProject(value)
  }

  const onChange = (record) => {
    if (!record) {
      setProjectList([])
      return
    }
    const curProj = projectList.filter((item) => item.id === record.key)[0]
    const { clientNames, applyCreditAmount, id } = curProj

    form.setFieldsValue({
      clientNames: clientNames.join('、'),
      applyCreditAmount: amountFormat(formatPercent(applyCreditAmount)),
      id,
    })
  }

  return (
    <Modal
      title={'发起授信评审'}
      store={store.$createModal}
      okText={'确定'}
      destroyOnClose
      width={580}
    >
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        <Item
          label={'授信名称'}
          name={'projectName'}
          rules={[{ required: true, message: '请输入' }]}
        >
          <Select
            labelInValue
            placeholder="请输入查询"
            allowClear
            options={projectList}
            filterOption={false}
            style={{ maxWidth: '100%' }}
            onSearch={onSearch}
            onChange={onChange}
            fieldNames={{ label: 'projName', value: 'id' }}
          />
        </Item>
        <Item dependencies={['projectName']} noStyle>
          {({ getFieldValue }) => {
            const val = getFieldValue('projectName')
            if (val) {
              return (
                <>
                  <Item label={'授信主体'} name={'clientNames'}>
                    <Input disabled></Input>
                  </Item>
                  <Item label={'授信额度'} name="applyCreditAmount">
                    <Input disabled></Input>
                  </Item>
                  <Item name="id" hidden></Item>
                </>
              )
            }
          }}
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(CreateModal)
