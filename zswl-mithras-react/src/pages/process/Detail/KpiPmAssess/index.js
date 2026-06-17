import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import PmAssess from '@/components/Kpi/PmAssess/EditModal/Content'

const Index = (props) => {
  const { canEditFlag } = props
  const renderContractType = useMemo(() => {
    return (
      <div>
        <PmAssess {...props} canEditFlags={canEditFlag ? 'true' : 'false'} />
      </div>
    )
  }, [canEditFlag])

  return renderContractType
}
export default observer(Index)
