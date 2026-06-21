import { history, observer } from '@zswl/admin'
import { useEffect, useMemo } from 'react'
import Store from './store'
import { App, Page } from '@zswl/components'
import { message } from 'antd'
import Api from '@/api/process/flowModelApi'
import { baseURL } from '@/utils'
function ProcessDesignDetail({ params: { id } }) {
  const store = useMemo(() => new Store(), [])
  useEffect(() => {
    return () => {
      App.resetStore(store)
    }
  }, [])
  useEffect(() => {
    init()
  }, [])
  useEffect(() => {
    save()
    return () => {
      // window.removeEventListener('message')
    }
  }, [])
  const init = () => {
    const myFrame = document.getElementById('myFrame')

    myFrame.onload = async () => {
      // 必须等iframe 加载完成才能执行相应的异步操作！！！

      let moduleConfigs = await Api.getModuleConfig()
      let dataXMl = await Api.getDetail({ modelId: id })
      let parentData = {
        type: 'init',
        moduleConfigs,
        xml: dataXMl.bpmnXml,
        baseURL: baseURL(),
      }
      myFrame.contentWindow.postMessage(parentData, '*')
    }
  }
  const save = () => {
    window.onmessage = async (event) => {
      if (event.data && event.data?.xml) {
        const { code, msg, data } = await Api.save({ bpmnXml: event.data?.xml })
        if (code === 200) {
          message.success(`模型${data}保存成功！,即将跳转到对应模型`)
          setTimeout(() => {
            history.replace(`/process/design/detail/${data}`)
          }, 1000)
        } else {
          msg && message.info(msg)
        }
      }
    }
  }
  return (
    <>
      <Page store={store}>
        <div>
          <iframe
            src={`/public/approvalFlow/index.html?time=${new Date().getTime()}`}
            // src={'/public/approvalFlow/index.html'}
            id="myFrame"
            // scrolling="no"
            //frameBorder="0"
            style={{ width: '100%', height: '725px' }}
          />
        </div>
      </Page>
    </>
  )
}

export default observer(ProcessDesignDetail)
