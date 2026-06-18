import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { Input, Tabs } from 'antd'
import { useEffect, useRef, useState } from 'react'
import { debounce as _debounce } from 'lodash'
import Api from '../api'
import { formatPercent, amountFormat } from '@/utils'
import { ProjectSelect } from '@/components/Project/ProjectSelectEntries'
import { ProjectClientSelect as ClientSelect } from '@/components/Project/ClientSelectEntries'

const { Item } = Form

function EditModal({ store }) {
  const [curtab, setCurTab] = useState('1')
  const proRef = useRef()
  const [form] = Form.useForm()

  const [projectList, setProjectList] = useState([])

  useEffect(() => {
    if (curtab === '2') {
      getProject()
    }
  }, [curtab])

  const getProject = _debounce(async (val) => {
    const res = await Api.getEstablishList({ projVagueName: val })
    setProjectList(res ?? [])
  }, 500)

  const onSearch = (value) => {
    getProject(value)
  }

  const onChange = async (record) => {
    if (!record) {
      setProjectList([])
      return
    }
    const curProj = projectList.filter((item) => item.id === record.key)[0]
    const { clientNames, id } = curProj
    const data = await Api.getRemianCreditAmount({
      groupCreditReviewId: id,
    })
    form.setFieldsValue({
      clientNames: clientNames.join('、'),
      applyCreditAmount: amountFormat(formatPercent(data)),
      id,
      curtab,
    })
  }

  const onProjectChang = (val) => {
    const data = proRef.current.getSelectedData(val.value)
    if (data) {
      const { clientNames, bizType } = data
      form.setFieldsValue({
        clientName: clientNames.join(','),
        bizType: store.getKeyOptionsLabelMap('projEstablishBizType')[bizType],
        curtab,
      })
    }
  }
  return (
    <Modal title={'发起评审'} store={store.createModal} okText={'确定'} destroyOnClose>
      <Tabs
        activeKey={curtab}
        onChange={setCurTab}
        items={[
          {
            label: `单体项目`,
            key: '1',
            children: null,
          },
          {
            label: `集团用信项目`,
            key: '2',
            children: null,
          },
        ]}
      ></Tabs>
      {curtab === '1' && (
        <Form form={form} labelCol={{ span: 6 }} preserve={false}>
          <Item
            label={'选择项目'}
            name={'projName'}
            rules={[{ required: true, message: '请选择项目！' }]}
          >
            <ProjectSelect
              ref={proRef}
              referer={'review'}
              onChange={onProjectChang}
              fieldNames={{ label: 'projName', value: 'id' }}
              labelInValue
              placeholder={'请输入'}
            />
          </Item>
          <Item
            label={'客户名称'}
            name={'clientName'}
            rules={[{ required: true, message: '请选择客户名称！' }]}
          >
            <Input disabled />
          </Item>
          <Item
            label={'业务类型'}
            name={'bizType'}
            disabled
            rules={[{ required: true, message: '请选择业务类型！' }]}
          >
            <Input disabled />
          </Item>
          <Item name={'curtab'} hidden>
            <Input />
          </Item>
        </Form>
      )}
      {curtab === '2' && (
        <Form form={form} labelCol={{ span: 6 }} preserve={false}>
          <h4>集团授信信息</h4>
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
                    <Item label={'剩余授信额度'} name="applyCreditAmount">
                      <Input disabled></Input>
                    </Item>
                    <Item name="id" hidden></Item>
                  </>
                )
              }
            }}
          </Item>
          <h4>项目用信信息</h4>
          <Form.Item
            label="客户名称"
            name="clientId"
            rules={[
              {
                required: true,
                message: '请输入客户名称！',
              },
            ]}
          >
            <ClientSelect queryParams={{ scene: 'main' }} />
          </Form.Item>
          <Item
            label={'项目名称'}
            name={'projName'}
            rules={[{ required: true, message: '请输入项目名称！' }]}
          >
            <Input></Input>
          </Item>

          <Item
            label={'业务类型'}
            name={'bizType'}
            disabled
            rules={[{ required: true, message: '请选择业务类型！' }]}
          >
            <Select options={'projEstablishBizType'}></Select>
          </Item>
          <Item name={'curtab'} hidden>
            <Input />
          </Item>
        </Form>
      )}
    </Modal>
  )
}

export default observer(EditModal)
