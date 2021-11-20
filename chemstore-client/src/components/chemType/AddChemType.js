import React, { Component } from 'react'
import classNames from "classnames";
import axios from 'axios';

export default class AddChemType extends Component {
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
        const newChemType = {name: this.state.name}
        try {
            await axios.post('/api/lab-manager/chem-type', newChemType)
            this.props.history.push("/chem-types")
        } catch(error) {
            this.setState({errors: error.response.data})
        }
    }

    render() {
        const {errors} = this.state
        return (
            <div className="add-chem-type">
                <div className="container">
                    <div className="row">
                    
                        <div className="col-md-8 m-auto">
                            <h1 className="display-4 text-center">Add Chemical category</h1>
                            <p className="lead text-center">Create a chemical category (for the whole account/company)</p>
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
                                            placeholder="chem type name (e.g. salt)" 
                                        />
                                        {
                                            (errors.name && <div className="invalid-feedback">{errors.name}</div>)
                                        }
                                       
                                    </div>
                                </div>
                                <button type="submit" className="btn btn-info btn-block mt-4">Add Chem Type</button>
                                
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
