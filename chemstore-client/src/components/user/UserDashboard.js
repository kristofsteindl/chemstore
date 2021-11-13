import axios from 'axios'
import React, { Component } from 'react'
import { refreshTokenAndUser } from '../../securityUtils/securityUtils'
import RedirectFormButton from '../RedirectFormButton'
import User from './User'

class UserDashboard extends Component {
    constructor() {
        super()
        this.state = {
            users: [],
            errors: {deleted:{}}
        }
        this.deleteUser=this.deleteUser.bind(this)
    }

    async deleteUser(id) {
        try {
            await axios.delete(`/api/account/user/${id}`)
            const refreshedUsers = this.state.users.filter(user => user.id !== id)
            this.setState({users: refreshedUsers})
        } catch(error) {
            console.log("in UserDashboard " + error)
            this.setState({ errors: {deleted: {["id" + id]: error.response.data}}})
        }

        
       
    }

    componentDidMount() {
        refreshTokenAndUser()
        axios.get('/api/account/user').then((results) => this.setState({ users: results.data }));
    }

    render() {
        const errors = this.state.errors
        console.log("in dashboard render " + errors.deleted.id13)
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
                                <User key={user.id } user={user} deleteUser={this.deleteUser} errors={errors.deleted[user.id] ? errors.deleted[user.id] : {"message":"bar"}}/>
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