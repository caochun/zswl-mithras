import { App } from '@zswl/components'

export const BlackGrayProgressLine = ({ finishedCount, totalCount }) => {
  const value = totalCount ? `${finishedCount}/${totalCount}` : '-'
  return value
}

export const BlackGrayApprovalStatus = ({ value, suggest, status = 'auditStatusEnum' }) => {
  const { label } = App.matchOption(status, value) || {}
  return suggest ? `${label || '-'} (${suggest})` : label || '-'
}

export const BlackGrayUser = ({ value }) => {
  const { label } = App.matchOption('userList', value) || {}
  return label || value || '-'
}

export const BlackGrayFormat = {
  ApprovalStatus: BlackGrayApprovalStatus,
  ProgressLine: BlackGrayProgressLine,
  User: BlackGrayUser,
}
