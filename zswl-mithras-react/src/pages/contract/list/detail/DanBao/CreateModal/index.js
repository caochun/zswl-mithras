import { observer } from '@zswl/admin'
import { getInputNumberValueFromEvent, amountStrToNumber, hasValue } from '@/utils'
import { Form, Modal, Select, Button } from '@zswl/components'
import { Input, Tooltip } from 'antd'
import { QuestionCircleOutlined } from '@ant-design/icons'
import DataUpload from '@/components/DataUpload'
import mathjs from '@/utils/math'

const { Item } = Form

function Index({ store, bizType }) {
  const [form] = Form.useForm()
  const {
    isDetail,
    isCreate,
    contractList,
    clientList,
    contractDataList,
    getContactDataList,
    clientType,
  } = store

  const onChange = (value) => {
    form.setFieldsValue({
      guarantorIds: undefined,
      relatContracts: undefined,
      contactId: undefined,
    })
    store.contractDataList = []
    store.clientType = value
    store.searchClientList()
  }

  const onMortgageIdsChange = (value) => {
    const jointGuaranteeMark = value.length <= 1 ? 'SINGLE' : 'JOINT'
    const amountSingle = hasValue(store.baseStore.priceData.applyCreditAmount)
      ? mathjs.toNonExponentialPlus(
          mathjs.format(mathjs.divide(store.baseStore.priceData.applyCreditAmount, 10000))
        )
      : undefined

    if (isCreate) {
      setTimeout(() => {
        form.setFieldsValue({
          amountSingle,
        })
      }, 10)
    }

    form.setFieldsValue({
      relatContracts: undefined,
      contactId: undefined,
      jointGuaranteeMark,
    })
    store.contractDataList = []
    store.clientIds = value
    store.getContractList()
  }

  return (
    <Modal
      title={`${isCreate ? '创建' : isDetail ? '查看' : '编辑'}担保措施`}
      store={store.$createModal}
      destroyOnClose
      footer={
        isDetail
          ? null
          : [
              <Button key={1} onClick={store.$createModal.close}>
                取消
              </Button>,
              <Button key={2} type="primary" onClick={store.$createModal.submit}>
                确定
              </Button>,
            ]
      }
    >
      <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 18 }} preserve={false}>
        <Item
          label={'保证合同编号'}
          name={'guarantorContractCode'}
          // rules={[{ required: true, message: '请输入保证合同编号！' }]}
        >
          <Input disabled={isDetail} />
        </Item>
        <Item
          label={'担保人类型'}
          name={'guarantorType'}
          rules={[{ required: true, message: '请选择担保人类型！' }]}
        >
          <Select options={'clientType'} disabled={isDetail} onChange={onChange} />
        </Item>
        <Item
          label={'担保人名称'}
          name={'guarantorIds'}
          rules={[{ required: true, message: '请选择担保人名称！' }]}
        >
          <Select
            debounceSearch
            placeholder="请选择担保人名称"
            allowClear
            mode="multiple"
            disabled={isDetail}
            filterOption={false}
            options={clientList || []}
            showSearch
            onSearch={(val) => store.searchClientList(val)}
            fieldNames={{ value: 'id', label: 'clientName' }}
            onChange={onMortgageIdsChange}
          />
        </Item>
        <Item noStyle dependencies={['guarantorType']}>
          {({ getFieldValue }) => {
            const guarantorType = getFieldValue('guarantorType')
            if (bizType === 'ZL' && guarantorType === 'CORPORATION') {
              return (
                <Item
                  label={'决议类型'}
                  name={'resolutionType'}
                  rules={[{ required: true, message: '请选择！' }]}
                >
                  <Select options={'resolutionTypeEnum'} disabled={isDetail} />
                </Item>
              )
            }
          }}
        </Item>
        <Item noStyle dependencies={['resolutionType']}>
          {({ getFieldValue }) => {
            const resolutionType = getFieldValue('resolutionType')
            if (bizType === 'ZL' && resolutionType === 'OTHER') {
              return (
                <Item
                  label={'决议文件'}
                  name={'resolutionFileId'}
                  rules={[{ required: true, message: '请上传！' }]}
                >
                  <DataUpload maxCount={1}></DataUpload>
                </Item>
              )
            }
          }}
        </Item>
        {bizType === 'ZL' && (
          <Item noStyle dependencies={['guarantorType']}>
            {({ getFieldValue }) => {
              const guarantorType = getFieldValue('guarantorType')
              if (guarantorType === 'CORPORATION') {
                return (
                  <Item
                    label={'章程文件'}
                    name={'constitutionFileIds'}
                    rules={[{ required: true, message: '请上传！' }]}
                  >
                    <DataUpload maxCount={1}></DataUpload>
                  </Item>
                )
              }
            }}
          </Item>
        )}
        <Item noStyle dependencies={['guarantorType', 'guarantorIds']}>
          {({ getFieldValue }) => {
            const guarantorType = getFieldValue('guarantorType')
            const guarantorIds = getFieldValue('guarantorIds')
            if (guarantorType === 'CORPORATION' && guarantorIds?.length > 0) {
              return (
                <Item
                  label={
                    <>
                      指定联系人
                      <Tooltip title="需从客户管理模块取值">
                        <QuestionCircleOutlined />
                      </Tooltip>
                    </>
                  }
                  name={'contactId'}
                >
                  <Select
                    allowClear
                    disabled={isDetail}
                    options={contractDataList}
                    onSearch={(e) => getContactDataList(e, guarantorIds[0])}
                    onFocus={(e) => getContactDataList(e, guarantorIds[0])}
                    fieldNames={{ value: 'id', label: 'name' }}
                  />
                </Item>
              )
            }
          }}
        </Item>
        <Item label={'关联合同编号'} name={'relatContracts'}>
          <Select
            debounceSearch
            options={contractList || []}
            disabled={isDetail}
            mode="multiple"
            showSearch
            onSearch={(val) => store.getContractList(val)}
            fieldNames={{ value: 'contractCode', label: 'contractCode' }}
          />
        </Item>
        <Item
          label={'担保方式'}
          name={'guaranteeMethod'}
          rules={[{ required: true, message: '请选择担保方式！' }]}
          tooltip="如无特殊情况需选择“连带责任”"
        >
          <Select options={'guaranteeMethodEnum'} disabled={isDetail} />
        </Item>
        <Item
          label={'联保标志'}
          name={'jointGuaranteeMark'}
          rules={[{ required: true, message: '请选择联保标志！' }]}
          tooltip="如保证主体为1个，选择“单人保证”，如大于1个选择“联保”"
        >
          <Select options={'jointGuaranteeMarkEnum'} disabled={isDetail} />
        </Item>
        <Item dependencies={['jointGuaranteeMark']} noStyle>
          {({ getFieldValue }) => {
            const jointGuaranteeMark = getFieldValue('jointGuaranteeMark')
            const guarantorIds = getFieldValue('guarantorIds')
            if (jointGuaranteeMark === 'SINGLE' || jointGuaranteeMark === 'JOINT') {
              return (
                <Item
                  label={'担保本金(元)'}
                  name={'amountSingle'}
                  getValueFromEvent={getInputNumberValueFromEvent}
                  tooltip="填写合同金额"
                  rules={[
                    { required: true, message: '请输入担保本金！' },
                    {
                      validator(_, val) {
                        const valueNumber = amountStrToNumber(val)
                        if (valueNumber > 1000000000) {
                          return Promise.reject(new Error('金额需少于1,000,000,000！'))
                        }
                        return Promise.resolve()
                      },
                    },
                  ]}
                >
                  <Input disabled={isDetail} placeholder="请输入" />
                </Item>
              )
            } else if (jointGuaranteeMark === 'MULTIPLE_SEPARATE') {
              return (
                <Item label={'担保本金(元)'}>
                  {guarantorIds?.map((gid) => {
                    return (
                      <div key={gid} style={{ marginBottom: 10 }}>
                        <Item noStyle>
                          <Select
                            style={{ width: 172, marginRight: 10 }}
                            disabled
                            options={clientList || []}
                            fieldNames={{ value: 'id', label: 'clientName' }}
                            defaultValue={gid}
                          />
                        </Item>
                        <Item
                          name={`amountMultiple-${gid}`}
                          noStyle
                          getValueFromEvent={getInputNumberValueFromEvent}
                          rules={[
                            {
                              required: true,
                              message: '请输入担保本金！',
                            },
                            {
                              validator(_, val) {
                                const valueNumber = amountStrToNumber(val)
                                if (valueNumber > 1000000000) {
                                  return Promise.reject(new Error('金额需少于1,000,000,000！'))
                                }
                                return Promise.resolve()
                              },
                            },
                          ]}
                        >
                          <Input style={{ width: 172 }} disabled={isDetail} placeholder="请输入" />
                        </Item>
                      </div>
                    )
                  })}
                </Item>
              )
            }
            return null
          }}
        </Item>
        <Item
          label={'是否上报征信'}
          name={'isReport'}
          rules={[{ required: true, message: '请选择是否上报征信！' }]}
          tooltip="如纪要无特殊要求，均需勾选上报征信"
        >
          <Select
            options={[
              { value: 1, label: '是' },
              { value: 0, label: '否' },
            ]}
            disabled={isDetail}
          />
        </Item>
        <Item name="id" hidden>
          <Input />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
