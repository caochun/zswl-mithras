import BpmnViewer from 'bpmn-js/lib/NavigatedViewer'
import { useEffect, useRef, useState } from 'react'
import { message, Spin } from 'antd'
import Api from '@/api/process/bpmnFlowChartApi'
// import { SVG } from '@svgdotjs/svg.js'
import styles from './index.less'
import IconFont from '@/components/Icon'
const urls = {
  xmlGetUrl: '/flow/process/getProcessBpmnXml', // bpmn xml获取地址
  highLightGetUrl: '/flow/process/getProcessPictureData', // 高亮的节点线获取地址
}
const BpmnFlowChart = ({
  processInstanceId, // 流程Id
  height, // 流程图高度
  ApiUrls = urls,
}) => {
  const [xmlData, setXmlData] = useState()
  const [highLightData, setHighLightData] = useState()
  const [loading, setLoading] = useState(false)
  const [bpmnModule, setBpmnModule] = useState(null)
  const bpmnRef = useRef()
  const getData = async () => {
    setLoading(true)
    try {
      const params = { processInstanceId }
      const res = await Promise.all([
        await Api.getXml(ApiUrls.xmlGetUrl, params),
        await Api.getHighLight(ApiUrls.highLightGetUrl, params),
      ])
      if (res) {
        setLoading(false)
        setXmlData(res[0])
        setHighLightData(res[1])
      }
    } catch (e) {
      setLoading(false)
    }
  }
  // 设置节点颜色
  const setNodeColor = (ids, newBpmn, colorClass) => {
    const elementRegistry = newBpmn.get('elementRegistry')

    ids.forEach((item) => {
      if (elementRegistry._elements[item]) {
        const element = elementRegistry._elements[item].gfx
        element.classList.add(colorClass)
        // console.log(elementRegistry, element)
      }
    })
  }

  const createDiagram = () => {
    bpmnModule && bpmnModule.destroy && bpmnModule.destroy()
    const newBpmn = new BpmnViewer({
      container: bpmnRef.current,
      height,
    })

    const canvas = newBpmn.get('canvas')

    newBpmn.importXML(xmlData, (err) => {
      //console.log(err, xmlData, JSON.stringify(err), 88899898)
      if (err) {
        //console.log(err)
        // message.error(err)
        //return Promise.reject(new Error(err))
      } else {
        canvas.zoom('fit-viewport', 'auto')
        if (highLightData) {
          const successIds = highLightData.highLine.concat(highLightData.highPoint)
          const processingIds = highLightData.waitingToDo
          const returnIds = highLightData.backNodeList
          //   addCustomDefs(
          //     newBpmn.get('elementRegistry')._elements.ProjEstablishModifyFlow.secondaryGfx
          //   )
          setNodeColor(successIds, newBpmn, 'nodeSuccess')
          setNodeColor(processingIds, newBpmn, 'nodeProcessing')
          setNodeColor(returnIds, newBpmn, 'nodeReturn')
        }
      }
    })
    setBpmnModule(newBpmn)
  }

  useEffect(() => {
    getData()
  }, [])

  useEffect(() => {
    try {
      if (xmlData) {
        createDiagram()
      }
    } catch (e) {
    }
  }, [xmlData, highLightData])
  const processList = [
    { title: '未到达节点', color: '#8A8B8E' },
    { title: '已通过节点', color: '#00A870' },
    { title: '待审核节点', color: '#2558E6' },
    { title: '未通过节点', color: 'red' },
  ]
  return (
    <Spin spinning={loading} tip="加载中...">
      <div className={styles.wrap}>
        <div className={styles.legendWrap}>
          {processList.map((item) => {
            return (
              <div key={item.title} className={styles.legendItem}>
                <div className={styles.legend} style={{ border: `2px solid ${item.color}` }}></div>
                <div className={styles.title}>{item.title}</div>
              </div>
            )
          })}
        </div>
        <div
          className={styles.reset}
          onClick={() => {
            bpmnModule.get('canvas').zoom('fit-viewport', 'auto')
          }}
        >
          <IconFont type="icon-icon_withdraw" />
          复位
        </div>
        <div id="canvas" ref={bpmnRef} style={{ height }} className={styles.container} />
      </div>
    </Spin>
  )
}
export default BpmnFlowChart
