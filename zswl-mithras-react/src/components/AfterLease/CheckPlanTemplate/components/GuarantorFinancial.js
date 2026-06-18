import { observer } from '@zswl/admin'
import { Button, Empty, Radio } from 'antd'
import { useEffect, useState, useMemo } from 'react'
import Financial from './Financial'
import { userIsProjSponsor } from '@/utils'

function Index({
  clientList = [],
  id,
  canEdit,
  activeTag,
  businessVersion,
  canEditFlags,
  isFormApproval,
  canImport,
}) {
  // 财报导入权限
  const canImportFinancial = useMemo(() => {
    const belongSponsorId = clientList?.[0]?.belongSponsorId
    const condition = belongSponsorId === null || userIsProjSponsor(belongSponsorId)
    if (isFormApproval) return (canEditFlags === 'true' || canImport) && condition
    return condition
  }, [JSON.stringify(clientList)])

  const [active, setActive] = useState()

  useEffect(() => {
    const clientId = clientList?.[0]?.clientId
    clientId && setActive(clientId)
  }, [JSON.stringify(clientList)])

  const Content = useMemo(() => {
    if (!active) return <Empty></Empty>
    return (
      <Financial
        key={active}
        id={active}
        canEdit={canEdit}
        projectId={id}
        activeTag={activeTag}
        businessVersion={businessVersion}
        canImportFinancial={canImportFinancial}
      />
    )
  }, [active, activeTag, id, businessVersion, canImportFinancial])
  return (
    <div>
      <div>
        <Radio.Group
          style={{ marginBottom: 16 }}
          onChange={(e) => {
            console.log(e.target.value)
            setActive(e.target.value)
          }}
          value={active}
        >
          {(clientList || []).map(({ clientName, clientId }) => (
            <Radio.Button value={clientId} key={clientId}>
              {clientName}
            </Radio.Button>
          ))}
        </Radio.Group>
      </div>
      {Content}
    </div>
  )
}

export default observer(Index)
