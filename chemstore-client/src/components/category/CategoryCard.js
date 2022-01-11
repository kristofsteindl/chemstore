import React, { Component } from 'react'
import { Link } from 'react-router-dom'
import { getShelfLife } from '../../utils/durationUtils'

export default class CategoryCard extends Component {
    constructor(props) {
        super()
        this.state = {
            errors: {}
        }
        this.deleteCategory = props.deleteCategory
    }


    render() {
        const {category} = this.props
        return (
            <div className="container">
                <div className="card card-body bg-light mb-3" style={{padding: "10px"}}>
                    <div className="row" >
                        <div className="col-sm-4">
                            <h4>{category.name}</h4>
                        </div>
                        <div className="col-sm-4">
                            <span>{getShelfLife(category)}</span>
                        </div>
                        <div className="col-sm-2">
                            { this.props.isAdmin && (
                                <Link to={`/update-category/${category.id}`}>
                                    <li className="list-group-item update">
                                        <i className="fa fa-edit pr-1">Update</i>
                                    </li>
                                </Link>) 
                            }
                        </div>
                        <div className="col-sm-2">
                            { this.props.isAdmin && (
                                <span onClick={() => this.deleteNamedEntity(category)}>
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

    
