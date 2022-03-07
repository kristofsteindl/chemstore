import React, { Component } from 'react'
import DuButtons from '../UI/DuButtons';

class User extends Component {
    constructor(props) {
        super()
        this.deleteUser=props.deleteUser
    }

    render() {
        const {user} = this.props
        return (
            <div className="container">
                <div className="card card-body bg-light mb-3">
                    <div className="row">
                        <div className="col-3">
                            <span className="mx-auto">{user.username}</span>          
                                 { user.roles.length > 0 && (<div>
                                        <br />
                                        <span className="mx-auto">({user.roles.map(role => role.name).join(",")})</span>
                                    </div>)
                                 }
                        </div>
                        <div className="col-lg-6">
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
                        <div className="col-md-3">
                            { this.props.isAccountManager && 
                                <DuButtons 
                                    updateFormTo={`/update-user/${user.id}`}
                                    onDelete={() => this.deleteUser(user)}
                                /> 
                            }                     
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default User 
