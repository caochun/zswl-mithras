import customerRatApi from '@/api/customer/customerRat/customerRatApi'

export const getApprovalText = async ({ id }) => {
  const res = await customerRatApi.postClientReport({ id })
  const { adjustEventList, qualitativeList = [], quantitativeList = [] } = res
  const approvalList = [
    ...qualitativeList,
    ...quantitativeList,
    ...adjustEventList.map((v) => ({ ...v, _type: 'adjustEventList' })),
  ]
  const notApproval = approvalList
    .filter((item) => item.approvalStatus === false)
    .map(({ fieldComment, approvalOpinion, approvalStatus, _type }) => {
      const approvalStatusText = approvalStatus ? '通过' : '不通过'
      if (_type === 'adjustEventList') {
        return `<div>调整项${approvalStatusText}，审批说明：${approvalOpinion ?? '无'};</div>`
      }
      return `<div>指标名称：${fieldComment},审批意见：${approvalStatusText}.审批说明：${
        approvalOpinion ?? '无'
      };</div>`
    })
  return notApproval.join('')
}

export const indexCheck = async (params) => {
  await customerRatApi.postIndexCheck(params)
}
