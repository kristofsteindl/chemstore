import axios from 'axios'
import React, { Component } from 'react'
import { refreshTokenAndUser } from '../../securityUtils/securityUtils'
import RedirectFormButton from '../RedirectFormButton'
import User from './User'

class UserDashboard extends Component {
    constructor() {
        super()
        this.state = {
            users: []
        }
    }

    componentDidMount() {
        refreshTokenAndUser()
        axios.get('/api/account/user').then((results) => this.setState({ users: results.data }));
    }

    render() {
        return (
            <div className="users">
                <div className="container">
                    <div className="row">
                        <div className="col-md-12">
                            <h1 className="display-4 text-center">Users</h1>
                            <br />
                            <RedirectFormButton formRoute="/add-user" buttonLabel="Add User"/>
                            <br />
                            <hr />
                            {this.state.users.map(user => (
                                <User key={user.id } user={user}/>
                            ))
                                
                            }
                           
                        </div>
                    </div>
                </div>
            </div>
        )   
    }
}

export default UserDashboard