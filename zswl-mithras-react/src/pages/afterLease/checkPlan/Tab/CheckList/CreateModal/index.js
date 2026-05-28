import { observer } from '@zswl/admin'
import { Form, Modal, Select, App } from '@zswl/components'
import { Input, InputNumber, DatePicker } from 'antd'
import { ClientSelect, ApiSelect, FounderSelect } from '@/components'
import moment from 'moment'
import Api from '../api'

const { Item } = Form

function CreateModal({ store }) {
  const [form] = Form.useForm()
  const { optionsType } = App.getData()

  const onClientChange = async (clientId) => {
    if (!clientId) {
      form.setFieldsValue({
        term: undefined,
        belongDeptId: undefined,
        belongSponsorId: undefined,
        deadLine: undefined,
      })
      return
    }
    const result = await Api.postCheckPlanClient({ clientId })
    const {
      belongSponsorId,
      term,
      deadLine,
      belongSponsorName,
      planName,
      checkWay,
      belongDeptId,
      stockRiskExposure,
    } = result
    form.setFieldsValue({
      planName,
      checkWay,
      belongDeptId,
      term,
      stockRiskExposure,
      belongSponsorId: {
        label: belongSponsorName,
        value: belongSponsorId,
      },
      deadLine: deadLine ? moment(deadLine) : undefined,
    })
  }

  return (
    <Modal title={'新增检查计划'} store={store.createModal} okText={'确定'} destroyOnClose>
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        <Item name="belongDeptId" hidden>
          <Input></Input>
        </Item>
        <Item name="term" hidden>
          <InputNumber></InputNumber>
        </Item>
        <Item name="stockRiskExposure" hidden>
          <InputNumber></InputNumber>
        </Item>
        <Item label={'计划类型'} name={'planType'} rules={[{ required: true, message: '请选择' }]}>
          <Select
            onChange={() => {
              form.setFieldsValue({
                planName: undefined,
              })
            }}
            placeholder="请输入"
            allowClear
            options={'afterLeaseCheckPlanTypeEnum'}
            style={{ maxWidth: '100%' }}
          />
        </Item>
        <Item dependencies={['planType']} noStyle>
          {({ getFieldValue }) => {
            const planType = getFieldValue('planType')
            if (planType === 'COMMONLY') {
              return (
                <Item
                  label={'客户名称'}
                  name="clientId"
                  rules={[{ required: true, message: '请选择' }]}
                >
                  <ClientSelect canJump={false} onChange={onClientChange}></ClientSelect>
                </Item>
              )
            }
          }}
        </Item>
        <Item dependencies={['planType', 'clientId']} noStyle>
          {({ getFieldValue }) => {
            const planType = getFieldValue('planType')
            // 一般检查计划
            if (planType === 'COMMONLY') {
              return (
                <>
                  <Item
                    label={'计划名称'}
                    name={'planName'}
                    rules={[{ required: true, message: '请输入' }]}
                  >
                    <Input />
                  </Item>
                  <Item
                    label={'客户主办'}
                    name="belongSponsorId"
                    rules={[{ required: true, message: '请选择' }]}
                  >
                    <FounderSelect
                      labelInValue
                      placeholder="请选择"
                      queryParams={{ job: 'projmanager' }}
                      functionCode="assetStrategySelectFounder"
                    />
                  </Item>
                  <Item
                    label={'检查形式'}
                    name="checkWay"
                    rules={[{ required: true, message: '请选择' }]}
                  >
                    <Select
                      options={optionsType.afterLeaseCheckWayEnum.filter(
                        (item) => item.value !== 'WITHOUT_CHECK'
                      )}
                      placeholder="请选择"
                    />
                  </Item>
                  <Item
                    label={'检查报告模板'}
                    name="reportType"
                    rules={[{ required: true, message: '请选择' }]}
                  >
                    <Select options={'afterLeaseCheckReportTypeEnum'} placeholder="请选择" />
                  </Item>
                  <Item dependencies={['checkWay']} noStyle>
                    {({ getFieldValue }) => {
                      const checkWay = getFieldValue('checkWay')
                      if (checkWay === 'SITE') {
                        return (
                          <Item
                            label={'协查风控经理'}
                            name="riskManagerId"
                            rules={[{ required: true, message: '请选择' }]}
                          >
                            <ApiSelect api={Api.postRiskManagerList} />
                          </Item>
                        )
                      }
                    }}
                  </Item>
                  <Item
                    label={'租后检查截止日'}
                    name="deadLine"
                    rules={[{ required: true, message: '请选择' }]}
                  >
                    <DatePicker style={{ width: '100%' }}></DatePicker>
                  </Item>
                </>
              )
            }
            return (
              <Item
                label={'计划名称'}
                name={'planName'}
                rules={[{ required: true, message: '请输入计划名称！' }]}
              >
                <Input />
              </Item>
            )
          }}
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(CreateModal)
