import axios from 'axios'
import classNames from 'classnames'
import React, { Component } from 'react'

export default class ChangePassword extends Component {
    constructor() {
        super()
        this.state = {
            oldPassword: "",
            newPassword: "",
            newPassword2: "",
            errors: {}
        }
        this.onSubmit = this.onSubmit.bind(this)
        this.onChangeBasicInputs = this.onChangeBasicInputs.bind(this)
    }

    async onSubmit(e) {
        e.preventDefault()
        const passwordInput = {
            oldPassword: this.state.oldPassword,
            newPassword: this.state.newPassword,
            newPassword2: this.state.newPassword2
        }
        try {
            await axios.patch("/api/logged-in/user", passwordInput)
            this.props.history.push("/chem-items")
        } catch (error) {
            this.setState({errors: error.response.data})
        }
    }

    onChangeBasicInputs(e) {
        this.setState({ [e.target.name]: e.target.value})
    }

    render() {
        const {errors} = this.state
        return (
            <div className="change-password">
                <div className="container">
                    <div className="row">
                    
                        <div className="col-md-8 m-auto">
                            <h1 className="display-4 text-center">Change Password</h1>
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
                                    <label htmlFor="oldPassword" className="col-sm-4 col-form-label">Old password</label>
                                    <div className="col-sm-8">
                                        <input 
                                            name="oldPassword"
                                            value={this.state.oldPassword}
                                            onChange={this.onChangeBasicInputs}
                                            type="password" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.oldPassword})} 
                                            placeholder="old password" 
                                        />
                                        {
                                            errors.oldPassword && <div  className="invalid-feedback">{errors.oldPassword}</div>
                                        }
                                    </div>
                                </div>
                                
                                <div className="form-group row mb-3">
                                    <label htmlFor="newPassword" className="col-sm-4 col-form-label">New password</label>
                                    <div className="col-sm-8">
                                        <input 
                                            name="newPassword"
                                            value={this.state.newPassword}
                                            onChange={this.onChangeBasicInputs}
                                            type="password" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.newPassword})} 
                                            placeholder="new password" 
                                        />
                                        {
                                            errors.newPassword && <div  className="invalid-feedback">{errors.newPassword}</div>
                                        }
                                    </div>
                                </div>

                                <div className="form-group row mb-3">
                                    <label htmlFor="newPassword2" className="col-sm-4 col-form-label">New password (again)</label>
                                    <div className="col-sm-8">
                                        <input 
                                            name="newPassword2"
                                            value={this.state.newPassword2}
                                            onChange={this.onChangeBasicInputs}
                                            type="password" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.newPassword2})} 
                                            placeholder="new password (again)" 
                                            
                                        />
                                        {
                                            errors.newPassword2 && <div className="invalid-feedback">{errors.newPassword2}</div>
                                        }
                                    </div>
                                </div>
                                
                                <input type="submit" className="btn btn-info btn-block mt-4" />
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

