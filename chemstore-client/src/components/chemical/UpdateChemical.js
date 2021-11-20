import axios from 'axios'
import classNames from 'classnames'
import React, { Component } from 'react'
import { checkExpiry } from '../../securityUtils/securityUtils'
import Select from 'react-dropdown-select';

export default class UpdateChemical extends Component {
    constructor() {
        super()
        this.state = {
            shortName: "",
            exactName: "",
            chemTypes: [],
            chemType: {name: "", id: -1},
            errors: {}
        }
        this.onChange = this.onChange.bind(this)
        this.onSubmit = this.onSubmit.bind(this)
    }

    onChange(e) {
        this.setState({[e.target.name]: e.target.value})
    }

    componentDidMount() {
        checkExpiry()
        axios.get('/api/lab-manager/chem-type').then(result => this.setState({chemTypes: result.data}))
        axios.get(`/api/lab-admin/chemical/${this.props.match.params.id}`).then(result => this.setState({
            shortName: result.data.shortName,
            exactName: result.data.exactName,
            chemType: result.data.chemType ? result.data.chemType : {name: "", id: -1}}))
    }


    async onSubmit(e) {
        checkExpiry()
        e.preventDefault()
        const updatedChemical = {
            shortName: this.state.shortName,
            exactName: this.state.exactName,
            chemTypeId: this.state.chemType.id
        }
        try {
            await axios.put(`/api/lab-admin/chemical/${this.props.match.params.id}`, updatedChemical)
            this.props.history.push("/chemicals")
        } catch(error) {
            this.setState({errors: error.response.data})
        }
    }
    render() {
        const {errors} = this.state
        return (
            <div className="update-manufacturer">
                <div className="container">
                    <div className="row">
                    
                        <div className="col-md-8 m-auto">
                            <h1 className="display-4 text-center">Update Chemical</h1>
                            <p className="lead text-center">Modify chemical (for the whole account/company)</p>
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
                                    <label htmlFor="shortName" className="col-sm-4 col-form-label">short name</label>
                                    <div className="col-sm-8">
                                        <input 
                                            name="shortName"
                                            value={this.state.shortName}
                                            onChange={this.onChange}
                                            type="text" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.shortName})} 
                                            placeholder="short name" 
                                        />
                                        {
                                            (errors.shortName && <div className="invalid-feedback">{errors.shortName}</div>)
                                        }
                                       
                                    </div>
                                </div>
                                <div className="form-group row mb-3">
                                    <label htmlFor="exactName" className="col-sm-4 col-form-label">exact name</label>
                                    <div className="col-sm-8">
                                        <input 
                                            name="exactName"
                                            value={this.state.exactName}
                                            onChange={this.onChange}
                                            type="text" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.exactName})} 
                                            placeholder="exact name" 
                                        />
                                        {
                                            (errors.exactName && <div className="invalid-feedback">{errors.exactName}</div>)
                                        }
                                       
                                    </div>
                                </div>

                                <div className="form-group row mb-3">
                                    <label htmlFor="exactName" className="col-sm-4 col-form-label">category</label>
                                    <div className="col-sm-8">
                                      <Select
                                            options={this.state.chemTypes}
                                            values={[this.state.chemType]}
                                            labelField="name"
                                            valueField="id"
                                            placeholder="category"
                                            searchable="true"
                                            searchBy="name"
                                            clearable="true"
                                            style={{height: "50px", fontSize: "20px"}}
                                            onChange={(items) => this.setState({chemType: items[0]})}
                                      />
                                        {
                                            (errors.chemType && <div className="invalid-feedback">{errors.chemType}</div>)
                                        }
                                       
                                    </div>
                                </div>
                                
                                <button type="submit" className="btn btn-info btn-block mt-4">Update Chemical</button>
                                
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
