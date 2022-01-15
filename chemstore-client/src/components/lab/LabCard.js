import React, { Component } from 'react'
import { Link } from 'react-router-dom'

export default class LabCard extends Component {
    constructor(props) {
        super()
        this.state = {
            errors: {}
        }
        this.deleteLab = props.deleteLab
    }


    render() {
        const {lab} = this.props
        return (
            <div className="container">
                <div className="card card-body bg-light mb-3" style={{padding: "10px"}}>
                    <div className="row" >
                        <div className="col-2">
                            <span className="mx-auto">{lab.key}</span>
                        </div>
                        <div className="col-sm-6">
                            <h4>{lab.name}</h4>
                            <i>({lab.labManagers.map(manager => manager.fullName).join(",")})</i>
                        </div>
                        <div className="col-sm-2">
                            {this.props.isAccountManager &&
                                (<Link to={`update-lab/${lab.id}`}>
                                    <li className="list-group-item update">
                                    
                                        <i className="fa fa-edit pr-1">Update</i>
                                    </li>
                                </Link>)
                            }
                        </div>
                        <div className="col-sm-2">
                            {this.props.isAccountManager &&
                                (<span onClick={() => this.deleteLab(lab)}>
                                    <li className="list-group-item delete">
                                        <i className="fa fa-minus-circle pr-1">Delete</i>
                                        {
                                            (this.props.errors.message && <h5 >{this.props.errors.message}</h5>)
                                        }
                                    </li>
                                </span>)
                            } 
                        </div>
                    </div>
                </div>
            </div>
        )
    }

}
