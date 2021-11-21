import axios from 'axios'
import React, { Component } from 'react'
import { refreshTokenAndUser } from '../../securityUtils/securityUtils'
import RedirectFormButton from '../RedirectFormButton'
import LabCard from './LabCard'

export default class LabDashboard extends Component {
    constructor() {
        super()
        this.state = {
            labs: [],
            errors: {deleted : {}}
        }
        this.deleteLab=this.deleteLab.bind(this)
    }


    async deleteLab(lab) {
        const id = lab.id
        if (window.confirm(`Are you sure you want to delete \'${lab.name}\' (${lab.key})?`)) {
            try {
                await axios.delete(`/api/account/lab/${id}`)
                const refreshedLabs = this.state.labs.filter(labFromList => labFromList.id !== id)
                this.setState({labs: refreshedLabs})
            } catch (error) {
                this.setState({ errors: {deleted: {["id" + id]: error.response.data}}})
            }

        }
    }

    componentDidMount() {
        refreshTokenAndUser()
        axios.get('/api/account/lab').then(result => this.setState({labs: result.data}))
    }

    render() {
        return (
            <div className="labs">
                <div className="container">
                    <div className="row"> 
                        <div className="col-md-12">
                            <h1 className="display-4 text-center">Labs</h1>
                            <p className="lead text-center">Manage labs (craete, delete, modify) as account admin</p>
                            <br />
                            <RedirectFormButton formRoute="/add-lab" buttonLabel="Add Lab"/>
                            <br />
                            <hr />
                            {this.state.labs.map(lab => (
                                <LabCard 
                                    lab={lab} 
                                    key={lab.id} 
                                    deleteLab={this.deleteLab}
                                    errors={this.state.errors.deleted["id" + lab.id] ? this.state.errors.deleted["id" + lab.id] : {}}
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

