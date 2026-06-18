import { observer } from '@zswl/admin'
import DataUpload from '@/components/DataUpload'
import { Form, Modal, Select } from '@zswl/components'
import { Button, Input } from 'antd'

const { Item } = Form
const { TextArea } = Input

function Index({ store }) {
  const [form] = Form.useForm()
  const { isDetail, isCreate, clientList, contractList } = store

  const onChange = (value) => {
    form.setFieldsValue({
      pledgeIds: undefined,
    })
    store.clientType = value
    store.searchClientList()
  }
  const onPledgeIdsChange = (value) => {
    form.setFieldsValue({
      relatContracts: undefined,
    })
    store.clientIds = value
    store.getContractList()
  }

  return (
    <Modal
      title={`${isCreate ? '创建' : isDetail ? '查看' : '编辑'}质押措施`}
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
          label={'质押合同编号'}
          name={'pledgeContractCode'}
          // rules={[{ required: true, message: '请输入！' }]}
        >
          <Input disabled={isDetail} />
        </Item>
        <Item
          label={'质押类型'}
          name={'contractPledgeType'}
          rules={[{ required: true, message: '请选择！' }]}
        >
          <Select options={'pledgeTypeEnum'} disabled={isDetail} />
        </Item>
        <Item dependencies={['contractPledgeType']} noStyle>
          {({ getFieldValue }) => {
            const type = getFieldValue('contractPledgeType')
            if (type === 'ACCOUNTS_RECEIVABLE_PLEDGE') {
              return (
                <Item
                  label="应收账款基础材料"
                  name={'mortgagePledgeObjList'}
                  required
                  rules={[{ required: true, message: '请上传应收账款基础材料！' }]}
                >
                  <DataUpload disabled={isDetail} accept="*" />
                </Item>
              )
            }
          }}
        </Item>
        <Item
          label={'质押物清单'}
          name={'file'}
          rules={[{ required: true, message: '请上传质押物清单！' }]}
        >
          <DataUpload accept=".xlsx" maxCount={1} disabled={isDetail}></DataUpload>
        </Item>
        <Item
          label={'出质人类型'}
          name={'pledgeType'}
          rules={[{ required: true, message: '请选择出质人类型！' }]}
        >
          <Select
            options={'clientType'}
            disabled={isDetail}
            onChange={onChange}
            placeholder="请选择出质人类型"
          />
        </Item>
        <Item
          label={'出质人名称'}
          name={'pledgeIds'}
          rules={[{ required: true, message: '请选择出质人名称！' }]}
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
            onChange={onPledgeIdsChange}
            fieldNames={{ value: 'id', label: 'clientName' }}
          />
        </Item>
        <Item label={'关联合同编号'} name={'relatContracts'} dependencies={['pledgeIds']}>
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
        <Item
          label={'质押物描述'}
          name={'pledgeDescribe'}
          rules={[{ required: true, message: '请输入质押物描述！' }]}
        >
          <TextArea
            disabled={isDetail}
            autoSize={{ minRows: 4, maxRows: 20 }}
            placeholder="请输入质押物描述"
          />
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
          <Input />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
