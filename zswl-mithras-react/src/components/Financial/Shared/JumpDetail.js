import { history } from '@zswl/admin'
import { Tooltip } from 'antd'

const JumpDetail = ({ title, isChange, path }) => {
  if (!title) {
    return '-'
  }

  const jumpLink = (
    <a
      style={{ color: isChange ? 'red' : '#2552e6' }}
      onClick={() => history.push(path)}
      target="_blank"
    >
      {title}
    </a>
  )

  return (
    <div>
      <Tooltip title={title} placement="topLeft">
        {jumpLink}
      </Tooltip>
    </div>
  )
}

export const JumpContractDetail = ({ title, isChange, contractId }) => {
  return (
    <JumpDetail
      title={title}
      isChange={isChange}
      path={`/contract/list/detail/${contractId}`}
    ></JumpDetail>
  )
}

export const JumpProjDetail = ({ title, isChange, projId }) => {
  return (
    <JumpDetail
      title={title}
      isChange={isChange}
      path={`/project/review/detail/${projId}?typeId=review`}
    ></JumpDetail>
  )
}
