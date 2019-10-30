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
            from: moment().startOf('week'),
            to: moment().endOf('week'),
            selectedDate: moment(),
        },
        resultSetByDate: {
            loading: true,
            chartData: {}
        },

        resultSetByTime: {
            loading: true,
            chartData: {}
        },
        aggregation:""
    };

    this.refresh = this.refresh.bind(this);
    this.onFromDateChange = this.onFromDateChange.bind(this);
    this.onFromTimeChange = this.onFromTimeChange.bind(this);
    this.onToDateChange = this.onToDateChange.bind(this);
    this.onToTimeChange = this.onToTimeChange.bind(this);
    this.onChange = this.onChange.bind(this);
    this.onOk = this.onOk.bind(this);
    this.fetchDateWiseVehiclesCount = this.fetchDateWiseVehiclesCount.bind(this);
    this.fetchTimelyVehiclesCount = this.fetchTimelyVehiclesCount.bind(this);
    this.handleChange = this.handleChange.bind(this);

}
componentDidMount() {
   this.refresh();
}


refresh(){
    this.fetchTimelyVehiclesCount();
    //this.fetchDateWiseVehiclesCount();
}

fetchTimelyVehiclesCount(){
    DashboardService.getTotalNoOfVehiclesBySelectedDate(this.state.filter).then(response => {

        let keys=[];
        let values=[];
        let datasets=[];
        let labels=[];

       //for(var i=0;i<response.data.length;i++){
       //    keys.push(response.data[i].key);
       //    values.push(response.data[i].countOfTotalVehicles);
       //}

        Object.keys(response.data).map(function(dates, data) {
             var res=response.data[dates];
             Object.keys(res).map(function(time, count){
                 datasets.push({label:res[time].key, data:res[time].countOfTotalVehicles})
                 labels.push(res[time].key);
             });

        });

          let resultSet = {
             loading: false,
             chartData: {
                 labels: labels,
                 datasets: datasets
             }
          };

         this.setState({resultSetByDate: resultSet});
    },
    error=>{
        message.error(error.response.data.message);
    })
   }

   fetchDateWiseVehiclesCount(){
     if(this.state.aggregation=="hourly"){
        this.fetchTimelyVehiclesCount();
     }
     else{
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
             this.setState({resultSetByDate: resultSet});
        },
        error=>{
            message.error(error.response.data.message);
        })
     }
   }

   handleChange(value){
        this.setState({aggregation:value})
   }

onFromDateChange(date) {
    let filter = this.state.filter;
    if(date!=null){
      filter.selectedDate = date.format("YYYY-MM-DD");
    }
    else{
        filter.selectedDate=null;
    }
    this.setState({filter: filter});
}
onFromTimeChange(time) {
    let filter = this.state.filter;
    if(time!=null){
      filter.fromTime = time.format("HH:mm:ss");
    }
    else{
        filter.fromTime=null;
    }
    this.setState({filter: filter});
}
onToDateChange(date) {
    let filter = this.state.filter;
    if(date!=null){
      filter.toDate = date.format("YYYY-MM-DD");
    }
    else{
      filter.toDate=null;
    }
    this.setState({filter: filter});
}
onToTimeChange(time) {
    let filter = this.state.filter;
    if(time!=null){
     filter.toTime = time.format("HH:mm:ss");
    }
    else{
      filter.toTime=null;
    }
    this.setState({filter: filter});
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
}


    render() {
      const onChange=this.onChange;
      const menu = (
        <Menu>
          <Menu.Item>
            <a target="_blank" rel="noopener noreferrer">
             Current week
            </a>
          </Menu.Item>
          <Menu.Item>
            <a target="_blank" rel="noopener noreferrer">
              Last Week
            </a>
          </Menu.Item>
          <Menu.Item>
            <a target="_blank" rel="noopener noreferrer">
             Current Month
            </a>
          </Menu.Item>
          <Menu.Item>
            <a target="_blank" rel="noopener noreferrer">
             Last Month
            </a>
          </Menu.Item>

        </Menu>
      );


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

                <Row gutter={24}>

                    <Col span={16}>
                        <Card>
                            <RangePicker
                                  ranges={{
                                    'This Month': [moment().startOf('month'), moment().endOf('month')],
                                    'This Week': [moment().startOf('week'), moment().endOf('week')],
                                  }}
                                  defaultValue={[moment().startOf('week'), moment().endOf('week')]}
                                  format="YYYY/MM/DD"
                                  onChange={onChange}
                                />

                                &nbsp; &nbsp;

                                <Select defaultValue="hourly" style={{ width: 120 }} onChange={this.handleChange}>
                                      <Option value="hourly">Hourly</Option>
                                      <Option value="weekly">Weekly</Option>
                                      <Option value="monthly">
                                        Monthly
                                      </Option>
                                      <Option value="yearly">Yearly</Option>
                                </Select>
                                &nbsp; &nbsp;
                                <Dropdown overlay={menu}>
                                    <a className="ant-dropdown-link" href="#">
                                      Date Range <Icon type="down" />
                                    </a>
                                </Dropdown>
                                 &nbsp; &nbsp;
                            <Button onClick={() => {
                                this.fetchDateWiseVehiclesCount()
                            }}><Icon type="reload"/>Reload</Button>

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
                                                      return data.datasets[tooltipItems.datasetIndex].label[tooltipItems.index] + " : $" +data.datasets[tooltipItems.datasetIndex].data[tooltipItems.index]
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