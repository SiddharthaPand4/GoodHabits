import React, {Component} from "react"
import {Button, Card, Col, Empty, Icon, Input, message, Row, Spin, Table,} from "antd";
import AnprService from "../../services/AnprService";
import IncidentTimeline from "./IncidentTimeline";

const {Column} = Table;
const {Search} = Input;

export default class MasterDataView extends Component {
    constructor(props) {
        super(props);
        this.state = {
            archiveEventsLoading: "",
            activeTab: "All-Incidents",
            visible: false,
            timelineLpr: "",
            filter: {
                lpr: "",
            },
            archiveLpr: {
                anprresponse: {},
                lpr: "",
            },

            allData: {
                loading: false,
                anprresponse: {},
                filter: {
                    pages: 1,
                    pageSizes: 24,
                    lpr: "",
                    incidentType: "",
                }
            },
            pageSizeOptions:[12,24,48,96]
        };

        this.refresh = this.refresh.bind(this);
        this.handleRefresh = this.handleRefresh.bind(this);
        this.onMasterDataPageChange = this.onMasterDataPageChange.bind(this);
        this.onMasterDataPageSizeChange = this.onMasterDataPageSizeChange.bind(this);
        this.handleTabClick = this.handleTabClick.bind(this);
        this.refreshIncidentsNow = this.refreshIncidentsNow.bind(this);
        this.onLprInputChange = this.onLprInputChange.bind(this);
        this.search = this.search.bind(this);
        this.onTabChange = this.onTabChange.bind(this);
        this.toggleIncidentTimelineModal = this.toggleIncidentTimelineModal.bind(this);
        this.archiveLprOnChange = this.archiveLprOnChange.bind(this);
    }

    componentDidMount() {
        this.refresh();
    }

    handleTabClick(tabIndex) {
        this.setState({
            activeTabIndex:
                tabIndex === this.state.activeTabIndex
                    ? this.props.defaultActiveTabIndex
                    : tabIndex
        });
    }

    refresh() {
        this.refreshIncidentsNow();
    }

    showModal = (lpr) => {
        this.setState({
            visible: true,
            timelineLpr: lpr
        });
    }

    toggleIncidentTimelineModal() {
        let visible = this.state.visible;
        this.setState({visible: !visible});
    }

    //cant use refresh to read from state as state may not have been set

    refreshIncidentsNow() {
        let allData = this.state.allData;
        allData.loading = true;
        this.setState({allData: allData});
        AnprService.getIncidentsList(this.state.allData.filter).then(request => {
            allData.loading = false;
            allData.anprresponse = request.data;
            this.setState({allData: allData});
        }).catch(error => {
            allData.loading = false;
            this.setState({hasError: true});
            alert("Something went wrong");
        });
    }

    handleRefresh() {
        this.refresh();
    }

    onLprInputChange(e) {
        let filter = this.state.filter;
        filter.lpr = e.target.value;
        this.setState({filter: filter})
    }

    search(searchText) {
        let {filter, allData} = this.state;
        filter.lpr = searchText;
        allData.filter.lpr = searchText;
        this.setState({filter, allData}, () => {
            this.refresh();
        });
    }

    onMasterDataPageChange(pages, pageSizes) {
        let filter = this.state.allData.filter;
        filter.pages = pages;
        filter.pageSizes = pageSizes;
        this.refreshIncidentsNow(filter);
    }

    onMasterDataPageSizeChange(current, pageSizes) {
        let filter = this.state.allData.filter;
        filter.pageSizes = pageSizes;
        this.refreshIncidentsNow(filter);
    }

    onTabChange(key) {
        this.setState({activeTab: key})
    }

    archiveLprOnChange(lpr) {
        this.setState({archiveEventsLoading: lpr});
        AnprService.archiveAllEvent(lpr).then(request => {
            this.setState({archiveEventsLoading: ""});
            this.refresh(this.state.archiveLpr);
            message.success('Vehicle archived!');
        }).catch(error => {
            this.setState({archiveEventsLoading: ""});
            message.error('Something went wrong!');
        })
    }

    render() {

        return (
            <div>
                <div>

                </div>

                <Card
                    style={{width: '100%'}}
                    title={<Row>
                        <Col xl={{span: 16}} lg={{span: 16}} md={{span: 12}} sm={{span: 12}} xs={{span: 12}}>
                            <h4>Master-Anpr-Data</h4>
                        </Col>
                        <Col xl={{span: 8}} lg={{span: 8}} md={{span: 12}} sm={{span: 12}} xs={{span: 12}}>

                            <Search allowClear
                                    placeholder="Search Vehicle "
                                    onChange={this.onLprInputChange}
                                    style={{textAlign: "right"}}
                                    onSearch={value => this.search(value)} enterButton
                            />
                        </Col>
                    </Row>}


                    onTabChange={key => {
                        this.onTabChange(key);
                    }}
                >
                    {this.renderMasterData()}
                    <IncidentTimeline
                        lpr={this.state.timelineLpr}
                        incidentType={this.state.activeTab}
                        visible={this.state.visible}
                        toggleVisible={this.toggleIncidentTimelineModal}
                    />

                </Card>

            </div>
        );
    }

    renderMasterData() {


        let events = this.state.allData.anprresponse.events;
        let count = this.state.allData.anprresponse.totalPages * this.state.allData.anprresponse.pageSizes;

        let {archiveLpr} = this.state;

        const paginationOption = {
            showSizeChanger: true,
            showQuickJumper: true,
            onShowSizeChange: this.onMasterDataPageSizeChange,
            onChange: this.onMasterDataPageChange,
            total: count,
            pageSizeOptions:this.state.pageSizeOptions

        };

        const pagination = {
            ...paginationOption,
            total: count,
            current: this.state.allData.filter.pages,
            pageSizes: this.state.allData.filter.pageSizes
        };

        if (this.state.allData.loading) {
            const antIcon = <Icon type="loading" style={{fontSize: 24}} spin/>;
            return <Spin indicator={antIcon}/>
        }

        if (!this.state.allData.anprresponse.events) {
            return <Empty description={false}/>
        }

        return (
            <div>
                <Table dataSource={events} pagination={pagination}>

                    <Column title="LPR" dataIndex="anprText" key="anprText"
                            render={anprText => <Button onClick={() => this.showModal(anprText)}>{anprText}</Button>}/>

                    <Column title="Incident Count" dataIndex="repeatedTimes" key="repeatedTimes"
                            render={repeatedTimes => repeatedTimes}/>

                    <Column
                        render={(text, record, index) => <div>
                            <p><Button type={'danger'} loading={this.state.archiveEventsLoading === record.anprText}
                                       onClick={() => this.archiveLprOnChange(record.anprText)}>Archive</Button></p>

                        </div>
                        }/>
                </Table>
            </div>

        )
    }

}
