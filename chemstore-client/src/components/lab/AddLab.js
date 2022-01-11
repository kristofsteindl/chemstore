import React, { Component } from 'react'
import classNames from "classnames";
import axios from 'axios';
import { checkExpiry } from '../../utils/securityUtils';
import Multiselect from 'multiselect-react-dropdown';


export default class AddLab extends Component {
    constructor() {
        super()
        this.state = {
            key: "",
            name: "",
            labManagerUsernames: [],
            users: [],
            errors: {}
        }
        this.onChange = this.onChange.bind(this)
        this.onSubmit = this.onSubmit.bind(this)
        this.managersOfLabMultiselect = React.createRef();
    }

    onChange(e) {
        this.setState({[e.target.name]: e.target.value})
    }

    componentDidMount() {
        checkExpiry()
        axios.get('/api/account/user').then(result => this.setState({users: result.data}))
    }

    async onSubmit(e) {
        checkExpiry()
        e.preventDefault()
        const input = {
            key: this.state.key,
            name: this.state.name,
            labManagerUsernames: this.managersOfLabMultiselect.current.getSelectedItems().map(user => user.username)
        }
        try {
            await axios.post('/api/account/lab', input)
            this.props.history.push("/labs")
        } catch(error) {
            this.setState({errors: error.response.data})
        }
    }

    render() {
        const {errors} = this.state
        return (
            <div className="add-lab">
                <div className="container">
                    <div className="row">                 
                        <div className="col-md-8 m-auto">

                    
                            <h1 className="display-4 text-center">Add Lab</h1>
                            <p className="lead text-center">Create a lab</p>
                            <br/>
                            {
                                (errors.message && 
                                <div className="form-group row mb-3 invalid-feedback">
                                    {errors.message}
                                </div>)
                            }
                            <form onSubmit={this.onSubmit}>
                                <div className="form-group row mb-3">
                                    <label htmlFor="key" className="col-sm-4 col-form-label">key</label>
                                    <div className="col-sm-8">
                                        <input 
                                            name="key"
                                            value={this.state.key}
                                            onChange={this.onChange}
                                            type="text" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.key})} 
                                            placeholder="key (eg: compound-lab)" 
                                        />
                                        {
                                            (errors.key && <div className="invalid-feedback">{errors.key}</div>)
                                        }
                                       
                                    </div>
                                </div>
                                <div className="form-group row mb-3">
                                    <label htmlFor="name" className="col-sm-4 col-form-label">name</label>
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
                                <div className="form-group row mb-3">
                                    <label htmlFor="managers" className="col-sm-4 col-form-label">Managers of lab</label>
                                    <div className="col-sm-8">
                                        <Multiselect
                                            displayValue="fullName"
                                            placeholder='managers of lab'
                                            onSearch={function noRefCheck(){}}
                                            closeOnSelect={false}
                                            style={{searchBox: {"fontSize": "20px"}}}
                                            options={this.state.users}
                                            ref={this.managersOfLabMultiselect}
                                            showCheckbox
                                        />
                                        </div>
                                </div>

                            
                                
                                <button type="submit" className="btn btn-info btn-block mt-4">Add Lab</button>
                                
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
