import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { DatePicker, Tooltip } from 'antd'
import IconFont from '@/components/Icon'
import DataUpload from '@/components/DataUpload'
import { bizTypeMapText } from '../../bizTypeConfig'

const { Item } = Form

const iconStyle = {
  marginLeft: 5,
  color: 'blue',
}

function Index({ $createModal, paymemntList, bizType }) {
  const [form] = Form.useForm()

  return (
    <Modal title={`新增借据`} store={$createModal} destroyOnClose>
      <Form form={form} labelCol={{ span: 7 }} preserve={false}>
        <Item
          label={'付款申请编号'}
          name={'paymentId'}
          rules={[{ required: true, message: '请选择付款申请编号！' }]}
        >
          <Select
            fieldNames={{ label: 'paymentCode', value: 'paymentId' }}
            allowClear
            debounceSearch
            options={paymemntList}
            filterOption={false}
            style={{ maxWidth: '100%' }}
            showSearch
          />
        </Item>
        <Item
          name="receiptStartDate"
          label={
            <Tooltip title="该字段表明该借据的实际计息开始日，后续将用于收入分摊及其他财务处理。">
              借据起租日期
              <IconFont type="icon-icon_info" style={iconStyle}></IconFont>
            </Tooltip>
          }
          rules={[{ required: true, message: '请选择' }]}
          transform={(value) => value && value.format('YYYY-MM-DD')}
        >
          <DatePicker style={{ width: '100%' }}></DatePicker>
        </Item>
        <Item
          label={
            <Tooltip title="注意：系统只取租金项目">
              实际{bizTypeMapText[bizType]?.rentTitle}
              <IconFont type="icon-icon_info" style={iconStyle}></IconFont>
            </Tooltip>
          }
          name={'file'}
          rules={[{ required: true, message: '请上传文件!' }]}
        >
          <DataUpload accept=".xlsx" maxCount={1}></DataUpload>
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
