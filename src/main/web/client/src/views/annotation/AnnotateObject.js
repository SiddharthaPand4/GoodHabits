import React, {Component} from "react";
import Annotation from "react-image-annotation/lib/components/Annotation";
import Card from "antd/es/card";
import {Button, Col, Empty, message, Row} from "antd";
import {Link} from "react-router-dom";
import {ArrowLeftOutlined, ArrowRightOutlined, DeleteOutlined} from '@ant-design/icons';
import DatasetService from "../../services/DatasetService";
import Select from "antd/es/select";
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
export default class AnnotateObject extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isAnnotated: false,
            annotations: [],
            annotation: {},
            currentImageFileName: "",
            currentImageDir: "",
            currentImageLabel: "",
            currentImageId: 0,
            currentImageCount: 0,
            totalImages: 0,
            datasetId: 0,
            labels: [],
            count: 0,
            activeAnnotations: [],
            project: {}
        }
        this.saveImageAnnotation = this.saveImageAnnotation.bind(this);
        this.parseReceivedData = this.parseReceivedData.bind(this);
        this.loadImage = this.loadImage.bind(this);
        this.loadCount = this.loadCount.bind(this);
        this.clearClickHandler = this.clearClickHandler.bind(this);
        this.leftClickHandler = this.leftClickHandler.bind(this);
        this.rightClickHandler = this.rightClickHandler.bind(this);
        this.handleKeyPress = this.handleKeyPress.bind(this);
        this.renderEditor = this.renderEditor.bind(this);
        this.onChange = this.onChange.bind(this);
        this.labelChangeHandler = this.labelChangeHandler.bind(this);
        this.getLabels = this.getLabels.bind(this);
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

    onChange = (annotation) => {
        this.setState({annotation})
    }

    onMouseOver = (id) => e => {
        this.setState({
            activeAnnotations: [
                ...this.state.activeAnnotations,
                id
            ]
        })
    }

    onMouseOut = (id) => e => {
        const index = this.state.activeAnnotations.indexOf(id)

        this.setState({
            activeAnnotations: [
                ...this.state.activeAnnotations.slice(0, index),
                ...this.state.activeAnnotations.slice(index + 1)
            ]
        })
    }

    activeAnnotationComparator = (a, b) => {
        return a.data.id === b
    }
    onSubmit = (annotation) => {
        const {geometry, data} = annotation

        let count = this.state.count;
        if (data) {
            this.setState({
                annotation: {},
                annotations: this.state.annotations.concat({
                    geometry,
                    data: {
                        ...data,
                        id: count
                    }
                }),
                isAnnotated: true,
                count: count + 1
            })
        }

    }

    parseReceivedData() {
        let {project, datasetId} = this.state;
        /*if (this.props.location.project) {
            Object.assign(project, this.props.location.project);
        }
        Object.assign(project, JSON.parse(localStorage.getItem('project')));
        if (project.labels != null)
            receivedData.labels = project.labels;
        if (project.datasetId != null)
            receivedData.datasetId = project.datasetId;*/
        if (this.props.location.search) {
            let searchParams = queryString.parse(this.props.location.search);
            datasetId = searchParams.datasetId;
            project.projectId = searchParams.projectId;
            project.projectName = searchParams.projectName;
            this.setState({project, datasetId}, () => this.getLabels(datasetId));
        }

    }

    getLabels(datasetId) {
        DatasetService.getLabels(datasetId).then(response => {
            console.log(response.data);
            let options = [];

            options = response.data;

            this.loadCount(datasetId);
            this.loadImage("next", datasetId, 0);
            console.log(options);
            this.setState({
                labels: options
            })
        }).catch(error => {
            let msg = "Something went wrong!";
            if (error && error.response && error.response.data && error.response.data.message) {
                msg = error.response.data.message;
                message.warn(msg);
            }
        })
    }

    loadCount(datasetId) {
        DatasetService.loadTotalImagesCount(datasetId)
            .then(response => {
                this.setState({
                    totalImages: response.data
                })
            })
            .catch(err => {
                console.log(err)
            })
    }

    loadImage(direction, datasetId, offset) {
        let delta;   // for increment or decrement of current image count
        if (direction == "prev") {
            delta = -1;
            offset -= 2;
        } else {
            delta = 1;
        }
        DatasetService.loadClassificationImage(datasetId, offset)
            .then(response => {
                console.log(response)
                let image = response.data;
                let annotationList = image.annotations;
                let annotations = [];
                const type = "RECTANGLE";
                for (let i = 0; i < annotationList.length; i++) {
                    let annotation = {};
                    let data = {};
                    let geometry = {};
                    let label = annotationList[i].label;
                    let id = i;
                    let x = annotationList[i].x;
                    let y = annotationList[i].y;
                    let h = annotationList[i].h;
                    let w = annotationList[i].w;
                    geometry.type = type;
                    geometry.x = x;
                    geometry.y = y;
                    geometry.width = w;
                    geometry.height = h;
                    data.id = id;
                    data.text = label;
                    annotation.geometry = geometry;
                    annotation.data = data;
                    annotations.push(annotation);
                }
                this.setState({
                    isAnnotated: false,
                    annotations: annotations,
                    currentImageFileName: response.data.fileName,
                    currentImageDir: response.data.dir,
                    currentImageLabel: response.data.label,
                    currentImageId: response.data.id,
                    currentImageCount: this.state.currentImageCount + delta
                })
            })
            .catch((err) => {
                console.log(err)
            })
    }

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

    saveImageAnnotation(finish) {
        let currentImageId = this.state.currentImageId;
        let annotations = this.state.annotations;
        let annotationList = [];
        for (let i = 0; i < annotations.length; i++) {
            let annotation = {};
            let label = annotations[i].data.text;
            let x = annotations[i].geometry.x;
            let y = annotations[i].geometry.y;
            let height = annotations[i].geometry.height;
            let width = annotations[i].geometry.width;
            annotation.label = label;
            annotation.x = x;
            annotation.y = y;
            annotation.h = height;
            annotation.w = width;
            annotationList.push(annotation);
        }
        DatasetService.saveCurrentImageAnnotation(currentImageId, annotationList)
            .then(res => {
                if (finish) {
                    let {project} = this.state;
                    this.props.history.push('/project-models?' + queryString.stringify({
                        projectId: project.projectId,
                        projectName: project.projectName
                    }))
                } else {
                    console.log(res.data)
                }
            })
            .catch(err => {
                console.log(err)
            })
    }

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
                    left: `${geometry.x}%`,
                    top: `${geometry.y + geometry.height}%`,
                }}
            >
                <Select defaultValue="Select" style={{width: 120}} onChange={value => props.onChange({
                    ...props.annotation,
                    data: {
                        ...props.annotation.data,
                        text: value
                    }
                })}>
                    {

                        this.state.labels.map(label =>
                            <Option value={label}>
                                {label}
                            </Option>
                        )
                    }
                </Select>
                <button onClick={props.onSubmit}>Ok</button>

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
                    left: `${geometry.x}%`,
                    top: `${geometry.y + geometry.height}%`
                }}

            >
                {"Id :" + " " + annotation.data.id}
                <br/>{annotation.data.text
            }
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
                isAnnotated: true,
                count: this.state.count - 1
            };
        });
    }

    render() {
        // let curr = this.state.currentImageId;
        let imageFileName = this.state.currentImageFileName;
        let imageDir = this.state.currentImageDir;
        let currentCount = this.state.currentImageCount;
        let {project} = this.state;
        console.log(this.state.labels);
        return (
            // tabIndex="0" onKeyPress={this.handleKeyPress}
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
                                        src={"/open/classify1?fileName=" + imageFileName + "&dir=" + imageDir}
                                        //src={"/images/mona-lisa.jpg"}
                                        alt='Two pebbles anthropomorphized holding hands'
                                        annotations={this.state.annotations}
                                        type={this.state.type}
                                        value={this.state.annotation}
                                        onChange={this.onChange}
                                        onSubmit={this.onSubmit}
                                        renderEditor={this.renderEditor}
                                        renderContent={this.renderContent}
                                        renderHighlight={this.renderHighlight}
                                        activeAnnotationComparator={this.activeAnnotationComparator}
                                        activeAnnotations={this.state.activeAnnotations}
                                        //onUpdate={this.onUpdate}
                                    />
                                    <br/><br/>
                                    <div style={{textAlign: 'center'}}>
                                        <Button style={{float: "left", width: 150}} className={'button'}
                                                onClick={this.leftClickHandler}><ArrowLeftOutlined/>Previous</Button>
                                        <b>{currentCount}</b>
                                        <Button style={{float: "right", width: 150}} className={'button'}
                                                onClick={this.rightClickHandler}>Next<ArrowRightOutlined/></Button>
                                    </div>
                                </Card>
                            </div>
                            <br/>
                        </Col>
                        <Col xl={{span: 1}} lg={{span: 1}} md={{span: 1}} sm={{span: 1}} xs={{span: 1}}>
                        </Col>
                        <Col xl={{span: 6}} lg={{span: 6}} md={{span: 6}} sm={{span: 6}} xs={{span: 6}}>


                            <Card type={'inner'}
                                  style={{
                                      minHeight: "400px",
                                      border: "4px solid #BEBEBE",
                                      margin: '20px 20px 20px 20px'
                                  }}
                                  align="center" title={"Labels"}>
                                <div className={"annotation-scroller"}>
                                    {this.state.annotations.map((annotations, i) =>
                                        <li key={i} style={{marginBottom: '5px'}}>
                                            <label style={{marginRight: '5px'}}>
                                                {"Id: " + i}</label>
                                            <select data-index={i}
                                                    defaultValue={annotations.data.text}
                                                    onChange={this.labelChangeHandler}
                                                    onMouseOver={this.onMouseOver(annotations.data.id)}
                                                    onMouseOut={this.onMouseOut(annotations.data.id)}
                                                    key={annotations.data.id}
                                                    style={{width: "130px"}}>

                                                {annotations.data.text}

                                                {this.state.labels.map(label =>
                                                    <option value={label}> {label} </option>
                                                )}
                                            </select>

                                            &nbsp;<Button size="small" className={'button'} data-index={i}
                                                          onClick={this.deleteHandler}><DeleteOutlined/>
                                        </Button>
                                            <br/>
                                        </li>
                                    )}
                                    {(() => {
                                        if (this.state.annotations.length == 0) {
                                            return <Empty description={true}/>;
                                        }
                                    })()}
                                </div>
                                <br/>
                                <div>
                                    <div style={{textAlign: 'center'}}>
                                        <Button block className={'button'}
                                                onClick={this.clearClickHandler}>Clear
                                            All</Button>
                                    </div>
                                    <br/>
                                    <div style={{textAlign: 'center'}}>
                                        <Link to={{
                                            pathname: "/select/model",
                                            project: project
                                        }}>

                                        </Link>
                                    </div>
                                </div>

                            </Card>
                            <br/><br/>
                            <div><Button block className={'button'}
                                         onClick={this.finishHandler}
                            >Finish
                            </Button></div>
                        </Col>
                    </Row>
                </Card>
            </div>
        )
    }
}