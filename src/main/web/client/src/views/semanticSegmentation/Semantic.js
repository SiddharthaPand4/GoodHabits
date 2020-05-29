import React, {Component} from 'react'
//import Annotation from '../../../../../src'
import Annotation from './Annotation'
//import PolygonSelector from '../../../../../src/hocs/PolygonSelector'
import PolygonSelector from './PolygonSelector'
import {Button, Card, Col, Empty, message, Row} from "antd";
import Select from "antd/es/select";

import {Link} from "react-router-dom";
import {getHorizontallyCentralPoint, getVerticallyLowestPoint} from "./pointsUtils";
import queryString from "query-string";

const Box = ({children, geometry, style}) => (
    <div
        style={{
            ...style,
            position: 'absolute',
            left: `${geometry.x}%`,
            top: `${geometry.y}%`,
            height: `${geometry.height}%`,
            width: `${geometry.width}%`,
        }}
    >
        {children}
    </div>
)
const {Option} = Select;

export default class Semantic extends Component {
    constructor(props) {
        super(props);

        this.state = {
            annotations: [],
            annotation: {},
            type: PolygonSelector.TYPE,

            isAnnotated: false,
            currentImageFileName: "",
            currentImageDir: "",
            currentImageLabel: "",
            currentImageId: 0,
            currentImageCount: 0,
            totalImages: 0,
            datasetId: 0,
            labels: [],
            activeAnnotations: [],
            project: {}
        }
        // this.saveImageAnnotation = this.saveImageAnnotation.bind(this);
        this.parseReceivedData = this.parseReceivedData.bind(this);
        //  this.loadImage = this.loadImage.bind(this);
        //  this.loadCount = this.loadCount.bind(this);
        this.clearClickHandler = this.clearClickHandler.bind(this);
        this.leftClickHandler = this.leftClickHandler.bind(this);
        this.rightClickHandler = this.rightClickHandler.bind(this);
        this.handleKeyPress = this.handleKeyPress.bind(this);
        this.renderEditor = this.renderEditor.bind(this);
        this.onChange = this.onChange.bind(this);
        this.onMouseOverHandler=this.onMouseOverHandler.bind(this);
        this.onMouseOutHandler=this.onMouseOutHandler.bind(this);
        this.labelChangeHandler = this.labelChangeHandler.bind(this);
        // this.getLabels=this.getLabels.bind(this);

    }

    labelChangeHandler(event) {
        let index = event.target.dataset.index;
        let value = event.target.value;
        let annotations = this.state.annotations;
        annotations[index].data.text = value;
        this.setState({
            annotations: annotations,
            isAnnotated: true
        })
    }

    componentDidMount() {
        this.parseReceivedData();
    }

    clearClickHandler() {
        this.setState({
            annotations: [],
            isAnnotated: true
        })
    }

    onMouseOverHandler= (id) => e => {
        this.setState({
            activeAnnotations: [
                ...this.state.activeAnnotations,
                id
            ]
        })
    }

    onMouseOutHandler= (id) => e => {
        const index = this.state.activeAnnotations.indexOf(id)

        this.setState({
            activeAnnotations: [
                ...this.state.activeAnnotations.slice(0, index),
                ...this.state.activeAnnotations.slice(index + 1)
            ]
        })
    }



    activeAnnotationComparator = (a, b) => {
        return a.data.id == b
    }


    onChange = (annotation) => {
        this.setState({annotation})
    }

    onSubmit = (annotation) => {
        const {geometry, data} = annotation


        this.setState({
            annotation: {},
            annotations: this.state.annotations.concat({
                geometry,
                data: {
                    ...data,
                    id: this.state.annotations.length
                }
            }),
            isAnnotated: true
        })


    }


    parseReceivedData() {
        let receivedData = {labels: []};
        let {project,datasetId} = this.state;
        if (this.props.location.search) {
            let searchParams = queryString.parse(this.props.location.search);
            datasetId = searchParams.datasetId;
            project.projectId=searchParams.projectId;
            project.projectName=searchParams.projectName;
            this.setState({project,datasetId}, () => this.getLabels(datasetId));
        }
    }

    // getLabels(datasetId) {
    //     DatasetService.getLabels(datasetId).then(response => {
    //         console.log(response.data);
    //         let options = [];
//
    //         options=response.data;
//
    //         this.loadCount(datasetId);
    //         this.loadImage("next", datasetId, 0);
//
    //         this.setState({
    //             labels: options,
    //         })
    //     }).catch(error => {
    //         let msg = "Something went wrong!";
    //         if (error && error.response && error.response.data && error.response.data.message) {
    //             msg = error.response.data.message;
    //             message.warn(msg);
    //         }
    //     })
    // }

    //  loadCount(datasetId) {
    //      DatasetService.loadTotalImagesCount(datasetId)
    //          .then(response => {
    //              this.setState({
    //                  totalImages: response.data
    //              })
    //          })
    //          .catch(error => {
    //              let msg = "Something went wrong!";
    //              if (error && error.response && error.response.data && error.response.data.message) {
    //                  msg = error.response.data.message;
    //                  message.warn(msg);
    //              }
    //          })
    //  }

    // loadImage(direction, datasetId, offset) {
    //     let delta;   // for increment or decrement of current image count
    //     if (direction == "prev") {
    //         delta = -1;
    //         offset -= 2;
    //     } else {
    //         delta = 1;
    //     }
    //     DatasetService.loadClassificationImage(datasetId, offset)
    //         .then(response => {
    //             console.log(response)
    //             let image = response.data;
    //             let annotationList = image.annotations;
    //             let annotations = [];
    //             const type = "POLYGON";
    //             for (let i = 0; i < annotationList.length; i++) {
    //                 let annotation = {};
    //                 let data = {};
    //                 let geometry = {};
    //                 let label = annotationList[i].label;
    //                 let id = i;
    //                 let points = annotationList[i].coordinates;
    //                 geometry.type = type;
    //                 geometry.points = points;
    //                 data.id = id;
    //                 data.text = label;
    //                 annotation.geometry = geometry;
    //                 annotation.data = data;
    //                 annotations.push(annotation);
    //             }
    //             this.setState({
    //                 isAnnotated: false,
    //                 annotations: annotations,
    //                 currentImageFileName: response.data.fileName,
    //                 currentImageDir: response.data.dir,
    //                 currentImageLabel: response.data.label,
    //                 currentImageId: response.data.id,
    //                 currentImageCount: this.state.currentImageCount + delta,
//
    //             })
    //         })
    //         .catch((err) => {
    //             console.log(err)
    //         })
    // }

    leftClickHandler() {
        if (this.state.currentImageCount > 1) {
            if (this.state.isAnnotated) {
                this.saveImageAnnotation(false);
            }
            this.loadImage("prev", this.state.datasetId, this.state.currentImageCount);
        }
    }

    rightClickHandler() {
        let total = this.state.currentImageCount;
        if (total < this.state.totalImages) {
            if (this.state.isAnnotated) {
                this.saveImageAnnotation(false);
            }
            this.loadImage("next", this.state.datasetId, this.state.currentImageCount);
        }
    }

    finishHandler = () => {
        let {project} = this.state;
        if (this.state.isAnnotated) {
            this.saveImageAnnotation(true);
        } else {
            this.props.history.push('/project-models?' + queryString.stringify({
                projectId: project.projectId,
                projectName: project.projectName
            }))
        }

    }

    //  saveImageAnnotation(finish) {
    //      let currentImageId = this.state.currentImageId;
    //      let annotations = this.state.annotations;
    //      let annotationList = [];
    //      for (let i = 0; i < annotations.length; i++) {
    //          let annotation = {};
    //          let label = annotations[i].data.text;
    //          annotation.label = label;
    //          let coordinates = [];
    //          annotations[i].geometry.points.map(point => {
    //              coordinates.push(point);
    //          })
    //          annotation.coordinates = coordinates;
    //          annotation.type = "POLYGON";
    //          annotationList.push(annotation);
    //      }
    //      DatasetService.saveCurrentImageAnnotation(currentImageId, annotationList)
    //          .then(res => {
    //              if (finish) {
    //                  let {project} = this.state;
    //                  this.props.history.push('/project-models?' + queryString.stringify({
    //                      projectId: project.projectId,
    //                      projectName: project.projectName
    //                  }))
    //              } else {
    //                  console.log(res.data)
    //              }
    //          })
//
    //          .catch(err => {
    //              console.log(err)
    //          })
    //  }

    handleKeyPress(event) {
        let key = event.key;
        if (key === "c") {
            this.clearClickHandler();
        } else if (key === "v") {
            this.rightClickHandler();
        } else if (key === "x") {
            this.leftClickHandler();
        } else {
            console.log(key);
        }
    }

    renderEditor(props) {
        const {geometry, data} = props.annotation
        if (!geometry) return null
        return (
            <div

                style={{
                    background: 'white',
                    borderRadius: 3,
                    position: 'absolute',
                    left: `${getHorizontallyCentralPoint(geometry.points)}%`,
                    top: `${(getVerticallyLowestPoint(geometry.points)+3)}%`,
                }}
            >

                {(geometry.points.length >= 2) && <button onClick={props.onSubmit}>Ok</button>}

            </div>
        )
    }

    renderContent({annotation}) {
        const {geometry} = annotation
        return (
            <div
                key={annotation.data.id}
                style={{
                    background: 'black',
                    color: 'white',
                    padding: 10,
                    position: 'absolute',
                    fontSize: 12,
                    left: `${getHorizontallyCentralPoint(geometry.points)}%`,
                    top: `${(getVerticallyLowestPoint(geometry.points)+3)}%`,
                    //top: `${(getVerticallyLowestPoint(geometry.points) + 10)}%`
                }}

            >
                {annotation.data.id}


            </div>

        )
    }

    renderHighlight({annotation, active}) {
        const {geometry} = annotation
        if (!geometry) return null

        return (
            <Box
                key={annotation.data.id}
                geometry={geometry}
                style={{
                    border: 'solid 2px black',
                    boxShadow: active
                        && '0 0 40px 40px rgba(255, 255, 255, 0.3) inset'
                }}
            >

            </Box>

        )
    }

    onSelect = (annotation) => {
        const {annotations} = this.state;
        let oldIndex = annotations.findIndex((a) => {
            return a.data.id === annotation.data.id;
        });
        if (oldIndex > -1) {
            annotations[oldIndex] = {
                geometry: {...annotation.geometry},
                data: {
                    ...annotation.data,
                    id: annotation.data.id
                }
            };
        }
        this.setState({annotation: {}, annotations: annotations});
    }


    deleteHandler = (event) => {
        const index = event.target.dataset.index;
        this.setState(state => {
            let tasks = [...state.annotations]
            tasks.splice(index, 1);
            tasks.map((annotation, i) => {
                annotation.data.id = i;
            })
            return {
                annotations: tasks,
                isAnnotated: true
            };
        });
    }


    render() {
        // let currentImageId = this.state.currentImageId;
        let imageFileName = this.state.currentImageFileName;
        let imageDir = this.state.currentImageDir;
        let currentCount = this.state.currentImageCount;
        let {project} = this.state;
        console.log(this.state.labels);
        return (


            <div tabIndex="0" onKeyPress={this.handleKeyPress} style={{backgroundColor: 'white'}}>

                    <Card>
                        <Row>
                            <Col xl={{span: 16}} lg={{span: 16}} md={{span: 16}} sm={{span: 16}} xs={{span: 16}}>
                                <div tabIndex="0" align="center">
                                    <Card style={{backgroundColor: "#cee9ea"}}>

                                        <Annotation
                                            style={{

                                                maxHeight: "380px",
                                                minHeight: "380px"
                                            }}
                                            objectFit="contain"
                                            //src={"/open/classify1?fileName=" + imageFileName + "&dir=" + imageDir}
                                            src={"/pgs/p-001.png"}
                                            alt='Two pebbles anthropomorphized holding hands'

                                            renderEditor={this.renderEditor}

                                            activeAnnotationComparator={this.activeAnnotationComparator}
                                            activeAnnotations={this.state.activeAnnotations}
                                            annotations={this.state.annotations}
                                            renderContent={this.renderContent}
                                            //renderHighlight={this.renderHighlight}
                                            type={this.state.type}
                                            value={this.state.annotation}
                                            onChange={this.onChange}
                                            onSubmit={this.onSubmit}
                                        />
                                        <br/><br/>

                                    </Card>
                                </div>
                                <br/>
                            </Col>

                        </Row></Card>
            </div>
        )
    }
}
