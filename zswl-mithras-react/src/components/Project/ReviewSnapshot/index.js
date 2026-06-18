import { useMemo } from 'react'
import BaseInfo from '../ReviewDetail/BaseInfo'
import CashFlowStatement from '../ReviewDetail/CashFlowStatement'
import DataList from '../ReviewDetail/Data'
import QuotationScheme from '../ReviewDetail/QuotationScheme'
import ReviewData from '../ReviewDetail/Report'
import ReviewStore from '../ReviewDetail/store'

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
