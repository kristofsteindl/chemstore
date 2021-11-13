import React, { Component } from 'react'
import { Link } from 'react-router-dom'

export default class ManufacturerCard extends Component {
    constructor(props) {
        super()
        this.state = {
            errors: {}
        }
        this.deleteManufacturer = props.deleteManufacturer
    }

    /**/

    render() {
        const {manufacturer} = this.props
        return (
            <div className="container">

                <div className="card card-body bg-light mb-3">
                    <div className="row">
                        <div className="col-md-8">
                            <h4>{manufacturer.name}</h4>
                        </div>
                        <div className="col-md-4">
                                <Link to={`/update-manufacturer/${manufacturer.id}`}>
                                    <li className="list-group-item update">
                                        <i className="fa fa-edit pr-1">Update Manufacturer</i>
                                    </li>
                                </Link>
                                <span onClick={() => this.deleteManufacturer(manufacturer)}>
                                    <li className="list-group-item delete">
                                        <i className="fa fa-minus-circle pr-1">Delete Manufacturer</i>
                                        {
                                            (this.props.errors.message && <h5 >{this.props.errors.message}</h5>)
                                        }
                                    </li>
                                </span> 
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
