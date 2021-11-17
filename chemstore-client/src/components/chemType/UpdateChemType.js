import axios from 'axios'
import classNames from 'classnames'
import React, { Component } from 'react'
import { checkExpiry } from '../../securityUtils/securityUtils'

export default class UpdateChemType extends Component {
    constructor() {
        super()
        this.state = {
            name: "",
            errors: {}
        }
        this.onChange = this.onChange.bind(this)
        this.onSubmit = this.onSubmit.bind(this)
    }


    componentDidMount() {
        axios.get(`/api/lab-manager/chem-type/${this.props.match.params.id}`).then(result => this.setState({name: result.data.name} ))
    }

    onChange(e) {
        this.setState({[e.target.name]: e.target.value})
    }

    async onSubmit(e) {
        checkExpiry()
        e.preventDefault()
        const input = {name: this.state.name}
        try {
            await axios.put(`/api/lab-manager/chem-type/${this.props.match.params.id}`, input)
            this.props.history.push("/chem-types")
        } catch(error) {
            this.setState({errors: error.response.data})
        }
    }
    render() {
        const {errors} = this.state
        return (
            <div className="update-chem-type">
                <div className="container">
                    <div className="row">
                    
                        <div className="col-md-8 m-auto">
                            <h1 className="display-4 text-center">Update chem type</h1>
                            <p className="lead text-center">Modify a chem type (for the whole account/company)</p>
                            <br/>
                            {
                                (errors.message && <h5 className="invalid-input">{errors.message}</h5>)
                            }
                            {
                                (errors.message && 
                                <div className="form-group row mb-3 invalid-feedback">
                                    {errors.message}
                                </div>)
                            }
                            <form onSubmit={this.onSubmit}>
                                <div className="form-group row mb-3">
                                    <label htmlFor="username" className="col-sm-4 col-form-label">name</label>
                                    <div className="col-sm-8">
                                        <input 
                                            name="name"
                                            value={this.state.name}
                                            onChange={this.onChange}
                                            type="text" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.name})} 
                                            placeholder="name" 
                                        />
                                        {
                                            (errors.name && <div className="invalid-feedback">{errors.name}</div>)
                                        }
                                       
                                    </div>
                                </div>
                                <button type="submit" className="btn btn-info btn-block mt-4">Add</button>
                                
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
