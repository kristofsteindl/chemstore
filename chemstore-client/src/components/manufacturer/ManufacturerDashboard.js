import axios from 'axios'
import React, { Component } from 'react'
import { refreshTokenAndUser } from '../../securityUtils/securityUtils'
import RedirectFormButton from '../RedirectFormButton'
import ManufacturerCard from './ManufacturerCard'

export default class ManufacturerDashboard extends Component {
    constructor() {
        super()
        this.state = {
            manufacturers: [],
            errors: {deleted : {}}
        }
        this.deleteManufacturer=this.deleteManufacturer.bind(this)
    }

    deleteManufacturer(manufacturer) {
        console.log(`Hello ${manufacturer.name}`)
    }

    componentDidMount() {
        refreshTokenAndUser()
        axios.get('/api/lab-admin/manufacturer').then(result => this.setState({manufacturers: result.data}))
    }

    render() {
        return (
            <div className="manufacturers">
                <div className="container">
                    <div className="row"> 
                        <div className="col-md-12">
                            <h1 className="display-4 text-center">Manufacturers</h1>
                            <br />
                            <RedirectFormButton formRoute="/add-manufacturer" buttonLabel="Add Manufacturer"/>
                            <br />
                            <hr />
                            {this.state.manufacturers.map(manufacturer => (
                                <ManufacturerCard manufacturer={manufacturer} 
                                    key={manufacturer.id} deleteManufacturer={this.deleteManufacturer} 
                                    errors={this.state.errors.deleted["id" + manufacturer.id] ? this.state.errors.deleted["id" + manufacturer.id] : {}}
                                />
                            ))
                                
                            }
                           
                        </div>
                    </div>
                </div>
            </div>
        ) 
    }
}

