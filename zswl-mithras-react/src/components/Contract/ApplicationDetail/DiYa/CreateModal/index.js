import DataUpload from '@/components/DataUpload'
import { hasValue } from '@/utils'
import { observer } from '@zswl/admin'
import { Form, Modal, Select, Button } from '@zswl/components'
import { Input, DatePicker } from 'antd'

const { Item } = Form
const { TextArea } = Input

function ContractMortgageCreateModal({ store }) {
  const [form] = Form.useForm()
  const { isDetail, isCreate, contractList, clientList } = store

  const onChange = (value) => {
    form.setFieldsValue({
      mortgageIds: undefined,
      relatContracts: undefined,
    })
    store.clientType = value
    store.searchClientList()
  }

  const onMortgageIdsChange = (value) => {
    form.setFieldsValue({
      relatContracts: undefined,
    })
    store.clientIds = value
    store.getContractList()
  }

  return (
    <Modal
      title={`${isCreate ? '创建' : isDetail ? '查看' : '编辑'}抵押措施`}
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
          label={'抵押合同编号'}
          name={'mortgageContractCode'}
          // rules={[{ required: true, message: '请输入！' }]}
        >
          <Input disabled={isDetail} />
        </Item>
        <Item
          label={'抵押类型'}
          name={'contractMortgageType'}
          rules={[{ required: true, message: '请选择抵押类型！' }]}
        >
          <Select options={'mortgageTypeEnum'} disabled={isDetail} />
        </Item>
        <Item dependencies={['contractMortgageType']} noStyle>
          {({ getFieldValue }) => {
            const type = getFieldValue('contractMortgageType')
            if (type === 'REAL_ESTATE_MORTGAGE') {
              return (
                <Item
                  label="不动产权登记证书"
                  name={'mortgagePledgeObjList'}
                  required
                  rules={[{ required: true, message: '请上传不动产权登记证书' }]}
                >
                  <DataUpload disabled={isDetail} accept="*" />
                </Item>
              )
            }
          }}
        </Item>
        <Item label={'抵押物清单'} name={'file'}>
          <DataUpload accept=".xlsx" maxCount={1} disabled={isDetail}></DataUpload>
        </Item>
        <Item
          label={'抵押物类型'}
          name={'mortgageItemType'}
          rules={[{ required: true, message: '请选择抵押物类型！' }]}
        >
          <Select options={'mortgageItemTypeEnum'} disabled={isDetail} />
        </Item>
        <Item
          label={'抵押人类型'}
          name={'mortgageType'}
          rules={[{ required: true, message: '请选择抵押人类型！' }]}
        >
          <Select options={'clientType'} disabled={isDetail} onChange={onChange} />
        </Item>
        <Item
          label={'抵押人名称'}
          name={'mortgageIds'}
          rules={[{ required: true, message: '请选择抵押人名称！' }]}
        >
          <Select
            debounceSearch
            placeholder="请选择"
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
        <Item label={'关联合同编号'} name={'relatContracts'} dependencies={['mortgageIds']}>
          <Select
            debounceSearch
            options={contractList}
            disabled={isDetail}
            mode="multiple"
            showSearch
            onSearch={(val) => store.getContractList(val)}
            fieldNames={{ value: 'contractCode', label: 'contractCode' }}
          />
        </Item>
        <Item label={'抵押物描述'} name={'mortgageDescribe'}>
          <TextArea disabled={isDetail} autoSize={{ minRows: 4, maxRows: 20 }} />
        </Item>
        <Item
          label={'是否评估'}
          name={'assess'}
          rules={[{ required: true, message: '请选择是否评估！' }]}
        >
          <Select
            options={[
              {
                label: '否',
                value: 0,
              },
              {
                label: '是',
                value: 1,
              },
            ]}
            disabled={isDetail}
          />
        </Item>
        <Item dependencies={['assess']} noStyle>
          {({ getFieldValue }) => {
            const assess = getFieldValue('assess')
            return hasValue(assess) ? (
              <Item
                label={'评估公司'}
                name={'appraisalCompany'}
                rules={[{ required: !!assess, message: '请输入评估公司！' }]}
              >
                <Input disabled={isDetail} />
              </Item>
            ) : null
          }}
        </Item>
        <Item dependencies={['assess']} noStyle>
          {({ getFieldValue }) => {
            const assess = getFieldValue('assess')
            return hasValue(assess) ? (
              <Item
                label={'评估日期'}
                name={'assessDate'}
                rules={[{ required: !!assess, message: '请选择评估日期！' }]}
              >
                <DatePicker disabled={isDetail} style={{ width: '100%' }} format="yyyy-MM-DD" />
              </Item>
            ) : null
          }}
        </Item>
        <Item label={'评估编号'} name={'appraisalCode'}>
          <Input disabled={isDetail} />
        </Item>
        <Item
          label={'是否最高额'}
          name={'highest'}
          rules={[{ required: true, message: '请选择是否最高额！' }]}
        >
          <Select
            options={[
              {
                label: '否',
                value: 0,
              },
              {
                label: '是',
                value: 1,
              },
            ]}
            disabled={isDetail}
          />
        </Item>
        <Item name="id" hidden>
          <Input></Input>
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(ContractMortgageCreateModal)
