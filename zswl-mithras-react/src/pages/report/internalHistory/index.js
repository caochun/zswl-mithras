import { BiView } from '@/components'
import { observer, setSessionStorage, getSessionStorage } from '@zswl/admin'
import { Row, Select, message } from 'antd'
import { useEffect, useMemo, useState, useRef } from 'react'
import Api from '@/api/report/reportManage'
import localApi from './api'
import { Button, Page } from '@zswl/components'
import _ from 'lodash'

const REPORT_LOCAL_KEY = 'REPORT_LOCAL_KEY'

function InternalManage({ props }) {
  const elementRef = useRef(null)
  const [options, setOptions] = useState([])
  const [value, setValue] = useState('')
  const [showRefreshBtn, setShowRefreshBtn] = useState(false)

  const lastValue = getSessionStorage(REPORT_LOCAL_KEY)

  const pageStore = Page.useStore(
    {
      request: async () => {
        const res = await Api.getReportList()
        setOptions(res)
        if (res.length > 0) {
          setValue(lastValue ?? res[0].value)
        }
        return { list: res }
      },
    },
    []
  )

  const currentLabel = useMemo(() => {
    const currentItem = options.find((item) => item.value === value)
    return currentItem?.label || ''
  }, [value, options])

  const getRefreshBtnStatus = async (reportName) => {
    const res = await localApi.showRefreshBtn({ reportName })
    setShowRefreshBtn(res)
  }

  useEffect(() => {
    if (currentLabel) {
      getRefreshBtnStatus(currentLabel)
    }
  }, [currentLabel])

  const selectChange = (val) => {
    setValue(val)
    setSessionStorage(REPORT_LOCAL_KEY, val)
  }

  const refresh = async () => {
    if (currentLabel) {
      await localApi.getRefresh({
        reportName: currentLabel,
      })
      message.success('更新成功')
      setTimeout(() => {
        const frameWindow = document.querySelector('#reportIframe')
        frameWindow.src = value
      }, 3000)
    } else {
      message.info('请选择管报')
    }
  }

  return (
    <Page store={pageStore} style={{ height: '100%' }}>
      <div style={{ background: '#fff', margin: '0 12px' }}>
        <Row>
          <div style={{ padding: '0 12px' }}>
            <label>管报选择:</label>
            <Select
              options={options}
              value={value}
              onChange={selectChange}
              style={{ width: 200, margin: 12 }}
            />
            {showRefreshBtn && (
              <Button type="primary" onClick={refresh}>
                刷新
              </Button>
            )}
          </div>
        </Row>
      </div>
      <div ref={elementRef}></div>
      <BiView
        url={value}
        id="reportIframe"
        style={{ border: 0, width: '100%', height: `calc(100% - ${100}px)` }}
      />
    </Page>
  )
}

export default observer(InternalManage)
