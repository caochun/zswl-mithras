import { useMemo } from 'react'
import ReviewStore from '@/components/Project/ReviewDetail/store'
import BaseInfo from '@/components/Project/ReviewDetail/BaseInfo'
import QuotationScheme from '@/components/Project/ReviewDetail/QuotationScheme'
import CashFlowStatement from '@/components/Project/ReviewDetail/CashFlowStatement'
import DataList from '@/components/Project/ReviewDetail/Data'
import ReviewData from '@/components/Project/ReviewDetail/Report'

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
