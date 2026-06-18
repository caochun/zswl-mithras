import { Space, Radio, Tooltip } from 'antd'
import { observer, getQuery } from '@zswl/admin'
import { Page, Button } from '@zswl/components'
import { useEffect, useMemo } from 'react'
import QueryReport from './QueryReport'
import { getKeyOptionsLabelMapPlus, userIsProjSponsor } from '@/utils'
import Store from './store'
import styles from './index.less'
import { CustomerExternalPublicInfo as Public } from '@/components/Customer/CustomerEntries'

const Index = ({ params = {}, query: { canEditFlags = 'true', businessVersion } }) => {
  const store = useMemo(() => {
    return new Store({})
  }, [])

  const { id } = params
  const { page, submit, downReport, activeTag, clientGroup, onTabChange, curClientId } = store

  const { canModify, canSubmit, sponsorUserId, approvalStatus, clientInfos = [] } = page.getData()

  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'

  // 主办+ 非跨月
  const canEditFlagsFormAuth =
    canEditFlags === 'true' && canModify && userIsProjSponsor(sponsorUserId)

  useEffect(() => {
    clientInfos && store.setClientGroup(clientInfos)
  }, [JSON.stringify(clientInfos)])

  return (
    <Page store={store} current={'外部公开信息详情'} header={null} params={{ id, businessVersion }}>
      <div className={styles.nav}>
        <div className={styles.title}></div>
        <div className={styles.action}>
          <Space>
            {/* 审批通过 */}
            {approvalStatus === 'APPROVAL_PASS' && <Button onClick={downReport}>下载报告</Button>}
            {!isFormApproval && (
              <Button
                type="primary"
                onClick={submit}
                disabled={!userIsProjSponsor(sponsorUserId) || !canSubmit}
              >
                提交审批
              </Button>
            )}
          </Space>
        </div>
      </div>
      <div className={styles.tabWrap}>
        <Radio.Group value={activeTag} onChange={onTabChange}>
          <Space>
            <Radio.Button key={'-1'} value={'-1'} className={styles.buttonClamp}>
              外部信息查询报告
            </Radio.Button>
            {Object.keys(clientGroup).map((key) => {
              return clientGroup[key].map(
                ({ id: autoId, clientId, clientName, clientRole, clientType }, index) => {
                  return (
                    <Radio.Button
                      value={`${autoId}_${clientId}`}
                      key={`${autoId}_${clientId}`}
                      className={styles.buttonClamp}
                    >
                      <Tooltip title={clientName}>
                        {`${getKeyOptionsLabelMapPlus('clientRole')[clientRole]}${
                          clientRole === 'MAIN_LESSEE' || clientGroup[key].length < 2
                            ? ''
                            : index + 1
                        }`}
                        ：{clientName}
                      </Tooltip>
                    </Radio.Button>
                  )
                }
              )
            })}
          </Space>
        </Radio.Group>
      </div>
      <div className={styles.content}>
        {activeTag === '-1' ? (
          <QueryReport
            canEditFlag={canEditFlagsFormAuth}
            clientGroup={clientGroup}
            store={store}
          ></QueryReport>
        ) : (
          <Public
            ids={curClientId}
            showHeaderAction={false}
            canBtnAction={false}
            store={store}
          ></Public>
        )}
      </div>
    </Page>
  )
}

export default observer(Index)
