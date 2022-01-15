import axios from 'axios'
import React, { Component } from 'react'
import { connect } from 'react-redux'
import { refreshState } from '../../utils/securityUtils'
import RedirectFormButton from '../RedirectFormButton'
import LabCard from './LabCard'

class LabDashboard extends Component {
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
        if (window.confirm(`Are you sure you want to delete '${lab.name}' (${lab.key})?`)) {
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
        refreshState()
        axios.get('/api/logged-in/lab').then(result => this.setState({labs: result.data}))
    }

    render() {
        const isAccountManager = this.props.user.authorities.some(listItem => listItem.authority === "ACCOUNT_MANAGER")
        return (
            <div className="labs">
                <div className="container">
                    <div className="row"> 
                        <div className="col-md-12">
                            <h1 className="display-4 text-center">Labs</h1>
                            <p className="lead text-center">Manage labs (create, delete, modify) as account admin</p>
                            <br />
                            {isAccountManager &&
                                (<RedirectFormButton formRoute="/add-lab" buttonLabel="Add Lab"/>)
                            }
                            <br />
                            <hr />
                            {this.state.labs.map(lab => (
                                <LabCard 
                                    isAccountManager={isAccountManager}
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

const mapStateToProps = state => ({
    user: state.security.user
})

export default connect(mapStateToProps) (LabDashboard)

