import React, { Component } from 'react'
import { Link } from 'react-router-dom'

export default class NamedEntityCard extends Component {
    constructor(props) {
        super()
        this.state = {
            errors: {}
        }
        this.deleteNamedEntity = props.deleteNamedEntity
    }


    render() {
        const {namedEntity} = this.props
        return (
            <div className="container">
                <div className="card card-body bg-light mb-3" style={{padding: "10px"}}>
                    <div className="row" >
                        <div className="col-sm-8">
                        <h4>{namedEntity.name}</h4>
                        </div>
                        <div className="col-sm-2">
                            <Link to={`${this.props.updateUrl}/${namedEntity.id}`}>
                                <li className="list-group-item update">
                                    <i className="fa fa-edit pr-1">Update</i>
                                </li>
                            </Link>
                        </div>
                        <div className="col-sm-2">
                            <span onClick={() => this.deleteNamedEntity(namedEntity)}>
                                <li className="list-group-item delete">
                                    <i className="fa fa-minus-circle pr-1">Delete</i>
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
