import { observer } from '@zswl/admin'
import styles from './style.less'
import { forwardRef, useImperativeHandle, useMemo, useRef, useState } from 'react'
import DescMap from './DescMap'
import { Button, message, Space } from 'antd'

function Index(props, ref) {
  const { contentData = [], title, saveApi, required, canEdit, initEdit = false } = props
  const DescRef = useRef({})
  function getRef(dom, groupName) {
    if (dom) {
      DescRef.current[groupName] = dom
    }
  }

  const [editable, setEditable] = useState(initEdit)
  const getData = async () => {
    const allForm = Object.values(DescRef.current).filter(Boolean)
    const res = await Promise.all(allForm.map(async (form) => await form?.validateFields()))
    let map = res.reduce((acc, value) => ({ ...acc, ...value }))

    const data = contentData
      .reduce((acc, value) => [...acc, ...value.contentList], [])
      .map((v) => ({ ...v, content: map[v.templateId] }))
    return data
  }
  const saveData = async () => {
    setIsLoading(true)
    try {
      const data = await getData()
      await saveApi?.(data)
      setEditable(false)
    } catch (err) {
      // message.info('请填写完整数据')
    } finally {
      setIsLoading(false)
    }
  }
  useImperativeHandle(ref, () => ({
    saveData,
    getData,
  }))
  const [isLoading, setIsLoading] = useState(false)
  return (
    <div>
      <div className={styles.js}>
        <div className={styles.title}>{title}</div>
        <Space key="edit">
          {canEdit && editable && (
            <>
              <Button key="cancel" onClick={() => setEditable(false)}>
                取消
              </Button>
              <Button type="primary" key="save" onClick={() => saveData()} loading={isLoading}>
                保存
              </Button>
            </>
          )}
          {canEdit && !editable && (
            <Button type="primary" key="edit" onClick={() => setEditable(true)} disabled={!canEdit}>
              编辑
            </Button>
          )}
        </Space>
      </div>
      {contentData.map(({ groupName, contentList }) => {
        return (
          <DescMap
            key={groupName}
            contentList={contentList}
            groupName={groupName}
            editable={!editable}
            required={required}
            ref={(dom) => getRef(dom, groupName)}
          />
        )
      })}
    </div>
  )
}

export default observer(forwardRef(Index))
