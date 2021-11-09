import React, { Component } from 'react'
import { Link } from 'react-router-dom'

class User extends Component {
    render() {
        const {user} = this.props
        return (
            <div className="container">
                <div className="card card-body bg-light mb-3">
                    <div className="row">
                        <div className="col-2">
                            <span className="mx-auto">{user.username.split('@')[0]}</span>          
                                 { user.roles.length > 0 && (<div>
                                        <br />
                                        <span className="mx-auto">({user.roles.map(role => role.name).join(",")})</span>
                                    </div>)
                                 }
                        </div>
                        <div className="col-lg-6 col-md-4 col-8">
                            <h3>{user.fullName}</h3>
                            <i>User in:  </i><strong>{user.labsAsUser.map(lab => lab.name).join(", ")}</strong><br />
                            { user.labsAsAdmin.length > 0 && (
                                <div>
                                    <i>Admin in:   </i><strong>{user.labsAsAdmin.map(lab => lab.name).join(",")}</strong><br />
                                </div>)
                            }
                            { user.managedLabs.length > 0 && (
                                <div>
                                    <i>Managed labs:   </i><strong>{user.managedLabs.map(lab => lab.name).join(",")}</strong><br />
                                </div>)
                            }
                            

                        </div>
                        <div className="col-md-4 d-none d-lg-block">
                            <ul className="list-group">
                                <Link to={`/update-user/${user.id}`}>
                                    <li className="list-group-item update">
                                        <i className="fa fa-edit pr-1">Update User</i>
                                    </li>
                                </Link>
                                <a href="">
                                    <li className="list-group-item delete">
                                        <i className="fa fa-minus-circle pr-1">Delete User</i>
                                    </li>
                                </a>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default User 
