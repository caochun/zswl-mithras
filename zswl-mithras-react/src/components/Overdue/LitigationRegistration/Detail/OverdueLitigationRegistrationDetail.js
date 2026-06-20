import { observer, getQuery } from '@zswl/admin'
import { Button, Form, Input, Page, Select } from '@zswl/components'
import { useEffect, useMemo } from 'react'
import Store from './store'
import DefendantInfo from './DefendantInfo'
import ProgressTheCase from './ProgressTheCase'
import TrialInformation from './TrialInformation'
import DataList from './DataList'
import { Col, Row } from 'antd'
import { getContractList } from '../../CollectionModal/OverdueCollectionModal'

const Detail = ({ params: { id }, query: { bizType, newProject, canEditFlags = 'true' } }) => {
  const store = useMemo(() => {
    return new Store({ id })
  }, [id])
  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'
  const baseInfoDetail = store.page.getData()
  const { clientId } = baseInfoDetail
  // 按钮权限控制，审批流后端控制 + 是否主办人
  const canEditFlagsFormAuth = canEditFlags === 'true'

  return (
    <Page
      store={store}
      params={{ id, newProject: newProject === 'true', isFormApproval }}
      header={{
        fixed: true,
        extra: (
          <Button.Save type="primary" onClick={() => store.save()}>
            保存
          </Button.Save>
        ),
      }}
    >
      <Form column={3} initialValues={baseInfoDetail} store={store.form}>
        <Row gutter={16}>
          <Col span={8}>
            <Form.Item label="客户名称" name="clientName">
              <Input disabled />
            </Form.Item>
          </Col>
          <Col span={16}>
            <Form.Item label="合同编号" name="contract" rules={[{ required: true }]}>
              <Select
                options={() => getContractList(clientId)}
                labelInValue
                mode="multiple"
                onChange={store.contractChange}
              />
            </Form.Item>
          </Col>
        </Row>
        <DefendantInfo canEdit={canEditFlagsFormAuth} store={store} />
        <ProgressTheCase store={store} />
        <TrialInformation store={store} />
        <DataList mainId={id} />
      </Form>
    </Page>
  )
}

export default observer(Detail)
