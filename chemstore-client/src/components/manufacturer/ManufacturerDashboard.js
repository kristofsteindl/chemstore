import axios from 'axios'
import React, { Component } from 'react'
import { refreshTokenAndUser } from '../../utils/securityUtils'
import RedirectFormButton from '../RedirectFormButton'
import NamedEntityCard from '../NamedEntityCard'
import { connect } from 'react-redux';

class ManufacturerDashboard extends Component {
    constructor() {
        super()
        this.state = {
            manufacturers: [],
            errors: {deleted : {}}
        }
        this.deleteManufacturer=this.deleteManufacturer.bind(this)
    }


    async deleteManufacturer(manufacturer) {
        const id = manufacturer.id
        if (window.confirm(`Are you sure you want to delete \'${manufacturer.name}\'?`)) {
            try {
                await axios.delete(`/api/lab-admin/manufacturer/${id}`)
                const refreshedMf = this.state.manufacturers.filter(mfFromList => mfFromList.id !== id)
                this.setState({manufacturers: refreshedMf})
            } catch (error) {
                this.setState({ errors: {deleted: {["id" + id]: error.response.data}}})
            }

        }
    }

    componentDidMount() {
        refreshTokenAndUser()
        axios.get('/api/logged-in/manufacturer').then(result => this.setState({manufacturers: result.data}))
    }

    render() {
        const isAdmin = this.props.user.authorities.some(listItem => listItem.authority === "LAB_MANAGER" || listItem.authority === "LAB_ADMIN")
        return (
            <div className="manufacturers">
                <div className="container">
                    <div className="row"> 
                        <div className="col-md-12">
                            <h1 className="display-4 text-center">Manufacturers</h1>
                            <br />
                            {isAdmin &&
                                (<RedirectFormButton formRoute="/add-manufacturer" buttonLabel="Add Manufacturer"/>)
                            }
                            <br />
                            <hr />
                            {this.state.manufacturers.map(manufacturer => (
                                <NamedEntityCard 
                                    isAdmin={isAdmin}
                                    namedEntity={manufacturer} 
                                    updateUrl="/update-manufacturer"
                                    key={manufacturer.id} deleteNamedEntity={this.deleteManufacturer} 
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


const mapStateToProps = state => ({
    user: state.security.user
})

export default connect(mapStateToProps) (ManufacturerDashboard)

