import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { DatePicker, Input } from 'antd'
import { useState, useEffect } from 'react'
import { getHeaderWithFunctionCode } from '@/pages/creditManage/creditTable/Tab/config'
import Api from '@/pages/creditManage/creditTable/Tab/Level5/api'
import { compareTableData } from '@/utils'

const { Item } = Form

function Index({ store }) {
  const [form] = Form.useForm()
  const [accountIdList, setAccountIdList] = useState([])
  const getCodeList = async (value) => {
    const result = await Api.getZhangHuList(
      {
        channel: 'EDIT',
        onlyToBeReportFlag: 0,
        paymentApplyCode: value,
      },
      getHeaderWithFunctionCode({
        channel: 'EDIT',
        humpPath: 'crAccountList',
      })
    )
    const { newDetail } = compareTableData(result.list)
    setAccountIdList(newDetail)
  }
  useEffect(() => {
    getCodeList()
  }, [])
  return (
    <Modal title={'创建'} store={store.createModal} okText={'确定'} destroyOnClose>
      <Form form={form} labelCol={{ span: 8 }} preserve={false}>
        <Item label={'编号'} name={'accountId'} rules={[{ required: true, message: '请选择' }]}>
          <Select
            // onChange={getCodeList}
            allowClear
            placeholder="请选择"
            options={accountIdList}
            onSearch={getCodeList}
            fieldNames={{ value: 'id', label: 'paymentApplyCode' }}
          />
        </Item>
        <Item label={'五级分类'} name={'fiveClass'} rules={[{ required: true, message: '请选择' }]}>
          <Select placeholder="请输入" allowClear options={'crFiveClass'} />
        </Item>
        <Item
          label={'五级分类认定日期'}
          name={'identificationDate'}
          rules={[{ required: true, message: '请选择' }]}
        >
          <DatePicker placeholder="请选择" style={{ width: '100%' }} />
        </Item>
        <Item label={'新增原因'} name={'reason'} rules={[{ required: true, message: ' 请输入' }]}>
          <Input.TextArea rows={3} />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
