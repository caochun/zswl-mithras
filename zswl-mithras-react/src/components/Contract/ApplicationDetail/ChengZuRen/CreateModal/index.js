import { observer } from '@zswl/admin'
import { getInputNumberAmountProps, validatorRange } from '@/utils'
import { Form, Modal, Select, Upload } from '@zswl/components'
import { QuestionCircleOutlined } from '@ant-design/icons'
import { Input, InputNumber, Tooltip } from 'antd'
import DataUpload from '@/components/DataUpload'
import { ApiSelect } from '@/components/Select'
import Api from '@/api/contract/component/ApplicationDetail/ChengZuRen/api'
const { Item } = Form

function Index({ bizType, store, isChangYe }) {
  const [form] = Form.useForm()
  const { contractList, getContactList, contractId } = store
  const BL_ZR = bizType === 'BL' || bizType === 'ZR'

  return (
    <Modal title={`编辑`} store={store.$createModal} destroyOnClose>
      <Form form={form} labelCol={{ span: 8 }} preserve={false}>
        <Item
          label={BL_ZR ? '类型' : '承租人类型'}
          name={'lesseeType'}
          rules={[{ required: true, message: '请输入！' }]}
        >
          <Select
            options={BL_ZR ? 'creditorDebtorTypeEnum' : 'lesseeypeEnum'}
            labelInValue
            disabled
          />
        </Item>
        <Item
          label={BL_ZR ? '名称' : '承租人名称'}
          name={'lesseeName'}
          rules={[{ required: true, message: '请输入！' }]}
        >
          <Input placeholder="请输入" disabled />
        </Item>
        {bizType === 'ZL' && (
          <Item
            label={'决议类型'}
            name={'resolutionType'}
            rules={[{ required: true, message: '请选择！' }]}
          >
            <Select options={'resolutionTypeEnum'} />
          </Item>
        )}
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
          <Item
            label={'章程文件'}
            name={'constitutionFileIds'}
            rules={[{ required: true, message: '请上传！' }]}
          >
            <DataUpload maxCount={1}></DataUpload>
          </Item>
        )}
        {bizType === 'ZL' && isChangYe && (
          <Item
            label={'租赁物文件类型'}
            name={'leaseItemFileType'}
            rules={[{ required: true, message: '请选择！' }]}
          >
            <Select options={'contractLeaseItemFileTypeEnum'} />
          </Item>
        )}
        <Item
          label={'存量风险敞口（元）'}
          name={'stockRiskExposure'}
          rules={[{ required: true, message: '请输入存量风险敞口！' }]}
        >
          <InputNumber
            {...getInputNumberAmountProps()}
            style={{ width: '100%' }}
            disabled
          ></InputNumber>
        </Item>
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
          // rules={[{ required: BL_ZR, mesage: '请选择指定联系人！' }]}
        >
          <Select
            allowClear
            options={contractList}
            onSearch={getContactList}
            onFocus={() => getContactList()}
            fieldNames={{ value: 'id', label: 'name' }}
          />
        </Item>
        <Item
          label={'租金往来方'}
          name="rentConcatAccountId"
          rules={[{ required: true, message: '请选择' }]}
        >
          <ApiSelect
            api={Api.getHighSeasCustomersList}
            labelInValue
            searchField="customerName"
            params={{ contractId }}
          />
        </Item>
        <Item
          label={'是否上报征信'}
          name={'isReport'}
          rules={[{ required: true, message: '请选择是否上报征信！' }]}
        >
          <Select
            options={[
              {
                value: 1,
                label: '是',
              },
              {
                value: 0,
                label: '否',
              },
            ]}
          />
        </Item>
        <Item hidden name="id">
          <Input />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
