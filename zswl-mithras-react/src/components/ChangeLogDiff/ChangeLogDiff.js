import fileApi from '@/utils/api/fileApi'
import { observer } from '@zswl/admin'
import { FileTable } from '../Table'
import { Tooltip } from 'antd'
import { useRef } from 'react'
import styles from './style.less'

function findPanelDom(node) {
  if (!node.parentNode) {
    return null
  }
  if (node.parentNode.classList.contains('ant-collapse-item')) {
    return node.parentNode
  }
  return findPanelDom(node.parentNode)
}
function Index({ version, moduleType, options, functionCode, functionCodeList }) {
  const ref = useRef()
  const hasChange = useRef(false)
  const Render = ({ name, data }) => {
    const val = data[name]
    const { isChange } = data
    if (isChange && !hasChange.current) {
      hasChange.current = true
      const panel = findPanelDom(ref.current)
      if (panel) {
        const header = panel.querySelector('.ant-collapse-header-text')
        if (header) {
          const html = header.innerHTML
          header.innerHTML = `${html}  <span class='${styles.dot}'></span>`
        }
      }
    }
    const content = isChange ? <span style={{ color: 'red' }}>{val}</span> : val
    return (
      <Tooltip title={val} placement="topLeft">
        {content}
      </Tooltip>
    )
  }
  return (
    <div ref={ref}>
      <FileTable
        params={{ moduleType, mainId: version }}
        canBatchDownload={false}
        enumType={options}
        autoRequest
        functionCodeList={functionCodeList}
        columns={[
          {
            title: '文件名称',
            render(data) {
              return <Render data={data} name="name" />
            },
          },
          {
            title: '上传人',
            render(data) {
              return <Render data={data} name="updateByName" />
            },
          },
          {
            title: '上传时间',
            render(data) {
              return <Render data={data} name="updateTime" />
            },
          },
          {
            title: '变更类型',
            render({ changeType, updateByName }) {
              if (updateByName) {
                if (changeType) {
                  return (
                    <span style={{ color: 'red' }}>
                      {{ ADD: '新增', REMOVE: '删除' }[changeType]}
                    </span>
                  )
                }
                return '-'
              }
            },
          },
        ]}
        tableApi={() =>
          fileApi.postFileVersionCompare(
            {
              versionId: version,
              moduleType,
            },
            functionCode
          )
        }
      />
    </div>
  )
}

export default observer(Index)
