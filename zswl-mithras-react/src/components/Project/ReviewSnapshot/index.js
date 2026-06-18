import { useMemo } from 'react'
import {
  ReviewDetailBaseInfo as BaseInfo,
  ReviewDetailCashFlowStatement as CashFlowStatement,
  ReviewDetailDataList as DataList,
  ReviewDetailQuotationScheme as QuotationScheme,
  ReviewDetailReport as ReviewData,
  ReviewDetailStore as ReviewStore,
} from '@/components/Project/ReviewDetailEntries'

const ReviewSnapshot = ({ id }) => {
  const reviewStore = useMemo(() => new ReviewStore({}), [])

  return (
    <>
      <BaseInfo id={id} canEdit={false} rootStore={reviewStore} isFormAdjust />
      <QuotationScheme id={id} canEdit={false} isFormAdjust rootStore={reviewStore} />
      <CashFlowStatement id={id} canEdit={false} rootStore={reviewStore} />
      <ReviewData id={id} canEdit={false} rootStore={reviewStore} title={'项目评审资料'} />
      <DataList id={id} canEdit={false} rootStore={reviewStore} />
    </>
  )
}

export default ReviewSnapshot
