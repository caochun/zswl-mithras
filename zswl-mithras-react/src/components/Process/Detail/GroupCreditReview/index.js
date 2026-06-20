import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import { CreditReviewDetail as CreateReview } from '@/components/Credit/ReviewDetailEntries'

const Index = (props) => {
  const { canEditFlag, subModule, id, businessVersion } = props
  const renderContractType = useMemo(() => {
    return (
      <CreateReview
        params={{ id }}
        query={{
          canEditFlags: canEditFlag ? 'true' : 'false',
          subModule,
          businessVersion,
        }}
      />
    )
  }, [subModule, canEditFlag, businessVersion])

  return renderContractType
}
export default observer(Index)
