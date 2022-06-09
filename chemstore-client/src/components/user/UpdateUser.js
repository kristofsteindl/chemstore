import Multiselect from 'multiselect-react-dropdown'
import React, { Component } from 'react'
import classNames from "classnames";
import axios from "axios";
import { check, checkIfAccountManager } from '../../utils/securityUtils'
import { connect } from 'react-redux';

const getEmptyUser = () => {
    return {
        username: "",
        fullName: "",
        labsAsUser: [{name:"foo"}, {name:"bar"}, {name:"baz"}], 
        labsAsAdmin: [],
        roles: []
    }
}

class UpdateUser extends Component {
    constructor() {
        super()
        this.state = {
            username: "",
            fullName: "",
            labsAsUser: [], 
            labsAsAdmin: [],
            roles: [],
            labs: [],
            persistedUser: getEmptyUser(),
            errors: {}
        }
        this.onChangeBasicInputs=this.onChangeBasicInputs.bind(this)
        this.labsAsUserMultiSelect = React.createRef();
        this.labsAsAdminMultiselect = React.createRef();
        this.rolesMultiselect = React.createRef();
        this.onSubmit=this.onSubmit.bind(this)
    }

    componentDidMount() {
        check()
        if (!checkIfAccountManager(this.props.user)) {
            this.props.history.push("/users")
        } else {
            const id = this.props.match.params.id
            this.setState({
                id: id
            })
            axios.get('/api/logged-in/lab').then((results) => this.setState({ labs: results.data }))
            axios.get('/api/logged-in/role').then((results) => this.setState({ roles: results.data }));
            axios.get(`/api/account/user/${id}`).then((results) => {
                this.setState({ 
                persistedUser: results.data,
                username: results.data.username,
                fullName: results.data.fullName,
            })});
        }

    }

    onChangeBasicInputs(e) {
        check()
        this.setState({ [e.target.name]: e.target.value})
    }

    async onSubmit(e) {
        e.preventDefault()
        const userInput = {
            username: this.state.username,
            fullName: this.state.fullName,
            labKeysAsUser: this.labsAsUserMultiSelect.current.getSelectedItems().map(lab => lab.key), 
            labKeysAsAdmin: this.labsAsAdminMultiselect.current.getSelectedItems().map(lab => lab.key),
            roles: this.rolesMultiselect.current.getSelectedItems().map(role => role.key)
        }
        try {
            await axios.put(`/api/account/user/${this.state.id}`, userInput)
            this.props.history.push("/users")
        } catch (error) {
            this.setState({errors: error.response.data})
        }
    }


    render() {
        const {errors} = this.state
        return (
            <div className="register">
                <div className="container">
                    <div className="row">
                    
                        <div className="col-md-8 m-auto">
                            <h1 className="display-4 text-center">Update User</h1>
                            <p className="lead text-center">Update existing user</p>
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
                                    <label htmlFor="username" className="col-sm-4 col-form-label">username</label>
                                    <div className="col-sm-8">
                                        <input 
                                            name="username"
                                            value={this.state.username}
                                            onChange={this.onChangeBasicInputs}
                                            type="text" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.username})} 
                                            placeholder="username (email)" 
                                            
                                        />
                                        
                                        {
                                            (errors.username && <div className="invalid-feedback">{errors.username}</div>)
                                        }
                                       
                                    </div>    
                                </div>
                                      
                                
                                <div className="form-group row mb-3">
                                    <label htmlFor="fullName" className="col-sm-4 col-form-label">Full name</label>
                                    <div className="col-sm-8">
                                        <input 
                                            name="fullName"
                                            value={this.state.fullName}
                                            onChange={this.onChangeBasicInputs}
                                            type="text" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.fullName})} 
                                            placeholder="full name"  
                                        />
                                    {errors.fullName && (
                                        <div className="invalid-feedback">{errors.fullName}</div>
                                    )}
                                    </div>

                                </div>

                                <div className="form-group row mb-3">
                                    <label htmlFor="labsAsUser" className="col-sm-4 col-form-label">Labs, where user can administrate (open, use, etc) chemicals</label>
                                    <div className="col-sm-8">
                                        <Multiselect
                                            displayValue="name"
                                            placeholder='labs as user'
                                            onSearch={function noRefCheck(){}}
                                            closeOnSelect={false}
                                            style={{searchBox: {"fontSize": "20px"}}}
                                            options={this.state.labs}
                                            selectedValues={this.state.persistedUser.labsAsUser}
                                            ref={this.labsAsUserMultiSelect}
                                            showCheckbox
                                            />
                                        </div>
                                </div>
                                <div className="form-group row mb-3">
                                    <label htmlFor="labsAsAdmin" className="col-sm-4 col-form-label">Labs, where user is administrator</label>
                                    <div className="col-sm-8">
                                        <Multiselect
                                            displayValue="name"
                                            placeholder='admin in labs'
                                            onSearch={function noRefCheck(){}}
                                            closeOnSelect={false}
                                            style={{searchBox: {"fontSize": "20px"}}}
                                            options={this.state.labs}
                                            selectedValues={this.state.persistedUser.labsAsAdmin}
                                            ref={this.labsAsAdminMultiselect}
                                            showCheckbox
                                        />
                                        </div>
                                </div>
                                <div className="form-group row mb-3">
                                    <label htmlFor="roles" className="col-sm-4 col-form-label">Additional roles</label>
                                    <div className="col-sm-8">
                                        <Multiselect
                                            displayValue="name"
                                            placeholder='roles'
                                            onSearch={function noRefCheck(){}}
                                            closeOnSelect={false}
                                            style={{searchBox: {"fontSize": "20px"}}}
                                            options={this.state.roles}
                                            selectedValues={this.state.persistedUser.roles}
                                            ref={this.rolesMultiselect}
                                            showCheckbox
                                        />
                                    </div>
                                </div>
                                <button type="submit" className="d-flex p-2 btn btn-info btn-block mt-4">Update User</button>
                            </form>
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

export default connect(mapStateToProps) (UpdateUser)
