import { Amount } from '@/components/Format'
import { Form, Modal, Select } from '@zswl/components'
import { observer } from '@zswl/admin'
import { DatePicker, Input, InputNumber, Row, Col } from 'antd'
import { rules } from '@/utils'
import { disabledQuarterEndMonth } from '../../MetricValueShared/dateUtils'

const { Item } = Form

const targetSubjectMap = {
  ZSZL_SXLYW_BL_BL: '应收账款',
  ZSZL_SXLYW_RZZL_RZZL: '租赁物',
}

const RiskMetricControlCreateModal = ({ store }) => {
  const onBizTypeChange = (value) => {
    const { formStore } = store.createModal
    formStore.setFieldValue('targetSubject', targetSubjectMap[value])
  }

  return (
    <Modal
      width={600}
      propsBy={(data) => {
        return {
          title: data ? '编辑' : '创建',
        }
      }}
      store={store.createModal}
      okText={'确定'}
      destroyOnClose
    >
      <Form labelAlign="right" layout={'horizontal'} labelCol={{ span: 7 }} preserve={false}>
        <Item name="dataMonth" label="数据时点" rules={[rules.required()]}>
          <DatePicker picker={'month'} disabledDate={disabledQuarterEndMonth} />
        </Item>
        <Item name="bizType" label="业务类型" rules={[rules.required()]}>
          <Select options="jzdReportBizType" onChange={onBizTypeChange} />
        </Item>
        <Item name="createType" label="创建类型" rules={[rules.required()]}>
          <Select options="jzdReportCreateType" />
        </Item>
        <Item name="targetSubject" label="标的物名称" rules={[rules.required()]}>
          <Input disabled />
        </Item>
        <Item name="bizAmountTotal" label="业务总额(万元)" rules={[rules.required()]}>
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
        <Item name="bizAmountLeft" label="业务余额(万元)" rules={[rules.required()]}>
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
        <Item name="clientName" label="客户名称" rules={[rules.required()]}>
          <Input />
        </Item>
        <Item name="clientSameTrade" label="是否同业客户" rules={[rules.required()]}>
          <Select options="yesOrNo" />
        </Item>
        <Item name="economicComposition" label="企业经济成分" rules={[rules.required()]}>
          <Select options="jzdReportEconomyComposition" />
        </Item>
        <Item name="sponsorOrgName" label="主办业务部门" rules={[rules.required()]}>
          <Input />
        </Item>
        <Item name="bizStartDate" label="业务起始日期" rules={[rules.required()]}>
          <DatePicker format={'yyyy-MM-DD'} />
        </Item>
        <Item name="bizEndDate" label="业务到期日" rules={[rules.required()]}>
          <DatePicker format={'yyyy-MM-DD'} />
        </Item>
        <Item name="ensureValue" label="合同保证价值(万元)" rules={[rules.required()]}>
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
        <Item name="guaranteeName" label="担保人名称">
          <Input />
        </Item>
        <Item name="yjtjzValue" label="已计提减值(万元)" rules={[rules.required()]}>
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
        <Item name="overdueDays" label="逾期天数" rules={[rules.required()]}>
          <InputNumber style={{ width: '100%' }} />
        </Item>
        <Item name="overdueValue" label="逾期金额" rules={[rules.required()]}>
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
        <Item name="assetsCategory" label="资产质量分类" rules={[rules.required()]}>
          <Select options="jzdReportAssetsCategory" />
        </Item>
        <Item name="id" hidden>
          <Input />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(RiskMetricControlCreateModal)
