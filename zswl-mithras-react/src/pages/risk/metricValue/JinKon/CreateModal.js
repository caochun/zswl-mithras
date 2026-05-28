import { Form, Modal, Select } from '@zswl/components'
import { observer } from '@zswl/admin'
import { DatePicker, Input, InputNumber, Row, Col } from 'antd'
import { rules } from '@/utils'
import { ApiSelect } from '@/components'
import Amount from '@/components/Amount'
import Api from '@/api/risk/metricValue/controlGliy'

const { TextArea } = Input

const { Item } = Form

const Index = ({ store }) => {
  const transformResult = (data) => {
    const res = []
    data.map((item) => {
      res.push({
        label: item,
        value: item,
      })
    })
    return res
  }

  return (
    <Modal
      width={1000}
      propsBy={(data) => {
        return {
          title: data ? '编辑' : '创建',
        }
      }}
      store={store.createModal}
      okText={'确定'}
      destroyOnClose
    >
      <Form
        labelAlign="right"
        layout={'horizontal'}
        labelCol={{ span: 5 }}
        preserve={false}
        initialValues={{
          subjectPartyName: '浙江浙商融资租赁有限公司',
          tradeCategoryParentName: 'TRZL',
          tradeCategoryName: 'RZZL',
        }}
      >
        <Item name="subjectPartyName" label="主体机构名称">
          <Input disabled />
        </Item>
        <Item name="tradePartyName" label="交易对手名称" rules={[rules.required()]}>
          <ApiSelect
            api={Api.postRelatedClients}
            transformResult={transformResult}
            searchField="name"
            debounceSearch
          ></ApiSelect>
        </Item>
        <Item name="tradeCategoryParentName" label="一级分类" rules={[rules.required()]}>
          <Select options={'gljyReportCategoryOne'} />
        </Item>
        <Item name="tradeCategoryName" label="二级分类" rules={[rules.required()]}>
          <Select options="gljyReportCategoryTwo" />
        </Item>
        <Item name="amount" label="交易金额(万)" rules={[rules.required(), Amount.ruleFunc()]}>
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
        <Item name="tradeDate" label="交易日期" rules={[rules.required()]}>
          <DatePicker format={'yyyy-MM-DD'} />
        </Item>
        <Item
          name="tradePartyAssets"
          label={
            <div style={{ marginTop: 10, marginBottom: 10, fontSize: 10 }}>
              <div>交易对手上一年末</div>
              <div>审计净资产(万)</div>
            </div>
          }
          rules={[Amount.ruleFunc()]}
        >
          <Amount>
            <InputNumber style={{ width: '100%' }} />
          </Amount>
        </Item>
        <Item name="purpose" label="交易目的" rules={[rules.required()]}>
          <TextArea />
        </Item>
        <Item name="level" label="关联交易级别" rules={[rules.required()]}>
          <Select options="gljyReportLevel" />
        </Item>
        <Item noStyle dependencies={['level']}>
          {({ getFieldValue }) => {
            const level = getFieldValue('level')
            if (level === 'IMPORTANT') {
              return (
                <Item name="importantReason" label="重大交易原因" rules={[rules.required()]}>
                  <Select options="gljyReportImportantReason" />
                </Item>
              )
            }
          }}
        </Item>

        <Item name="description" label="交易描述" rules={[rules.required()]}>
          <TextArea />
        </Item>
        <Item name="opinion" label="董事会/委员会意见">
          <TextArea />
        </Item>

        <Item name="risk" label="风险/影响">
          <TextArea />
        </Item>

        <Item name="id" hidden>
          <Input />
        </Item>
      </Form>
    </Modal>
  )
}

export default observer(Index)
