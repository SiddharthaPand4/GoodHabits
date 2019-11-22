import React, {Component} from "react";
import {Col, Row, Statistic,TimePicker,DatePicker,Button,Icon,message,Card,Menu, Dropdown,Select} from "antd";
import DashboardService from "../services/DashboardService";
import moment from 'moment';
import classnames from 'classnames';
import {Bar, Pie,Line} from 'react-chartjs-2';
import * as name from "chartjs-plugin-colorschemes";

const { RangePicker } = DatePicker;

const { Option } = Select;

export default class HomeView extends Component {

constructor(props) {
    super(props);
    this.state = {
        loading: true,
        videoVisible: false,
        layout: "table",
        incidents: {},
        filter: {
            filterType:"today"

        },
        resultSetByDate: {
            loading: true,
            chartData: {}
        },

        resultSetByTime: {
            loading: true,
            chartData: {}
        },
        aggregation:"",
        isOpenDatePicker:false
    };

    this.refresh = this.refresh.bind(this);
    this.onChange = this.onChange.bind(this);
    this.onOk = this.onOk.bind(this);
    this.fetchDateWiseVehiclesCount = this.fetchDateWiseVehiclesCount.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.openDatePicker = this.openDatePicker.bind(this);

}
   componentDidMount() {
      this.refresh();
   }


   refresh(){
       this.fetchDateWiseVehiclesCount();
   }


   fetchDateWiseVehiclesCount(){

        DashboardService.getTotalNoOfVehiclesBetweenTwoDates(this.state.filter).then(response => {
            let keys=[];
            let values=[];
            for(var i=0;i<response.data.length;i++){
                keys.push(response.data[i].key);
                values.push(response.data[i].countOfTotalVehicles);
            }

              let resultSet = {
                 loading: false,
                 chartData: {
                     labels: keys,
                     datasets: [{
                         label: keys,
                         data: values
                     }]
                 }
              };
             this.setState({resultSetByDate: resultSet,isOpenDatePicker:false});
        },
        error=>{
            message.error(error.response.data.message);
        })
   }

   handleChange(value){
        let filter = this.state.filter;
        filter.filterType=value;
        this.setState({aggregation:value,filter:filter,isOpenDatePicker:false})
        if(value!="custom"){
            this.fetchDateWiseVehiclesCount();
        }
        if(value=="custom"){
            this.setState({isOpenDatePicker:true})
        }
   }

   openDatePicker(){
    this.setState({isOpenDatePicker:true})
   }

    onChange(value, dateString) {
     console.log('Selected Time: ', value);
     console.log('Formatted Selected Time: ', dateString);
     let filter = this.state.filter;
     filter.from=value[0];
     filter.to=value[1];

    }

    onOk(value) {
     console.log('onOk: ', value);
     this.setState({isOpenDatePicker:false})
     this.fetchDateWiseVehiclesCount()
    }


    render() {
      const onChange=this.onChange;

        return (
            <div>
                <Row gutter={24}>
                    <Col span={12}>
                        <Statistic title="Incidents (This Week)" value={24} />
                    </Col>
                    <Col span={12}>
                        <Statistic title="Incidents (Last Week)" value={34} />
                    </Col>
                </Row>

                <Row gutter={16}>

                    <Col span={16}>
                        <Card>


                                <Select defaultValue="today" style={{ width: 120 }} onChange={this.handleChange}>
                                      <Option value="yesterday">Yesterday</Option>
                                      <Option value="today">Today</Option>
                                      <Option value="last7days">
                                        Last 7 days
                                      </Option>
                                      <Option value="last3months">Last 3 months</Option>
                                      <Option value="last6months">Last 6 months</Option>
                                      <Option value="custom" onClick={this.openDatePicker}>Custom</Option>
                                </Select>

                                &nbsp; &nbsp;

                                 {this.state.isOpenDatePicker ? <RangePicker open={this.state.isOpenDatePicker}
                                       ranges={{
                                         'This Month': [moment().startOf('month'), moment().endOf('month')],
                                         'This Week': [moment().startOf('week'), moment().endOf('week')],
                                       }}
                                       defaultValue={[moment().startOf('week'), moment().endOf('week')]}
                                       format="YYYY/MM/DD"
                                       onChange={onChange}
                                       onOk={this.onOk}
                                       showTime
                                     /> :null}
                                     &nbsp; &nbsp;



                        {!this.state.resultSetByDate.loading ?
                            <Bar data={this.state.resultSetByDate.chartData} options={{
                                          title: {
                                              display: true,
                                              text: 'Vehicles'
                                          },
                                          options: {
                                            maintainAspectRatio : false,
                                            responsive: true,
                                          },
                                          legend: {
                                              display: false
                                          }, scales: {
                                              yAxes: [{
                                                  ticks: {
                                                      beginAtZero: true
                                                  },
                                                  scaleLabel: {
                                                      display: true,
                                                      labelString: 'Total no of vehicles'
                                                  }
                                              }],
                                              xAxes: [{
                                                  scaleLabel: {
                                                      display: true,
                                                      labelString: 'Date'
                                                  }
                                              }]
                                          },
                                          tooltips: {
                                              callbacks: {
                                                  title: function (tooltipItem, chartData) {
                                                      return "Total vehicles enters"
                                                  },
                                                  label: function (tooltipItems, data) {
                                                      return data.datasets[tooltipItems.datasetIndex].label[tooltipItems.index] + " : $" + data.datasets[tooltipItems.datasetIndex].data[tooltipItems.index]
                                                  }
                                              }
                                          },
                                          plugins: {
                                             colorschemes: {
                                                  scheme: 'brewer.Paired12'
                                             }
                                          }
                                   }}/>

                               :null}
                        </Card>

                    </Col>
                </Row>
            </div>
        )
    }
}