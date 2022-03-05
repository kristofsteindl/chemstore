const UserManual = () => {
    return (
        <div>
            <h1>User Manual</h1>
            <br/>
            <br/>
            <h2>Purpose</h2>
            <p>
                The purpose of this web application is to ease the burden of the registration and administration of chemicals and products made from them.
                While it provide a easy to use user interface, it complies the highest industrial quality assurance standards.
                It implements a fine grained authorization and a detailed audit logging (in progress).   
             </p>
             <br/>
             <br/>
            <h2>Roles</h2>
            <br/>

            <h3>User</h3>
            <br/>
            <p>
                A user is typically a techician, whose daily work is to use the chemicals and make products (eg eluents) out of it, 
                without additional admin role
            </p>
            <p>
                A user can see the every laboratory and user in the system (account), but he/she cannot modify these. 
            </p>
            <p>
                A user can be assigned to one or more laboratories (labs) by the Account Manager (see below). He/she can open and consume chemicals (chem items) in these labs.
                A user can see the chemicals and the chemical categories in each assigned lab, but cannot modify these.
            </p>
            <p>
                A user can choose from a top-right dropdown, which laboratory he/she would like to act.
                Eg. in which lab he/she would like to open a certain chemical
            </p>
            <p>
                A user can change its password, and can log out
            </p>





            <h3>Lab Admin</h3>
            <br/>
            <p>
                The lab admin is typically a employee, whose responsibility is to administrate the the everyday life of the laborytory in the application. 
                He/she registers the shipped chemicals, and administrates the related records. 
                A lab admin has all permissions as the user, plus some extra detailed below. Only these extra permissions are listed below.
                
            </p>
            
            <p>
                A lab admin can be assigned to one or more laboratories (labs) by the Account Manager. 
                If an employee is assigned to a lab as lab admin, it is not required to assign him/her as user to the same lab. 
                He/she get the 'user' permission by default.
                Besides opening and consuming chemicals (chem items), he/she can add new chem items to the lab
            </p>
            <p>A lab admin can add, modify and delete (archive) chemicals and chemical categories, for the administrating lab.</p>
            <p>A lab admin can add, modify and delete (archive) manufactures throughout the whole system (account)</p>
            
            
            
            <h3>Lab Manager</h3>
            <br/>
            <p>
                The lab manager is typically the leader or the deputy of the laboratory, or the some kind of responsible person.
                It is usually the lab manager, who responsible the whole laboratory, especially in the aspect of quality assurance.
                Only the lab manager has the authority to perform quality assurance critical modifications (eg deleting a chem item). 
                Of course these modifications are registered to the unmodifiable audit log (see below). 
                
            </p>
            <p>
                A lab manager can be assigned to one or more laboratories (labs) by the Account Manager.
                It is advisable to have more lab managers for one laboratory!
                If an employee is assigned to a lab as lab manager, it is not required to assign him/her as user to the same lab. 
                He/she get the 'user' permission by default.
            </p>
            <p>The lab manager can modify (in progress) and delete chem items from the system (hard delete)</p>
            <p>The lab manager can assign employees to the lab as 'users' or 'lab admins' (in progess)</p>


            <h3>Account Manager</h3>
            <br/>
            <p>
                The account manager is an employee, who is responsible the application in a high level. 
                He/she registers laboratories, users etc, but he/she not necessary an employee of the lab. 
                For example account manager can be an IT system admin, or a higher level leader or quality assurer.

            </p>
            <p>
                Account admin can create, modify and delete labs and users. 
                He/she can assign the employees to the labs as users and/or lab admins. He/she can modify the managers of the labs.
            </p>
            <p>
                He/she can restore the password of the users
            </p>
        </div>
        
        
    )
}

export default UserManual