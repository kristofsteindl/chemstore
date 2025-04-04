import axios from 'axios'
import React, { Component } from 'react'
import { connect } from 'react-redux'
import { checkIfAccountManager, refreshState } from '../../utils/securityUtils'
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

    async deleteUser(user) {
        const id = user.id
        if (window.confirm(`Are you sure yout want to delete ${user.fullName} (${user.username})?`)) {
            try {
                await axios.delete(`/api/account/user/${id}`)
                const refreshedUsers = this.state.users.filter(userFromList => userFromList.id !== id)
                this.setState({users: refreshedUsers})
            } catch(error) {
                this.setState({ errors: {deleted: {["id" + id]: error.response.data}}})
            }
        }  
       
    }

    componentDidMount() {
        refreshState()
        axios.get('/api/logged-in/user').then((results) => this.setState({ users: results.data }));
    } 

    render() {
        const isAccountManager = checkIfAccountManager(this.props.user)
        return (
            <div className="users">
                <div className="container">
                    <div className="row"> 
                        <div className="col-md-12">
                            <h1 className="display-4 text-center">Users</h1>
                            <br />
                            { isAccountManager &&
                                ( <RedirectFormButton formRoute="/add-user" buttonLabel="Add User"/>)
                            }
                           
                            <br />
                            <hr />
                            {this.state.users.map(user => (
                                <User 
                                    isAccountManager={isAccountManager}
                                    key={user.id } 
                                    user={user} 
                                    deleteUser={this.deleteUser} 
                                    errors={this.state.errors.deleted["id" + user.id] ? this.state.errors.deleted["id" + user.id] : {}}/>
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

export default connect(mapStateToProps) (UserDashboard)