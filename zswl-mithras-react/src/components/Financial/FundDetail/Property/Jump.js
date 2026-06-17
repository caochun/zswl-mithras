import { history } from '@zswl/admin'
import { Tooltip } from 'antd'

const JumpDetail = ({ title, isChange, path }) => {
  if (title) {
    const JumpA = (
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
          {JumpA}
        </Tooltip>
      </div>
    )
  } else {
    return '-'
  }
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
