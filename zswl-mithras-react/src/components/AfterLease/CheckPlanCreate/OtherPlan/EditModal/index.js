import { useEffect, useState } from 'react'
import { observer } from '@zswl/admin'
import { Form, Modal, App, Select } from '@zswl/components'
import { debounce as _debounce } from 'lodash'
import { Input } from 'antd'
import Api from '@/api/afterLease/checkPlanCreateApi'
import { ApiSelect, FounderSelect } from '@/components/Select'

const { Item } = Form

function CreateModal({ store }) {
  const [projectList, setProjectList] = useState([])
  const [form] = Form.useForm()
  const { optionsType } = App.getData()
  const { isCreate, planId } = store

  useEffect(() => {
    if (store.$editModal.visible) {
      getProject()
    }
  }, [store.$editModal.visible])

  const getProject = _debounce(async (val) => {
    const res = await Api.getProjectQuery({ clientName: val, planId })
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
    const curProj = projectList.filter((item) => item.clientId === record.key)[0]
    const { isSelected, clientCode, clientType, sponsorId, sponsorName } = curProj

    form.setFieldsValue({
      isSelected,
      clientCode,
      clientType,
      sponsorUserId: sponsorId
        ? {
            value: sponsorId,
            label: sponsorName,
          }
        : undefined,
    })
  }

  return (
    <Modal
      title={isCreate ? '新增' : '编辑'}
      store={store.$editModal}
      okText={'确定'}
      destroyOnClose
      width={580}
    >
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        <Item
          label={'客户名称'}
          name={'clientName'}
          rules={[{ required: true, message: '请输入' }]}
        >
          <Select
            disabled={!isCreate}
            labelInValue
            placeholder="请输入查询"
            allowClear
            options={projectList}
            filterOption={false}
            style={{ maxWidth: '100%' }}
            onSearch={onSearch}
            onChange={onChange}
            fieldNames={{ label: 'clientName', value: 'clientId' }}
          />
        </Item>
        <Item dependencies={['clientName']} noStyle>
          {({ getFieldValue }) => {
            const val = getFieldValue('clientName')
            if (val) {
              return (
                <>
                  <Item label={'客户编号'} name={'clientCode'}>
                    <Input disabled></Input>
                  </Item>
                  <Item label={'客户类型'} name="clientType">
                    <Select disabled options={'clientType'} />
                  </Item>
                  <Item
                    label={'客户主办'}
                    name="sponsorUserId"
                    rules={[{ required: true, message: '请选择' }]}
                  >
                    <FounderSelect
                      placeholder="请选择"
                      queryParams={{ job: 'projmanager' }}
                      functionCode="selectFounder-afterLease"
                    />
                  </Item>
                  {isCreate && (
                    <Item label={'是否已添加'} name={'isSelected'}>
                      <Select disabled options="trueOrFalse"></Select>
                    </Item>
                  )}
                  <Item
                    label={'检查形式'}
                    name="checkWay"
                    rules={[{ required: true, message: '请选择' }]}
                  >
                    <Select options="afterLeaseCheckWayEnum" placeholder="请选择" />
                  </Item>
                  <Item
                    label={'检查报告模版'}
                    name="reportType"
                    required
                    rules={[{ required: true, message: '请选择' }]}
                  >
                    <Select options="afterLeaseCheckReportTypeEnum" placeholder="请选择" />
                  </Item>
                  <Item name="id" hidden>
                    <Input />
                  </Item>
                  <Item dependencies={['checkWay']} noStyle>
                    {({}) => {
                      const checkWay = getFieldValue('checkWay')
                      if (checkWay != 'OFFSITE') {
                        return (
                          <Item
                            label={'协查风控经理'}
                            name="riskManagerId"
                            rules={[{ required: true, message: '请选择' }]}
                          >
                            {/* <FounderSelect
                              labelInValue
                              params={{ job: 'riskmanager', sameDept: false }}
                              functionCode="selectfounder-1"
                            ></FounderSelect> */}

                            <ApiSelect api={Api.postRiskManagerList} labelInValue />
                          </Item>
                        )
                      }
                    }}
                  </Item>
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
