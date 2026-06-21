import { Tabs, Result, Space, Input } from 'antd'
import { useMemo, useState, useRef } from 'react'
import { Page, Button, Modal, Form } from '@zswl/components'
import Public from '../ExternalPublicInfo/CustomerExternalPublicInfo'
import Financial from '../FinancialReport'
import Basic from './Basic'
import Store from './store'
import { observer, getQuery } from '@zswl/admin'
import EvaluationInfo from './EvaluationInfo'
import CheckBusiness from '../CommerceCheck/CustomerCommerceCheck'
import { CreditReportSearchModal } from '@/components/Credit/CreditReportSearchEntries'

const { TabPane } = Tabs
function Index({
  params: { id },
  query: {
    clientType,
    businessVersion,
    canEditFlag = true,
    domesticOrAbroad,
    startUserId,
    processInstanceId,
    isCreate,
    modelKey,
  },
}) {
  const childRef = useRef(null);
  const child2Ref = useRef(null);
  const [show, setShow] = useState(true);
  let canEdit = canEditFlag
  const isFormApproval = getQuery('typeId') == 'approval'
  const store = useMemo(() => new Store(), [])

  const pageData = store.page.getData()
  const baseInfo = store?.baseStore?.baseInfo
  canEdit = canEdit && pageData.canEdit

  const syncInfo = () => {
    // 原详情逻辑
    if(clientType === 'CORPORATION' && domesticOrAbroad === 'DOMESTIC' && !isFormApproval && canEdit && childRef.current){
      childRef.current.sync();
    }
    // 原公开信息逻辑
    if(canEdit && child2Ref.current){
      child2Ref.current.sync();
    }
  }

  const commonExtra = (
    <Space>
      <CreditReportSearchModal
        params={{ bizSource: 'clientList', clientId: id, clientName: baseInfo?.clientName }}
      />
      {/* 如果只显示工商信息模块的话，那就不给展示版本日志按钮 */}
      {((clientType === 'CORPORATION' && !pageData.showCommerceInfo) ||
        clientType === 'NORMAL') && <Button onClick={() => store.changeLog(id)}>版本日志</Button>}
      {/* 客户状态 新建、释放 才能 提交权限申请 */}
      {clientType === 'CORPORATION' && ['NEW', 'RELEASE'].includes(pageData.clientStatus) && (
        <Button onClick={() => store.authorityModal.open()} type="primary">
          提交权限申请
        </Button>
      )}
      {/* 客户状态 生效 才能 客户信息变更 */}
      {((clientType === 'CORPORATION' &&
        ['TAKE_EFFECT'].includes(pageData.clientStatus) &&
        pageData.authorityLevel === 3) ||
        clientType === 'NORMAL') && (
        <Button onClick={() => store.clientEffect(id)} disabled={!canEdit} type="primary">
          客户信息变更
        </Button>
      )}
    </Space>
  )
  const onVisible = (activeKey) => {
    setShow(activeKey === '1' || activeKey === '2')
  }
  const formApproval = [
    ['ClientAuthorityCreateFlow', 'ClientAuthorityModifyFlow'].includes(modelKey) && (
      <CheckBusiness clientId={id} />
    ),
  ]
  return (
    <Page store={store} header={null} params={{ id, processInstanceId }}>
      {pageData.message ? (
        <Result status="403" title="抱歉，您暂无查看权限" subTitle={`${pageData.message}`} />
      ) : (
        <Tabs
          defaultActiveKey="1"
          tabBarExtraContent={show && <Button onClick={syncInfo}>同步当前页</Button>}
          onChange={onVisible}
        >
          <TabPane tab="基本信息" key="1">
            <Basic
              ref={childRef}
              id={id}
              clientType={clientType}
              canEditFlag={canEdit}
              domesticOrAbroad={domesticOrAbroad}
              businessVersion={businessVersion}
              commonExtra={!isFormApproval ? commonExtra : formApproval}
              processInstanceId={processInstanceId}
              pageData={pageData}
              startUserId={startUserId}
              rootStore={store}
              isCreate={isCreate}
            />
          </TabPane>
          {clientType == 'CORPORATION' && !isFormApproval && !pageData.showCommerceInfo && (
            <>
              <TabPane tab="公开信息" key="2">
                <Public ids={id} canEditFlag={canEdit} ref={child2Ref}/>
              </TabPane>
              <TabPane tab="财务报表" key="3">
                <Financial
                  id={id}
                  canEditFlag={canEditFlag && [2, 3].includes(pageData.authorityLevel)}
                />
              </TabPane>
            </>
          )}
          {!isFormApproval && !pageData.showCommerceInfo && (
            <TabPane tab="评级信息" key="4">
              <EvaluationInfo store={store} />
            </TabPane>
          )}
        </Tabs>
      )}

      <Modal
        title="提交权限申请"
        store={store.authorityModal}
        okText="继续提交"
        cancelText="取消"
      >
        <Form>
          <Form.Item label="审批意见" name="opinion">
            <Input.TextArea
              autoSize={{ minRows: 4, maxRows: 8 }}
              maxLength={1000}
              showCount
              placeholder="请输入审批意见"
            />
          </Form.Item>
        </Form>
      </Modal>
    </Page>
  )
}
export default observer(Index)
