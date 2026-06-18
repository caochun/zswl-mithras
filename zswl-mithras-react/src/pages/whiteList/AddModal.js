import appraisalCompanyApi from '@/api/whiteList/appraisalCompanyApi'
import { observer } from '@zswl/admin'
import { Modal, Form, Select, Input, DatePicker } from '@zswl/components'

const { Item } = Form

/**
 * 新增评估机构 Modal 组件
 * 根据后端接口定义，新增时只需要统一社会信用代码
 * @param {Object} store - 页面 store 实例
 */
const AddModal = ({ store }) => {
  const [form] = Form.useForm()

  const getList = async (companyName) => {
    const res = await appraisalCompanyApi.queryCompany({ companyName, pageSize: 20 })
    return res
  }
  const companyChange = async (value, options) => {
    form.setFieldsValue(options)
  }
  return (
    <Modal title="新增评估机构" store={store.addModal} width={500} destroyOnClose>
      <Form labelCol={{ span: 8 }} wrapperCol={{ span: 16 }} form={form}>
        <Item label="评估机构名称" name="companyName">
          <Select
            options={getList}
            onChange={companyChange}
            debounceSearch
            placeholder="请输入需要查询的评估机构名称"
            fieldNames={{ label: 'companyName', value: 'creditCode' }}
          />
        </Item>

        <Item
          label="统一社会信用代码"
          name="creditCode"
          rules={[{ required: true, message: '请输入统一社会信用代码' }]}
        >
          <Input placeholder="请输入统一社会信用代码" maxLength={18} disabled />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(AddModal)
