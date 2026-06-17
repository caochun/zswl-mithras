import {
  Page,
  Button,
  App,
  FormStore,
  PageStore,
  Select,
  Form,
  Descriptions,
} from '@zswl/components'
import { history, makeAutoObservable, observer } from '@zswl/admin'
import LoginInfo from '@/components/BlackGray/Manage/LoginInfo'
import { useMemo } from 'react'
import { Col, Input, Row, TreeSelect, message } from 'antd'
import warehouseRuleApi from '@/api/blackList/warehouseRuleApi'
import { getDescColumns } from '@/utils'
import ALL_COLUMNS from '@/components/BlackGray/Columns'

const ColItem = ({ children, ...rest }) => {
  return (
    <Col span={8}>
      <Form.Item {...rest}>{children}</Form.Item>
    </Col>
  )
}
class Store {
  constructor({ source }) {
    makeAutoObservable(this)
    this.source = source
  }
  page = new PageStore({
    request: async (params) => {
      if (!params?.id) return {}
      return await warehouseRuleApi.postConfigDetail(params)
    },
  })

  form = new FormStore({})
  save = async (e) => {
    const { id } = this.page.getParams()
    const params = await this.form.submit()
    const func = id ? warehouseRuleApi.postConfigModify : warehouseRuleApi.postConfigAdd
    const recordId = await func({ ...params, source: this.source, id })
    message.success(`保存成功`)
    history.goBack()
  }
}

function Id({ params, query }) {
  const { view, source } = query
  const store = useMemo(() => new Store({ source }), [source])
  const detail = store.page.getData()

  const current = params.id ? (view ? '查看详情' : '修改') : '新增'

  return (
    <Page
      current={current}
      params={params}
      store={store.page}
      header={{
        extra: [
          !view && (
            <Button.Save onClick={store.save} key="save">
              保存
            </Button.Save>
          ),
        ],
      }}
    >
      {view && (
        <Descriptions
          dataSource={detail}
          items={getDescColumns(ALL_COLUMNS, [
            '规则编号',
            '规则名称',
            '黑灰标识',
            '适用业务',
            '适用机构',
          ])}
        />
      )}
      {!view && (
        <Form store={store.form} initialValues={detail}>
          <Row gutter={12}>
            {params.id && (
              <ColItem label={'规则编号'} name="ruleNumber" rules={[{ required: true }]}>
                <Input disabled />
              </ColItem>
            )}
            <ColItem label={'规则名称'} name="ruleName" rules={[{ required: true }]}>
              <Input.TextArea maxLength={100} />
            </ColItem>
            <ColItem label={'黑灰标识'} name="blackGrayType" rules={[{ required: true }]}>
              <Select options="blackGrayTypeEnum" disabled={params.id} />
            </ColItem>
            <ColItem label={'适用业务'} name="suitBusiness" rules={[{ required: true }]}>
              <Select options="blackGrayBusinessTypeEnum" mode="multiple" />
            </ColItem>
            <ColItem label={'适用机构'} name="suitOrg" rules={[{ required: true }]}>
              <Select options="blackGrayOrgEnum" mode="multiple" />
            </ColItem>
          </Row>
        </Form>
      )}

      <LoginInfo />
    </Page>
  )
}

export default observer(Id)
