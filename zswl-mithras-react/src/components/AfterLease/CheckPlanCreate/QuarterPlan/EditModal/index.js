import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { Input } from 'antd'
import { ApiSelect, FounderSelect } from '@/components/Select'
import api from '@/api/afterLease/checkPlanCreateApi'

const { Item } = Form

function CreateModal({ store }) {
  const [form] = Form.useForm()

  return (
    <Modal title={'编辑'} store={store} okText={'确定'} destroyOnClose width={580}>
      <Form form={form} labelCol={{ span: 6 }} preserve={false}>
        <Item label={'客户编号'} name={'clientCode'}>
          <Input disabled></Input>
        </Item>
        <Item label={'客户名称'} name="clientName">
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
        <Item
          label={'本次是否需要检查'}
          name="check"
          rules={[{ required: true, message: '请选择' }]}
        >
          <Select options="trueOrFalse" placeholder="请选择" />
        </Item>
        <Item dependencies={['check']} noStyle>
          {({ getFieldValue }) => {
            const check = getFieldValue('check')
            if (check) {
              return (
                <>
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
                    rules={[{ required: true, message: '请选择' }]}
                  >
                    <Select options="afterLeaseCheckReportTypeEnum" placeholder="请选择" />
                  </Item>
                </>
              )
            }
          }}
        </Item>
        <Item dependencies={['check', 'checkWay']} noStyle>
          {({ getFieldValue }) => {
            const check = getFieldValue('check')
            const checkWay = getFieldValue('checkWay')
            if (check && checkWay != 'OFFSITE') {
              return (
                <Item
                  label={'协查风控经理'}
                  name="riskManagerId"
                  rules={[{ required: true, message: '请选择' }]}
                >
                  <ApiSelect api={api.postRiskManagerList} labelInValue />
                </Item>
              )
            }
          }}
        </Item>
        <Item hidden name="clientId">
          <Input></Input>
        </Item>
        <Item hidden name="id">
          <Input></Input>
        </Item>
        <Item hidden name="projectReviewId">
          <Input></Input>
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(CreateModal)
