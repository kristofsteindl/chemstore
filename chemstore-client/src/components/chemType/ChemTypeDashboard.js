import axios from 'axios'
import React, { Component } from 'react'
import { refreshTokenAndUser } from '../../securityUtils/securityUtils'
import NamedEntityCard from '../NamedEntityCard'
import RedirectFormButton from '../RedirectFormButton'

export default class ChemTypeDashboard extends Component {
    constructor() {
        super()
        this.state = {
            chemTypes: [],
            errors: {deleted : {}}
        }
        this.deleteChemType=this.deleteChemType.bind(this)
    }


    async deleteChemType(chemType) {
        const id = chemType.id
        if (window.confirm(`Are you sure you want to delete ${chemType.name}?`)) {
            try {
                await axios.delete(`/api/lab-manager/chem-type/${id}`)
                const refreshedCts = this.state.chemTypes.filter(ctFromList => ctFromList.id !== id)
                this.setState({chemTypes: refreshedCts})
            } catch (error) {
                this.setState({ errors: {deleted: {["id" + id]: error.response.data}}})
            }

        }
        console.log(`Hello ${chemType.name}`)
    }

    componentDidMount() {
        refreshTokenAndUser()
        axios.get('/api/lab-manager/chem-type').then(result => this.setState({chemTypes: result.data}))
    }

    render() {
        return (
            <div className="chem-types">
                <div className="container">
                    <div className="row"> 
                        <div className="col-md-12">
                            <h1 className="display-4 text-center">Chem Types</h1>
                            <p className="lead text-center">Categories of chemicals. With the help of these categories, you can specify shelf lifes for different categories for different lab</p>
                            <br />
                            <RedirectFormButton formRoute="/add-chem-type" buttonLabel="Add Chem Type"/>
                            <br />
                            <hr />
                            {this.state.chemTypes.map(chemType => (
                                <NamedEntityCard 
                                    namedEntity={chemType} 
                                    key={chemType.id} deleteNamedEntity={this.deleteChemType} 
                                    updateUrl="/update-chem-type"
                                    errors={this.state.errors.deleted["id" + chemType.id] ? this.state.errors.deleted["id" + chemType.id] : {}}
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

