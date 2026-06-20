import Modeler from 'bpmn-js/lib/Modeler'; // Import Modeler instead of Viewer
import { useEffect, useRef, useState } from 'react';
import { message, Spin } from 'antd';
import Api from '@/api/process/bpmnFlowChartApi';
import styles from './index.less';
import IconFont from '@/components/Icon';

const urls = {
  xmlGetUrl: '/flow/process/getProcessBpmnXml',
  highLightGetUrl: '/flow/process/getProcessPictureData',
};

const BpmnFlowChart = ({
  processInstanceId,
  height,
  ApiUrls = urls,
  childrenCallBack
}) => {
  const [xmlData, setXmlData] = useState();
  const [highLightData, setHighLightData] = useState();
  const [loading, setLoading] = useState(false);
  const [bpmnModeler, setBpmnModeler] = useState(null);

  const bpmnRef = useRef();

  const getData = async () => {
    setLoading(true);
    try {
      const params = { processInstanceId };
      const res = await Promise.all([
        Api.getXml(ApiUrls.xmlGetUrl, params),
        Api.getHighLight(ApiUrls.highLightGetUrl, params),
      ]);
      if (res) {
        setLoading(false);
        setXmlData(res[0]);
        setHighLightData(res[1]);
      }
    } catch (e) {
      setLoading(false);
    }
  };

  const setNodeColor = (ids, newBpmn, colorClass, type = 'add',) => {
    const elementRegistry = newBpmn.get('elementRegistry');
    ids.forEach((item) => {
      if (elementRegistry._elements[item]) {
        const element = elementRegistry._elements[item].gfx;
        if (type === 'remove') {
          element.classList.remove(colorClass);
        } else {
          element.classList.add(colorClass);
        }
      }
    });
  };

  const createDiagram = () => {
    bpmnModeler && bpmnModeler.destroy();

    const newBpmn = new Modeler({
      container: bpmnRef.current,
      height,
      additionalModules: [
        {
          paletteProvider: ["value", ''], //禁用/清空左侧工具栏
          labelEditingProvider: ["value", ''], //禁用节点编辑
          contextPadProvider: ["value", ''], //禁用图形菜单
          bendpoints: ["value", {}], //禁用连线拖动
          zoomScroll: ["value", ''], //禁用滚动
          moveCanvas: ['value', ''], //禁用拖动整个流程图
          move: ['value', ''] //禁用单个图形拖动
        }
      ]
    });
    const canvas = newBpmn.get('canvas')

    newBpmn.importXML(xmlData, (err) => {
      if (err) {
        message.error(err);
      } else {
        canvas.zoom('fit-viewport', 'auto')
        if (highLightData) {
          // console.log('highLightData: ', highLightData);
          // const successIds = highLightData.highLine.concat(highLightData.highPoint);
          const processingIds = highLightData.waitingToDo;
          // const returnIds = highLightData.backNodeList;
          // setNodeColor(successIds, newBpmn, 'nodeSuccess');
          setNodeColor(processingIds, newBpmn, 'nodeProcessing');
          // setNodeColor(returnIds, newBpmn, 'nodeReturn');
        }
      }
    });
    newBpmn.on('selection.changed', (event) => {
      // console.log('event2: ', event);
      if (event.newSelection.length) {
        const elementRegistry = newBpmn.get('elementRegistry');
        const selectedIds = [event.newSelection[0].id];
        let userTaskIds = [];
        // 遍历所有元素，找到 userTask
        elementRegistry.forEach((element) => {
          if (element.type === 'bpmn:UserTask') {
            userTaskIds.push(element.id);
          }
        });
        setNodeColor(selectedIds, newBpmn, 'nodeChoose');
        const otherIds = userTaskIds.filter((item) => item !== selectedIds[0]);
        setNodeColor(otherIds, newBpmn, 'nodeChoose', 'remove');
        childrenCallBack && childrenCallBack(selectedIds);
      } else if (event.oldSelection.length) {
        const selectedIds = [event.oldSelection[0].id];
        setNodeColor(selectedIds, newBpmn, 'nodeChoose', 'remove');
        childrenCallBack && childrenCallBack(selectedIds);
      }
    })
    setBpmnModeler(newBpmn);
  };
  useEffect(() => {
    getData();
  }, []);

  useEffect(() => {
    try {
      if (xmlData) {
        createDiagram();
      }
    } catch (e) {
    }
  }, [highLightData]);

  // const processList = [
  //   {
  //     title: '未到达节点',
  //     color: '#8A8B8E',
  //   },
  //   {
  //     title: '已通过节点',
  //     color: '#00A870',
  //   },
  //   {
  //     title: '待审核节点',
  //     color: '#2558E6',
  //   },
  //   {
  //     title: '未通过节点',
  //     color: 'red',
  //   },
  // ];

  return (
    <Spin spinning={loading} tip="加载中...">
      <div className={styles.wrap}>
        {/* <div className={styles.legendWrap}>
          {processList.map((item) => {
            return (
              <div key={item.title} className={styles.legendItem}>
                <div className={styles.legend} style={{ border: `1px solid ${item.color}` }}></div>
                <div className={styles.title}>{item.title}</div>
              </div>
            );
          })}
        </div> */}
        <div
          className={styles.reset}
          onClick={() => {
            bpmnModeler.get('canvas').zoom('fit-viewport', 'auto');
          }}
        >
          <IconFont type="icon-icon_withdraw" />
          复位
        </div>
        <div id="canvas" ref={bpmnRef} style={{ height }} className={styles.container} />
      </div>
    </Spin>
  );
};

export default BpmnFlowChart;
