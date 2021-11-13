import React, { Component } from 'react'
import classNames from "classnames";
import axios from 'axios';

export default class AddManufacturer extends Component {
    constructor() {
        super()
        this.state = {
            name: "",
            errors: {}
        }
        this.onChange = this.onChange.bind(this)
        this.onSubmit = this.onSubmit.bind(this)
    }

    onChange(e) {
        this.setState({[e.target.name]: e.target.value})
    }

    async onSubmit(e) {
        e.preventDefault()
        const newManufacturer = {name: this.state.name}
        try {
            await axios.post('/api/lab-admin/manufacturer', newManufacturer)
            this.props.history.push("/manufacturers")
        } catch(error) {
            this.setState({errors: error.response.data})
        }
    }

    render() {
        const {errors} = this.state
        return (
            <div className="add-manufacturer">
                <div className="container">
                    <div className="row">
                    
                        <div className="col-md-8 m-auto">
                            <h1 className="display-4 text-center">Add Manufacturer</h1>
                            <p className="lead text-center">Create a manufacturer (for the whole account/company)</p>
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
                                            placeholder="manufacturer name" 
                                        />
                                        {
                                            (errors.username && <div className="invalid-feedback">{errors.username}</div>)
                                        }
                                       
                                    </div>
                                </div>
                                <button type="submit" className="btn btn-info btn-block mt-4">Add Manufacturer</button>
                                
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
