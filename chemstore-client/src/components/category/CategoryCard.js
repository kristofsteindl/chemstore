import React, { Component } from 'react'
import { getShelfLife } from '../../utils/durationUtils'
import DuButtons from '../UI/DuButtons'

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
                        <div className="col-sm-5">
                            <span>{getShelfLife(category)}</span>
                        </div>
                        <div className="col-sm-3">
                            { this.props.isAdmin && 
                                <DuButtons 
                                    updateFormTo={`/update-category/${category.id}`}
                                    onDelete={() => this.deleteCategory(category)}
                                /> 
                               
                            }
                        </div>
                    </div>
                </div>
            </div>
        )
    }

}

    
